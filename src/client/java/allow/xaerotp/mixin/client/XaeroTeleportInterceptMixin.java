package allow.xaerotp.mixin.client;

import allow.xaerotp.network.FreeTpPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class XaeroTeleportInterceptMixin {

    private static final Pattern TP_PATTERN =
            Pattern.compile("tp\\s+@s\\s+(-?\\d+(?:\\.\\d+)?)\\s+(-?\\d+(?:\\.\\d+)?)\\s+(-?\\d+(?:\\.\\d+)?)");

    @Inject(
            method = "sendCommand",
            at = @At("HEAD"),
            cancellable = true
    )
    private void interceptXaeroTp(
            String command,
            CallbackInfoReturnable<Boolean> cir
    ) {

        if (!command.contains("tp @s")) {
            return;
        }

        Matcher matcher = TP_PATTERN.matcher(command);
        if (!matcher.find()) {
            cir.setReturnValue(false);
            return;
        }

        double x = Double.parseDouble(matcher.group(1));
        double y = Double.parseDouble(matcher.group(2));
        double z = Double.parseDouble(matcher.group(3));

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            cir.setReturnValue(false);
            return;
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
        cir.setReturnValue(false);
    }
}