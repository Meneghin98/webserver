package it.meneghin.webserver.core.server;

import it.meneghin.webserver.core.handlers.DatagramPacketHandler;
import it.meneghin.webserver.core.handlers.factories.DatagramPacketFactory;
import it.meneghin.webserver.core.handlers.factories.HandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServerTest {

    private UDPServer udpServerToTest;

    private DatagramSocket datagramSocketMock;
    private HandlerFactory handlerFactoryMock;
    private DatagramPacketFactory packetFactoryMock;

    @BeforeEach
    public void setup() {
        this.datagramSocketMock = Mockito.mock(DatagramSocket.class);
        this.handlerFactoryMock = Mockito.mock(HandlerFactory.class);
        this.packetFactoryMock = Mockito.mock(DatagramPacketFactory.class);

        this.udpServerToTest = new UDPServer(datagramSocketMock, handlerFactoryMock, packetFactoryMock);
    }

    @Test
    public void testStart_ThenReceivePacket() throws IOException {
        Mockito.when(datagramSocketMock.isClosed())
                .thenReturn(false)
                .thenReturn(true);

        DatagramPacket packetMock = Mockito.mock(DatagramPacket.class);
        Mockito.when(packetFactoryMock.create(1024)).thenReturn(packetMock);

        DatagramPacketHandler handlerMock = Mockito.mock(DatagramPacketHandler.class);
        Mockito.when(handlerFactoryMock.create(packetMock)).thenReturn(handlerMock);

        udpServerToTest.start();

        Mockito.verify(packetFactoryMock, Mockito.times(1)).create(1024);
        Mockito.verify(handlerFactoryMock, Mockito.times(1)).create(packetMock);
        Mockito.verify(datagramSocketMock, Mockito.times(1)).receive(packetMock);
        Mockito.verify(datagramSocketMock, Mockito.times(2)).isClosed();
    }

    @Test
    public void testShutdown_ThenCleanUpResources() {
        udpServerToTest.shutdown();
        Mockito.verify(datagramSocketMock, Mockito.times(1)).close();
    }
}
