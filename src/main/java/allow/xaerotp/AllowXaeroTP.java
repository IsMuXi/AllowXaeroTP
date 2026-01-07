package allow.xaerotp;
import allow.xaerotp.config.ConfigManager;
import allow.xaerotp.network.FreeTpPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class AllowXaeroTP implements ModInitializer {
	@Override
	public void onInitialize() {
		ConfigManager.load();

		PayloadTypeRegistry.playC2S().register(
				FreeTpPayload.ID,
				FreeTpPayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(
				FreeTpPayload.ID,
				(payload, context) -> {

					ServerPlayerEntity player = context.player();

					context.server().execute(() ->
							handleTeleport(
									player,
									payload.dimension(),
									payload.x(),
									payload.y(),
									payload.z()
							)
					);
				}
		);
	}
	private static void handleTeleport(ServerPlayerEntity player,
									   Identifier dimId,
									   int x, int y, int z) {

		if (TeleportCooldown.shouldIgnore(player.getUuid())) {
			return;
		}

		if (TeleportCooldown.isOnCooldown(player.getUuid())) {
			if (ConfigManager.CONFIG.send_cooldown_message) {
				long remain = TeleportCooldown.getRemaining(player.getUuid());
				player.sendMessage(
						Text.literal("§c传送冷却中，还需 " + remain + " 秒"),
						false
				);
			}
			return;
		}

		ServerWorld world = player.getServer().getWorld(
				RegistryKey.of(RegistryKeys.WORLD, dimId)
		);

		if (world == null) {
			player.sendMessage(Text.literal("§c目标维度不存在"), false);
			return;
		}

		if (y == 32767) {
			y = world.getTopY(
					Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
					x, z
			);
		}

		player.teleport(
				world,
				x + 0.5,
				y,
				z + 0.5,
				player.getYaw(),
				player.getPitch()
		);

		TeleportCooldown.record(player.getUuid());
	}
}