package it.meneghin.webserver.core.server;

import it.meneghin.webserver.core.handlers.SocketHandler;
import it.meneghin.webserver.core.handlers.factories.HandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerTest {

    private TCPServer tcpServerToTest;

    private ServerSocket serverSocketMock;
    private HandlerFactory socketHandlerFactoryMock;

    @BeforeEach
    public void setup() {
        serverSocketMock = Mockito.mock(ServerSocket.class);
        socketHandlerFactoryMock = Mockito.mock(HandlerFactory.class);

        tcpServerToTest = new TCPServer(serverSocketMock, socketHandlerFactoryMock);
    }

    @Test
    public void testStart_ThenAcceptConnection() throws IOException {
        Mockito.when(serverSocketMock.isClosed())
                .thenReturn(false)
                .thenReturn(true);

        Socket socketMock = Mockito.mock(Socket.class);
        Mockito.when(serverSocketMock.accept()).thenReturn(socketMock);

        SocketHandler socketHandlerMock = Mockito.mock(SocketHandler.class);
        Mockito.when(socketHandlerFactoryMock.create(socketMock)).thenReturn(socketHandlerMock);

        tcpServerToTest.start();

        Mockito.verify(socketHandlerFactoryMock, Mockito.times(1)).create(socketMock);
        Mockito.verify(serverSocketMock, Mockito.times(1)).accept();
        Mockito.verify(serverSocketMock, Mockito.times(2)).isClosed();
    }

    @Test
    public void testShutdown_ThenCleanUpResources() throws IOException {
        tcpServerToTest.shutdown();
        Mockito.verify(serverSocketMock, Mockito.times(1)).close();
    }


}
