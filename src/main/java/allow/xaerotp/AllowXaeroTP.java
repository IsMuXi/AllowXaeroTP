package allow.xaerotp;
import allow.xaerotp.network.FreeTpPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class AllowXaeroTP implements ModInitializer {
	@Override
	public void onInitialize() {

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

	private static final int COOLDOWN_SECONDS = 30; // ← 可之后接配置文件

	private static void handleTeleport(ServerPlayerEntity player,
									   Identifier dimId,
									   int x, int y, int z) {

		if (TeleportCooldown.shouldIgnore(player.getUuid())) {
			return;
		}
		if (allow.xaerotp.TeleportCooldown.isOnCooldown(
				player.getUuid(),
				COOLDOWN_SECONDS
		)) {
			long remain = allow.xaerotp.TeleportCooldown.getRemaining(
					player.getUuid(),
					COOLDOWN_SECONDS
			);
			player.sendMessage(
					net.minecraft.text.Text.literal(
							"§c传送冷却中，还需 " + remain + " 秒"
					),
					false
			);
			return;
		}

		ServerWorld world = player.getServer().getWorld(
				RegistryKey.of(RegistryKeys.WORLD, dimId)
		);

		if (world == null) {
			player.sendMessage(
					net.minecraft.text.Text.literal("§c目标维度不存在"),
					false
			);
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

		allow.xaerotp.TeleportCooldown.record(player.getUuid());
	}
}