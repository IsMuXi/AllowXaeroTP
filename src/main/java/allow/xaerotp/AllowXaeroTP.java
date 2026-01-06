package allow.xaerotp;
import allow.xaerotp.network.FreeTpPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Heightmap;

public class AllowXaeroTP implements ModInitializer {

	@Override
	public void onInitialize() {

		//注册 Payload 类型
		PayloadTypeRegistry.playC2S().register(
				FreeTpPayload.ID,
				FreeTpPayload.CODEC
		);

		//注册接收器
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
									   net.minecraft.util.Identifier dimId,
									   int x, int y, int z) {

		ServerWorld world = player.getServer().getWorld(
				RegistryKey.of(RegistryKeys.WORLD, dimId)
		);

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
	}
}