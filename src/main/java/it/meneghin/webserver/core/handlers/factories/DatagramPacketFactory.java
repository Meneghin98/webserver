package it.meneghin.webserver.core.handlers.factories;

import java.net.DatagramPacket;

public class DatagramPacketFactory {

    public DatagramPacket create(int bufferLength) {
        byte[] buffer = new byte[bufferLength];
        return new DatagramPacket(buffer, buffer.length);
    }
}
