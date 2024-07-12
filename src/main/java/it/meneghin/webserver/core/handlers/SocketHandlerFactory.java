package it.meneghin.webserver.core.handlers;

import java.net.Socket;

public class SocketHandlerFactory {

    public Runnable create(Socket socket) {
        return new SocketHandler(socket);
    }
}
