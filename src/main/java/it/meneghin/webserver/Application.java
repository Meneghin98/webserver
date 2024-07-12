package it.meneghin.webserver;

import it.meneghin.webserver.core.threads.SocketHandlerThread;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;


public class Application {

	private static final Logger log = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
        Thread applicationThread = Thread.currentThread();

        try {
            AtomicBoolean serverRuning = new AtomicBoolean(true);
            ServerSocket server = new ServerSocket(8080);

            Thread shutdownVThread = Thread.ofVirtual().unstarted(() -> {
                Thread.currentThread().setName("ShutdownThread");
                log.info("Gracefully shutting down");
                serverRuning.set(false);
                try {
                    server.close();
                } catch (IOException e) {
                    log.warn("Failed to close server", e);
                }
                synchronized (applicationThread){
                    applicationThread.notify();
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownVThread);
            log.debug("Registered shutdown hook");

            Thread.ofVirtual().start(() -> {
                Thread.currentThread().setName("ConnectionThread");
                while (serverRuning.get()) {
                    try {
                        log.trace("Waiting for a new connection");
                        Socket socket = server.accept();
                        (new SocketHandlerThread(socket)).start();
                    } catch (IOException e) {
                        if(server.isClosed()){
                            log.trace("Stop waiting for connection");
                        } else {
                            log.error("Failed to accept connection",e);
                        }
                    }
                }
            });
            log.info("Server listening on port 8080...");
        } catch (IOException e) {
            log.error("Failed to start server", e);
        }

        synchronized (applicationThread) {
            try {
                applicationThread.wait();
            } catch (InterruptedException e) {
                log.error("Application thread has been interrupted", e);
                log.info("Application shutting down");
            }
        }
    }
}
