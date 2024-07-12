package it.meneghin.webserver.core.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static it.meneghin.webserver.core.utils.Constants.CRLF;

public class SocketHandlerThread extends Thread {

    private static final Logger log = LogManager.getLogger(SocketHandlerThread.class);

    private final Socket socket;

    public SocketHandlerThread(Socket socket) {
        super(SocketHandlerThread.class.getName());
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            OutputStream os = socket.getOutputStream();

            String responseContent = "<html><body>Hello!</body></html>";
            String response = "HTTP/1.1 200 OK" +CRLF+
                    "Content-Type: text/html; charset=UTF-8"+CRLF+
                    "Content-Length: " +
                    responseContent.getBytes(StandardCharsets.UTF_8).length +CRLF+
                    CRLF+
                    responseContent;

            os.write(response.getBytes(StandardCharsets.UTF_8));
            log.debug("Written response:\n{}", response);

            os.close();
            socket.close();

        } catch (IOException e) {
            log.error("Socket processing has encountered an error", e);
        }
    }
}
