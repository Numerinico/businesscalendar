package eu.brengard.businesscalendar.util;

import eu.brengard.businesscalendar.entitites.BusinessDay;
import eu.brengard.businesscalendar.entitites.BusinessTimeSlot;
import eu.brengard.businesscalendar.exceptions.PropertiesLoaderLoadException;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utility class provided to load settings from a formatted business
 * calendar properties file.
 * <p>
 * These settings can then be used to get the required objects needed
 * to create a {@link eu.brengard.businesscalendar.entitites.BusinessCalendar}.
 *
 * @author Nicolas BRENGARD (eu.brengard.businesscalendar.entitites@brengard.eu)
 * @since 1.8
 */
public class PropertiesLoader {
    /**
     * {@value PROP_PREFIX_WEEK_DAY} Prefix used for week days properties.
     */
    private static final String PROP_PREFIX_WEEK_DAY = "weekday.";

    /**
     * {@value PROP_HOLIDAYS} Holidays property.
     */
    private static final String PROP_HOLIDAYS = "holidays";

    /**
     * {@value PROP_DATE_PATTERN} Date pattern property.
     */
    private static final String PROP_DATE_PATTERN = "date.pattern";

    /**
     * {@value PROP_DEFAULT_DATE_PATTERN} Default date pattern.
     */
    private static final String PROP_DEFAULT_DATE_PATTERN = "d/M/yyyy";

    /**
     * Properties having business days and holidays.
     */
    private Properties properties;

    /**
     * Simple private constructor that initialize field.
     */
    private PropertiesLoader() {
        properties = new Properties();
    }

    /**
     * Obtains an instance of {@code PropertiesLoader} from properties input
     * stream.
     *
     * @param properties the properties tu use, not null.
     * @return the properties loader, not null.
     */
    public static PropertiesLoader load(InputStream properties) {
        Objects.requireNonNull(properties, "properties");

        PropertiesLoader propertiesLoader = new PropertiesLoader();
        try {
            propertiesLoader.properties.load(properties);
        } catch (Exception e) {
            throw new PropertiesLoaderLoadException();
        }
        return propertiesLoader;
    }

    /**
     * Get the business days.
     *
     * @return the business days, not null.
     */
    public Set<BusinessDay> getBusinessDays() {

        Set<BusinessDay> businessDays = new LinkedHashSet<>(7);

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            String timeSlotsString = properties.getProperty(
                    PROP_PREFIX_WEEK_DAY + dayOfWeek.name().toLowerCase());

            if (timeSlotsString != null) {
                BusinessDay businessDay = BusinessDay.of(dayOfWeek);
                Set<BusinessTimeSlot> businessTimeSlots =
                        BusinessTimeSlot.parseMultiple(timeSlotsString);

                businessDay.setBusinessTimeSlots(businessTimeSlots);

                businessDays.add(businessDay);
            }

        }

        return businessDays;
    }

    /**
     * Get the holidays.
     *
     * @return the holidays, not null.
     */
    public Set<LocalDate> getHolidays() {
        Set<LocalDate> holidays = new HashSet<>();

        String holidaysString = properties.getProperty(PROP_HOLIDAYS);

        if (holidaysString != null && !holidaysString.isEmpty()) {
            String datePattern = properties
                    .getProperty(PROP_DATE_PATTERN, PROP_DEFAULT_DATE_PATTERN);
            DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern(datePattern);

            String[] holidaysTable = holidaysString.split(",");

            for (String holidayString : holidaysTable) {
                LocalDate holidayLocalDate =
                        LocalDate.parse(holidayString, dateTimeFormatter);
                holidays.add(holidayLocalDate);
            }
        }

        return holidays;
    }
}

