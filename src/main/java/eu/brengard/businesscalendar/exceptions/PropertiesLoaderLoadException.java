package eu.brengard.businesscalendar.exceptions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exception thrown if an error occur while extracting properties from the
 * business_calendar.properties file.
 * @author Nicolas BRENGARD (eu.brengard.businesscalendar.entitites@brengard.eu)
 * @since 1.8
 */
public class PropertiesLoaderLoadException extends RuntimeException {
    private static final Logger logger =
            LogManager.getLogger(PropertiesLoaderLoadException.class);

    public PropertiesLoaderLoadException() {
        logger.log(Level.ERROR, this);
    }
}

