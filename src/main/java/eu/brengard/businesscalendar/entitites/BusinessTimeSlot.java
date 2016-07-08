package eu.brengard.businesscalendar.entitites;

import eu.brengard.businesscalendar.exceptions.BusinessTimeSlotParseException;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A time slot, defined by a start time and an end time.
 * <p>
 * This class provides methods to create and handle a time slot.
 * This includes a method to get the {@link Duration} getDuration two
 * {@link LocalTime}s by counting only the time inside the time slot.
 * <p>
 * A time slot is constituted of two {@link LocalTime}s :
 * <ul>
 * <li>
 * A start time, inclusive.
 * </li>
 * <li>
 * An end time, exclusive.
 * </li>
 * </ul>
 *
 * @author Nicolas BRENGARD (eu.brengard.businesscalendar.entitites@brengard.eu)
 * @see java.time
 * @since 1.8
 */
public class BusinessTimeSlot implements Serializable {

    /**
     * {@value #DEFAULT_DATE_TIME_FORMATTER_PATTERN} The default date time
     * pattern used by the parse methods.
     */
    public static final String DEFAULT_DATE_TIME_FORMATTER_PATTERN = "H:mm";

    /**
     * {@value DEFAULT_TIMES_SEPARATOR} The default start time and end time
     * separator used by the parse methods.
     */
    public static final String DEFAULT_TIMES_SEPARATOR = "-";

    /**
     * {@value DEFAULT_SLOTS_SEPARATOR} The default slots separator used by
     * the parse methods providing multiple time slots.
     */
    public static final String DEFAULT_SLOTS_SEPARATOR = ",";

    /**
     * The start time, inclusive, not null.
     */
    private LocalTime startInclusive;

    /**
     * The end time, exclusive, not null.
     */
    private LocalTime endExclusive;

    /**
     * Simple private constructor that initialize fields from the parameters.
     *
     * @param startInclusive the start time, inclusive, not null.
     * @param endExclusive   the end time, inclusive, not null.
     */
    private BusinessTimeSlot(LocalTime startInclusive, LocalTime endExclusive) {
        validate(startInclusive, endExclusive);
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
    }

    /**
     * Obtains an instance of {@code BusinessTimeSlot} from start and end times.
     *
     * @param startInclusive the start time, inclusive, not null.
     * @param endExclusive   the end time, inclusive, not null.
     * @return the time slot, not null.
     */
    public static BusinessTimeSlot of(LocalTime startInclusive,
                                      LocalTime endExclusive) {
        return new BusinessTimeSlot(startInclusive, endExclusive);
    }

    /**
     * Obtains an instance of {@code BusinessTimeSlot} from a time slot as
     * string using default times separator and time pattern.
     *
     * @param timeSlot the time slot as string, not null.
     * @return the time slot, not null.
     * @see #DEFAULT_TIMES_SEPARATOR
     * @see #DEFAULT_DATE_TIME_FORMATTER_PATTERN
     */
    public static BusinessTimeSlot parse(String timeSlot) {
        return parse(timeSlot, DEFAULT_TIMES_SEPARATOR, DateTimeFormatter
                .ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
    }

    /**
     * Obtains an instance of {@code BusinessTimeSlot} from a time slot as
     * string using default time pattern and custom times separator.
     *
     * @param timeSlot            the time slot as string, not null.
     * @param regexTimesSeparator the custom times separator, not null.
     * @return the time slot, not null.
     * @see #DEFAULT_DATE_TIME_FORMATTER_PATTERN
     */
    public static BusinessTimeSlot parse(String timeSlot,
                                         String regexTimesSeparator) {
        return parse(timeSlot, regexTimesSeparator, DateTimeFormatter
                .ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
    }

    /**
     * Obtains an instance of {@code BusinessTimeSlot} from a time slot as
     * string using custom times separator and time pattern.
     *
     * @param timeSlot            the time slot as string, not null.
     * @param regexTimesSeparator the custom times separator, not null.
     * @param dateTimeFormatter   the custom time pattern, not null.
     * @return the time slot, not null.
     */
    public static BusinessTimeSlot parse(String timeSlot,
                                         String regexTimesSeparator,
                                         DateTimeFormatter dateTimeFormatter) {
        Objects.requireNonNull(timeSlot, "timeSlot");
        Objects.requireNonNull(regexTimesSeparator, "regexTimesSeparator");
        Objects.requireNonNull(dateTimeFormatter, "dateTimeFormatter");

        String[] times = timeSlot.split(regexTimesSeparator);

        try {
            LocalTime startTime = LocalTime.parse(times[0], dateTimeFormatter);
            LocalTime endTime = LocalTime.parse(times[1], dateTimeFormatter);
            return new BusinessTimeSlot(startTime, endTime);
        } catch (DateTimeParseException e) {
            throw new BusinessTimeSlotParseException(e.getMessage());
        }
    }

    /**
     * Obtains {@code BusinessTimeSlot}s from time slots as
     * string using default times separator, slots separator and time pattern.
     *
     * @param timeSlots the time slots as string, not null.
     * @return the time slots, not null.
     * @see #DEFAULT_TIMES_SEPARATOR
     * @see #DEFAULT_SLOTS_SEPARATOR
     * @see #DEFAULT_DATE_TIME_FORMATTER_PATTERN
     */
    public static Set<BusinessTimeSlot> parseMultiple(String timeSlots) {
        return parseMultiple(timeSlots, DEFAULT_SLOTS_SEPARATOR,
                DEFAULT_TIMES_SEPARATOR, DateTimeFormatter
                        .ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
    }

    /**
     * Obtains {@code BusinessTimeSlot}s from time slots as
     * string using default times separator and time pattern and custom slots
     * separator.
     *
     * @param timeSlots           the time slots as string, not null.
     * @param regexSlotsSeparator the custom slots separator, not null.
     * @return the time slots, not null.
     * @see #DEFAULT_TIMES_SEPARATOR
     * @see #DEFAULT_DATE_TIME_FORMATTER_PATTERN
     */
    public static Set<BusinessTimeSlot> parseMultiple(String timeSlots,
                                                      String regexSlotsSeparator) {
        return parseMultiple(timeSlots, regexSlotsSeparator,
                DEFAULT_TIMES_SEPARATOR, DateTimeFormatter
                        .ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
    }

    /**
     * Obtains {@code BusinessTimeSlot}s from time slots as
     * string using custom times separator, slots separator and time pattern.
     *
     * @param timeSlots           the time slots as string, not null.
     * @param regexSlotsSeparator the custom slots separator, not null.
     * @param regexTimesSeparator the custom times separator, not null.
     * @param dateTimeFormatter   the custom time pattern
     * @return the time slots, not null.
     */
    public static Set<BusinessTimeSlot> parseMultiple(String timeSlots,
                                                      String regexSlotsSeparator,
                                                      String regexTimesSeparator,
                                                      DateTimeFormatter dateTimeFormatter) {
        Objects.requireNonNull(timeSlots, "timeSlot");
        Objects.requireNonNull(regexSlotsSeparator, "regexSlotsSeparator");
        Objects.requireNonNull(regexTimesSeparator, "regexTimesSeparator");
        Objects.requireNonNull(dateTimeFormatter, "dateTimeFormatter");

        Set<BusinessTimeSlot> timeSlotsList = new HashSet<>();

        String[] timeSlotsArray = timeSlots.split(regexSlotsSeparator);

        if (!timeSlotsArray[0].isEmpty()) {
            for (String timeSlotString : timeSlotsArray) {
                BusinessTimeSlot businessTimeSlot = BusinessTimeSlot
                        .parse(timeSlotString, regexTimesSeparator,
                                dateTimeFormatter);
                timeSlotsList.add(businessTimeSlot);
            }
        }

        return timeSlotsList;
    }

    /**
     * Check the coherence getDuration two times (eg: start time is before end
     * time).
     *
     * @param startInclusive the start time, inclusive, not null.
     * @param endExclusive   the end time, exclusive, not null.
     * @throws IllegalArgumentException if the two times are not coherent.
     */
    private static void validate(LocalTime startInclusive,
                                 LocalTime endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");

        if (!startInclusive.isBefore(endExclusive)) {
            throw new IllegalArgumentException(
                    "First time must be before second time");
        }
    }

    /**
     * Get the duration getDuration two localTimes.
     * (eg: Duration = real Duration - time slot Duration).
     *
     * @param startInclusive the start time, inclusive, not null.
     * @param endExclusive   the end time, exclusive, not null.
     * @return the duration getDuration two localTimes, not null, eventually negative.
     */
    public Duration getDuration(LocalTime startInclusive,
                                LocalTime endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");

        boolean isNegated = false;

        //Negate duration if startInclusive is after endExclusive
        if (startInclusive.isAfter(endExclusive)) {
            LocalTime temp = endExclusive;
            endExclusive = startInclusive;
            startInclusive = temp;
            isNegated = true;
        }

        Duration duration = Duration.ZERO;

        //handle different cases
        if (this.contains(startInclusive)) {
            if (this.contains(endExclusive)) {
                duration = Duration.between(startInclusive, endExclusive);
            } else {
                duration = Duration.between(startInclusive, this.endExclusive);
            }
        } else {
            if (this.contains(endExclusive)) {
                duration = Duration.between(this.startInclusive, endExclusive);
            } else {
                if (this.startInclusive.isBefore(endExclusive)
                        && this.endExclusive.isAfter(startInclusive)) {
                    duration = Duration.between(this.startInclusive,
                            this.endExclusive);
                }
            }
        }

        if (isNegated) {
            return duration.negated();
        } else {
            return duration;
        }
    }

    /**
     * Check if a localTime is part of the time slot.
     *
     * @param localTime the localTime to check, not null.
     * @return true if it is part of the time slots, false if not.
     */
    public boolean contains(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");

        return localTime.equals(startInclusive) || (
                localTime.isAfter(startInclusive) && localTime
                        .isBefore(endExclusive));
    }

    /**
     * Get the time slot start time. Remember that LocalTime is immutable so is
     * startInclusive.
     *
     * @return the time slot start time, not null.
     */
    public LocalTime getStartInclusive() {
        return startInclusive;
    }

    /**
     * Set the time slot start time.
     *
     * @param startInclusive the start time to set, not null.
     */
    public void setStartInclusive(LocalTime startInclusive) {
        validate(startInclusive, this.endExclusive);
        this.startInclusive = startInclusive;
    }

    /**
     * Get the time slot end time. Remember that LocalTime is immutable so is
     * endExclusive.
     *
     * @return the time slot end time, not null.
     */
    public LocalTime getEndExclusive() {
        return endExclusive;
    }

    /**
     * Set the time slot end time.
     *
     * @param endExclusive the end time to set, not null.
     */
    public void setEndExclusive(LocalTime endExclusive) {
        validate(this.startInclusive, endExclusive);
        this.endExclusive = endExclusive;
    }

    /**
     * Check if two time slots are equals.
     *
     * @param o the second time slot.
     * @return true if the two time slots have the same start time and end time.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BusinessTimeSlot that = (BusinessTimeSlot) o;
        return startInclusive.equals(that.startInclusive) && endExclusive
                .equals(that.endExclusive);
    }

    /**
     * Get hashCode from the time slot start time and end time.
     *
     * @return the time slot hashcode.
     */
    @Override
    public int hashCode() {
        int result = startInclusive.hashCode();
        result = 31 * result + endExclusive.hashCode();
        return result;
    }
}

