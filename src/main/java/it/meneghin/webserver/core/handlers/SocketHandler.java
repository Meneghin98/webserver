package it.meneghin.webserver.core.handlers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static it.meneghin.webserver.core.utils.Constants.*;

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

    private RequestLine getReqestLine() throws IOException {
        String requestLine = buffInReader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalStateException("Reader returned null or blank request line");
        }

        String[] parts = requestLine.split(" ", 3);

        return new RequestLine(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim()
        );
    }

    private Map<String,String> getHeaders() throws IOException {
        HashMap<String,String> headers = new HashMap<>();

        for (String line = buffInReader.readLine(); (line != null) && !line.isEmpty(); line = buffInReader.readLine()) {
            String[] parts = line.split(":",2);
            if (parts.length != 2){
                continue;
            }

            headers.put(parts[0].trim().toLowerCase(), parts[1].trim());
        }

        return headers;
    }

    private String generateSecWebsocketAccept(String secWebsocketKey) {
        String uuid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String acceptValue = secWebsocketKey+uuid;
        byte[] acceptValueSha = DigestUtils.sha1(acceptValue);
        acceptValue = Base64.encodeBase64String(acceptValueSha);
        return acceptValue;
    }

    private String generateStatusLine() {
        BasicStatusLine statusLine = new BasicStatusLine(
                new ProtocolVersion("HTTP", 1,1),
                HttpStatus.SC_SWITCHING_PROTOCOLS,
                "Switching Protocols"
        );

        return statusLine.toString();
    }

    private String[] generateResponseHeaders(Map<String,String> requestHeaders) {
        String[] parts = new String[3];
        parts[0] = "Upgrade: websocket";
        parts[1] = "Connection: Upgrade";

        String acceptValue = generateSecWebsocketAccept(requestHeaders.get(SEC_WEBSOCKET_KEY.toLowerCase()));
        parts[2] = SEC_WEBSOCKET_ACCEPT.concat(": ").concat(acceptValue);

        return parts;
    }

    @Override
    public void run() {
        try{
            setup();

            log.info("START");
            RequestLine requestLine = getReqestLine();
            log.info(requestLine);
            Map<String,String> requestHeaders = getHeaders();
            log.info(requestHeaders);
            log.info("END");

            String statLine = generateStatusLine();
            String[] responseHeaders = generateResponseHeaders(requestHeaders);
            log.info(statLine);
            buffOutWriter.write(statLine+CRLF);
            for (String line : responseHeaders){
                log.info(line);
                buffOutWriter.write(line+CRLF);
            }
            buffOutWriter.write(CRLF);

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

    private record RequestLine(String verb, String path, String version) {}
}
