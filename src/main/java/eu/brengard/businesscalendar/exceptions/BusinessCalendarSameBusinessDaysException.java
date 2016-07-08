package eu.brengard.businesscalendar.exceptions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.DayOfWeek;

/**
 * Exception thrown when two {@link eu.brengard.businesscalendar.entitites.BusinessDay}s having the
 * same day-of-week are added to the same
 * {@link eu.brengard.businesscalendar.entitites.BusinessCalendar}.
 * @author Nicolas BRENGARD (eu.brengard.businesscalendar.entitites@brengard.eu)
 * @since 1.8
 */
public class BusinessCalendarSameBusinessDaysException
        extends RuntimeException {
    private static final Logger logger =
            LogManager.getLogger(BusinessTimeSlotParseException.class);

    public BusinessCalendarSameBusinessDaysException(DayOfWeek dayOfWeek) {
        super(dayOfWeek.name().toLowerCase() + " has already been defined, "
                + "use multiple BusinessTimeSlot for the same BusinessDay "
                + "instead");
        logger.log(Level.ERROR, this);
    }
}

