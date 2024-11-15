package it.meneghin.webserver;

import it.meneghin.webserver.core.handlers.factories.HandlerFactory;
import it.meneghin.webserver.core.server.TCPServer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.ServerSocket;


public class Application {

	private static final Logger log = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
        Thread applicationThread = Thread.currentThread();

        int port = 8080;

        try {
	        TCPServer server = new TCPServer(new ServerSocket(port), new HandlerFactory());
            Thread shutdownVThread = Thread.ofVirtual().unstarted(() -> {
                Thread.currentThread().setName("ShutdownThread");
                log.info("Gracefully shutting down");

                server.shutdown();

                synchronized (applicationThread){
                    applicationThread.notify();
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownVThread);
            log.debug("Registered shutdown hook");

            Thread.ofVirtual().start(() -> {
                Thread.currentThread().setName("ConnectionThread");
                server.start();
            });
            log.info("Server listening on port {}...", port);
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
