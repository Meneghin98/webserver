package it.meneghin.webserver.core.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.DatagramPacket;

public class DatagramPacketHandler implements Runnable {
    private static final Logger log = LogManager.getLogger(DatagramPacketHandler.class);

    private final DatagramPacket packet;

    public DatagramPacketHandler(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {
    }
}
