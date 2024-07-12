package it.meneghin.webserver.core.server;

import it.meneghin.webserver.core.handlers.SocketHandler;
import it.meneghin.webserver.core.handlers.factories.HandlerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Server {
    private static final Logger log = LogManager.getLogger(TCPServer.class);

    private final ServerSocket serverImpl;
    private final HandlerFactory socketHandlerFactory;

    public TCPServer(ServerSocket serverImpl, HandlerFactory handlerFactory) {
        this.serverImpl = serverImpl;
        this.socketHandlerFactory = handlerFactory;
    }

    @Override
    public void start() {
        while (!serverImpl.isClosed()) {
            try {
                log.trace("Waiting for a new connection");
                Socket socket = serverImpl.accept();
                SocketHandler sh = socketHandlerFactory.create(socket);

                Thread.ofVirtual()
                        .name(SocketHandler.class.getName())
                        .start(sh);

            } catch (IOException e) {
                if(serverImpl.isClosed()){
                    log.trace("Stop waiting for connection");
                } else {
                    log.error("Failed to accept connection",e);
                }
            }
        }
    }

    @Override
    public void shutdown() {
        log.debug("Server shutdown initiated");
        try {
            serverImpl.close();
        } catch (IOException e) {
            log.warn("Failed to close server, can be safely ignored", e);
        }
    }
}
