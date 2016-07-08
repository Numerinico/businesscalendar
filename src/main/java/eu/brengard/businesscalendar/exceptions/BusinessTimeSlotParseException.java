package eu.brengard.businesscalendar.exceptions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exception thrown when an error occur while creating a
 * {@link eu.brengard.businesscalendar.entitites.BusinessTimeSlot} from a String.
 * @author Nicolas BRENGARD (eu.brengard.businesscalendar.entitites@brengard.eu)
 * @since 1.8
 */
public class BusinessTimeSlotParseException extends RuntimeException {
    private static final Logger logger =
            LogManager.getLogger(BusinessTimeSlotParseException.class);

    public BusinessTimeSlotParseException(String message) {
        super(message);
        logger.log(Level.ERROR, this);
    }
}

