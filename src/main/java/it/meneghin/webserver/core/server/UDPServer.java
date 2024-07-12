package it.meneghin.webserver.core.server;

import it.meneghin.webserver.core.handlers.DatagramPacketHandler;
import it.meneghin.webserver.core.handlers.factories.DatagramPacketFactory;
import it.meneghin.webserver.core.handlers.factories.HandlerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer implements Server {
    private static final Logger log = LogManager.getLogger(UDPServer.class);

    private final DatagramSocket serverImpl;
    private final HandlerFactory handlerFactory;
    private final DatagramPacketFactory datagramPacketFactory;

    public UDPServer(DatagramSocket serverImpl, HandlerFactory handlerFactory, DatagramPacketFactory datagramPacketFactory) {
        this.serverImpl = serverImpl;
        this.handlerFactory = handlerFactory;
        this.datagramPacketFactory = datagramPacketFactory;
    }

    @Override
    public void start() {
        DatagramPacket responsePacket = datagramPacketFactory.create(1024);
        while (!serverImpl.isClosed()) {
            try {
                log.trace("Waiting for a new packet");
                serverImpl.receive(responsePacket);
                DatagramPacketHandler dph = handlerFactory.create(responsePacket);

                Thread.ofVirtual()
                        .name(DatagramPacketHandler.class.getName())
                        .start(dph);
                
            } catch (IOException e) {
                if(serverImpl.isClosed()){
                    log.trace("Stop waiting for connection");
                } else {
                    log.error("Failed to receive packet",e);
                }
            }
        }
    }

    @Override
    public void shutdown() {
        log.debug("Server shutdown initiated");
        serverImpl.close();
    }
}
