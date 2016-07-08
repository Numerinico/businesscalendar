package eu.brengard.businesscalendar.util;

import eu.brengard.businesscalendar.entitites.BusinessDay;
import eu.brengard.businesscalendar.entitites.BusinessTimeSlot;
import eu.brengard.businesscalendar.exceptions.PropertiesLoaderLoadException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PropertiesLoaderTest {
    /**
     * properties filename
     */
    private static final String PROP_TEST_FILE_NAME =
            "/business_calendar_test.properties";

    private PropertiesLoader propertiesLoader;

    @Before
    public void loadNoException() {
        try {
            InputStream properties =
                    getClass().getResourceAsStream(PROP_TEST_FILE_NAME);
            this.propertiesLoader = PropertiesLoader.load(properties);
        } catch (PropertiesLoaderLoadException e) {
            fail();
        }
    }

    @Test(expected = NullPointerException.class)
    public void loadException() {
        InputStream properties =
                getClass().getResourceAsStream(PROP_TEST_FILE_NAME + "s");
        PropertiesLoader.load(properties);
    }

    @Test
    public void getBusinessDays() {
        Set<BusinessDay> businessDaysFromProperties =
                propertiesLoader.getBusinessDays();

        BusinessDay monday = BusinessDay.of(DayOfWeek.MONDAY);
        Set<BusinessTimeSlot> mondayTimeSlots = monday.getBusinessTimeSlots();

        mondayTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        mondayTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));

        BusinessDay tuesday = BusinessDay.of(DayOfWeek.TUESDAY);
        Set<BusinessTimeSlot> tuesdayTimeSlots = tuesday.getBusinessTimeSlots();
        tuesdayTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(17, 0)));

        LinkedHashSet<BusinessDay> businessDays = new LinkedHashSet<>();
        businessDays.add(monday);
        businessDays.add(tuesday);

        assertEquals(businessDaysFromProperties, businessDays);

    }

    @Test
    public void getHolidays() {
        Set<LocalDate> holidaysFromProperties = propertiesLoader.getHolidays();

        HashSet<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2016, 3, 21));
        holidays.add(LocalDate.of(2016, 3, 23));

        assertEquals(holidaysFromProperties, holidays);
    }

}