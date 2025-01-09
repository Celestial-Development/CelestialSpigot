package net.minecraft.server;

import com.kaydeesea.spigot.CelestialSpigot;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class MinecraftPipeline extends ChannelInitializer<SocketChannel>
{
    private final ServerConnection serverConnection;

    public MinecraftPipeline(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        try {
            ChannelConfig config = ch.config();
            config.setOption(ChannelOption.TCP_NODELAY, CelestialSpigot.INSTANCE.getConfig().isTcpNoDelay());
            config.setOption(ChannelOption.TCP_FASTOPEN, 1);
            config.setOption(ChannelOption.TCP_FASTOPEN_CONNECT, true);
            config.setOption(ChannelOption.IP_TOS, 0x18); // [Nacho-0027] :: Optimize networking
            config.setAllocator(ByteBufAllocator.DEFAULT);
        } catch (ChannelException channelexception) {
            ;
        }

        ch.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(30))
                .addLast("legacy_query", new LegacyPingHandler(serverConnection))
                .addLast("splitter", new PacketSplitter())
                .addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                .addLast("prepender", new PacketPrepender())
                .addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));

        NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);

        ServerConnection.h.add(networkmanager);
        ch.pipeline().addLast("packet_handler", networkmanager);
        networkmanager.a((new HandshakeListener(serverConnection.f, networkmanager)));

    }
}
