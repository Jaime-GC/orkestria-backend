package com.tfg.app.resource.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeScheduleTest {

    @Test
    void testNoArgsConstructor() {
        EmployeeSchedule schedule = new EmployeeSchedule();
        assertNull(schedule.getId());
        assertNull(schedule.getUsername());
        assertNull(schedule.getTitle());
        assertNull(schedule.getStartDateTime());
        assertNull(schedule.getEndDateTime());
    }

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        String username = "employee1";
        String title = "Meeting";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 6, 15, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 6, 15, 11, 0);

        EmployeeSchedule schedule = new EmployeeSchedule(id, username, title, startDateTime, endDateTime);

        assertEquals(id, schedule.getId());
        assertEquals(username, schedule.getUsername());
        assertEquals(title, schedule.getTitle());
        assertEquals(startDateTime, schedule.getStartDateTime());
        assertEquals(endDateTime, schedule.getEndDateTime());
    }

    @Test
    void testBuilder() {
        Long id = 2L;
        String username = "employee2";
        String title = "Training";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 6, 16, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 6, 16, 16, 0);

        EmployeeSchedule schedule = EmployeeSchedule.builder()
                .id(id)
                .username(username)
                .title(title)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        assertEquals(id, schedule.getId());
        assertEquals(username, schedule.getUsername());
        assertEquals(title, schedule.getTitle());
        assertEquals(startDateTime, schedule.getStartDateTime());
        assertEquals(endDateTime, schedule.getEndDateTime());
    }

    @Test
    void testSettersAndGetters() {
        EmployeeSchedule schedule = new EmployeeSchedule();

        Long id = 3L;
        String username = "employee3";
        String title = "Conference";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 6, 17, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 6, 17, 17, 0);

        schedule.setId(id);
        schedule.setUsername(username);
        schedule.setTitle(title);
        schedule.setStartDateTime(startDateTime);
        schedule.setEndDateTime(endDateTime);

        assertEquals(id, schedule.getId());
        assertEquals(username, schedule.getUsername());
        assertEquals(title, schedule.getTitle());
        assertEquals(startDateTime, schedule.getStartDateTime());
        assertEquals(endDateTime, schedule.getEndDateTime());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime startDateTime = LocalDateTime.of(2023, 6, 15, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 6, 15, 11, 0);

        EmployeeSchedule schedule1 = new EmployeeSchedule(1L, "employee1", "Meeting", startDateTime, endDateTime);
        EmployeeSchedule schedule2 = new EmployeeSchedule(1L, "employee1", "Meeting", startDateTime, endDateTime);
        EmployeeSchedule schedule3 = new EmployeeSchedule(2L, "employee2", "Training", startDateTime, endDateTime);

        assertEquals(schedule1, schedule2);
        assertNotEquals(schedule1, schedule3);
        assertEquals(schedule1.hashCode(), schedule2.hashCode());
        assertNotEquals(schedule1.hashCode(), schedule3.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime startDateTime = LocalDateTime.of(2023, 6, 15, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 6, 15, 11, 0);
        
        EmployeeSchedule schedule = new EmployeeSchedule(1L, "employee1", "Meeting", startDateTime, endDateTime);
        
        String toString = schedule.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=employee1"));
        assertTrue(toString.contains("title=Meeting"));
        assertTrue(toString.contains("startDateTime=" + startDateTime));
        assertTrue(toString.contains("endDateTime=" + endDateTime));
    }
}
