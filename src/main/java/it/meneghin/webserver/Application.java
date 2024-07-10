package it.meneghin.webserver;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Application {

	private static final Logger log = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
		log.trace("TRACE");
		log.debug("DEBUG");
		log.info("INFO");
		log.error("ERROR");
	}
}
