package it.meneghin.webserver.core.handlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static it.meneghin.webserver.core.utils.Constants.CRLF;

public class SocketHandlerTest {

    private SocketHandler socketHandlerToTest;

    private Socket socketMock;
    private OutputStream osMock;

    @BeforeEach
    public void setupEach() throws IOException {
        this.socketMock = Mockito.mock(Socket.class);
        this.osMock = Mockito.mock(OutputStream.class);

        Mockito.when(socketMock.getOutputStream()).thenReturn(osMock);

        this.socketHandlerToTest = new SocketHandler(socketMock);
    }

    @Test
    public void testResponseMessage() throws IOException {
        final String response =
                "HTTP/1.1 200 OK"
                + CRLF
                + "Content-Type: text/html; charset=UTF-8"
                + CRLF
                + "Content-Length: 32"
                + CRLF
                + CRLF
                + "<html><body>Hello!</body></html>";

        this.socketHandlerToTest.run();

        Mockito.verify(socketMock, Mockito.times(1)).getOutputStream();
        Mockito.verify(socketMock, Mockito.times(1)).close();

        Mockito.verify(osMock, Mockito.times(1)).close();
        ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(osMock, Mockito.times(1)).write(argumentCaptor.capture());
        Assertions.assertArrayEquals(response.getBytes(), argumentCaptor.getValue());
    }
}
