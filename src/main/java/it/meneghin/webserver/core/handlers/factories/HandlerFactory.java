package it.meneghin.webserver.core.handlers.factories;

import it.meneghin.webserver.core.handlers.DatagramPacketHandler;
import it.meneghin.webserver.core.handlers.SocketHandler;

import java.net.DatagramPacket;
import java.net.Socket;

public class HandlerFactory {

    public SocketHandler create(Socket socket) {
        return new SocketHandler(socket);
    }

    public DatagramPacketHandler create(DatagramPacket packet){
        return new DatagramPacketHandler(packet);
    }
}
