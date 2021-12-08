package kr.co.nexmore.rfiddaemon.reader.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import kr.co.nexmore.rfiddaemon.reader.udp.handler.RFIDUdpMessageDecoder;
import kr.co.nexmore.rfiddaemon.reader.udp.handler.RFIDUdpMessageEncoder;
import kr.co.nexmore.rfiddaemon.reader.udp.handler.RFIDUdpMessageHandler;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpRequestVO;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static kr.co.nexmore.rfiddaemon.common.PacketConstant.*;
import static kr.co.nexmore.rfiddaemon.reader.udp.handler.RFIDUdpMessageHandler.*;

@Slf4j
@Component
public class RFIDUdpBroadcaster implements Runnable {

    @Value("${netty.broadcaster.port:7100}")
    private int broadcasterPort;

    @Value("${netty.broadcaster.target.ip:255.255.255.255}")
    private String targetIp;

    @Value("${netty.broadcaster.target.port:7100}")
    private int targetPort;

    @Value("${netty.co.timeout.second:10}")
//    @Value("${netty.co.timeout.second:10}")
    private int coTimeoutSecond;

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private Channel channel;


    @Override
    @PostConstruct
    public synchronized void run() {

        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
//                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel datagramChannel) throws Exception {
                            ChannelPipeline p = datagramChannel.pipeline();
                            p.addLast("udpEncoder", new RFIDUdpMessageEncoder(new InetSocketAddress(targetIp, targetPort)));
                            p.addLast("udpDecoder", new RFIDUdpMessageDecoder());
                            p.addLast("udpHandler", new RFIDUdpMessageHandler());
                        }
                    });
            channel = bootstrap.bind(broadcasterPort).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized UdpResponseVO sendUdpCommand(UdpRequestVO requestVO) {
        UdpResponseVO responseVO = null;
        try {
            channel.writeAndFlush(requestVO).sync();

            if ((requestVO.getCommand() == UDP_CORE_DOWNLOAD_REQUEST_COM || requestVO.getCommand() == UDP_FIRMWARE_DOWNLOAD_REQUEST_COM) && requestVO.getCurrentCount() == 0) {
                responseVO = udpBlockingQueue.poll(4500, TimeUnit.MILLISECONDS);
            } else {
                responseVO = udpBlockingQueue.poll(200, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return responseVO;
    }

}
