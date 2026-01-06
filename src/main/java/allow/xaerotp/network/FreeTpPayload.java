package allow.xaerotp.network;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FreeTpPayload(
        Identifier dimension,
        int x,
        int y,
        int z
) implements CustomPayload {

    public static final Id<FreeTpPayload> ID =
            new Id<>(Identifier.of("modid", "free_tp")); // ← 改这里

    public static final PacketCodec<
            net.minecraft.network.RegistryByteBuf,
            FreeTpPayload
            > CODEC =
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC, FreeTpPayload::dimension,
                    PacketCodecs.INTEGER, FreeTpPayload::x,
                    PacketCodecs.INTEGER, FreeTpPayload::y,
                    PacketCodecs.INTEGER, FreeTpPayload::z,
                    FreeTpPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}