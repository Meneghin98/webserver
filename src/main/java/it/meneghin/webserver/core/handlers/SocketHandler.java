package it.meneghin.webserver.core.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static it.meneghin.webserver.core.utils.Constants.CRLF;

public class SocketHandler implements Runnable {
    private static final Logger log = LogManager.getLogger(SocketHandler.class);

    private final Socket socket;
    private BufferedReader buffInReader;
    private BufferedWriter buffOutWriter;

    //TODO: socket not null
    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    private void setup() throws IOException {
        buffInReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        buffOutWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void teardown() {
        try {
            if(buffOutWriter != null) buffOutWriter.close();
            if(buffInReader != null) buffInReader.close();
            socket.close();
        } catch (IOException e) {
            log.error("SocketHandler teardown", e);
        }
    }

    private String getReqestLine() {
        return buffInReader.readLine();
    }

    @Override
    public void run() {
        try{
            setup();

            String requestLine = buffInReader.readLine(); //TODO: request line

            for (String line = ""; !CRLF.equals(line); line = buffInReader.readLine()) {

            }

//            OutputStream outStream = socket.getOutputStream();
//
//            String responseContent = "<html><body>Hello!</body></html>";
//            String response = "HTTP/1.1 101 OK" +CRLF+
//                    "Content-Type: text/html; charset=UTF-8"+CRLF+
//                    "Content-Length: " +
//                    responseContent.getBytes(StandardCharsets.UTF_8).length +CRLF+
//                    CRLF+
//                    responseContent;
//
//            os.write(response.getBytes(StandardCharsets.UTF_8));
//            log.debug("Written response:\n{}", response);
//
//            os.close();

        } catch (IOException e) {
            log.error("Socket processing has encountered an error", e);
        } finally {
            teardown();
        }
    }
}
