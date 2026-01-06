package allow.xaerotp;

import allow.xaerotp.network.FreeTpPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllowXaeroTPClient implements ClientModInitializer {

	// 匹配 tp @s x y z
	private static final Pattern TP_PATTERN =
			Pattern.compile("tp\\s+@s\\s+(-?\\d+(?:\\.\\d+)?)\\s+(-?\\d+(?:\\.\\d+)?)\\s+(-?\\d+(?:\\.\\d+)?)");

	@Override
	public void onInitializeClient() {

		ClientSendMessageEvents.ALLOW_COMMAND.register(command -> {

			// Xaero 的传送一定包含 tp @s
			if (!command.contains("tp @s")) {
				return true;
			}

			Matcher matcher = TP_PATTERN.matcher(command);
			if (!matcher.find()) {
				// 不是标准 tp，直接拦掉，防止原命令执行
				return false;
			}

			double x = Double.parseDouble(matcher.group(1));
			double y = Double.parseDouble(matcher.group(2));
			double z = Double.parseDouble(matcher.group(3));

			MinecraftClient client = MinecraftClient.getInstance();
			if (client.player == null || client.world == null) {
				return false;
			}

			Identifier dim = client.world.getRegistryKey().getValue();

			ClientPlayNetworking.send(
					new FreeTpPayload(
							dim,
							(int) Math.floor(x),
							(int) Math.floor(y),
							(int) Math.floor(z)
					)
			);
			return false;
		});
	}
}