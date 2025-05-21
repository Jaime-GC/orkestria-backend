package com.tfg.app.resource.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SpaceReservationTest {

    @Test
    void testNoArgsConstructor() {
        SpaceReservation reservation = new SpaceReservation();
        assertNull(reservation.getId());
        assertNull(reservation.getTitle());
        assertNull(reservation.getStartDateTime());
        assertNull(reservation.getEndDateTime());
        assertNull(reservation.getReservedBy());
        assertNull(reservation.getResourceGroup());
    }

    @Test
    void testSettersAndGetters() {
        SpaceReservation reservation = new SpaceReservation();

        Long id = 1L;
        String title = "Team Meeting";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 6, 20, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 6, 20, 15, 30);
        String reservedBy = "user123";
        ResourceGroup resourceGroup = new ResourceGroup();
        resourceGroup.setId(5L);
        resourceGroup.setName("Conference Room");

        reservation.setId(id);
        reservation.setTitle(title);
        reservation.setStartDateTime(startDateTime);
        reservation.setEndDateTime(endDateTime);
        reservation.setReservedBy(reservedBy);
        reservation.setResourceGroup(resourceGroup);

        assertEquals(id, reservation.getId());
        assertEquals(title, reservation.getTitle());
        assertEquals(startDateTime, reservation.getStartDateTime());
        assertEquals(endDateTime, reservation.getEndDateTime());
        assertEquals(reservedBy, reservation.getReservedBy());
        assertEquals(resourceGroup, reservation.getResourceGroup());
    }

    @Test
    void testOverlappingReservations() {
        ResourceGroup resourceGroup = new ResourceGroup();
        resourceGroup.setId(1L);
        
        // Create two overlapping reservations
        LocalDateTime start1 = LocalDateTime.of(2023, 6, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2023, 6, 15, 12, 0);
        
        LocalDateTime start2 = LocalDateTime.of(2023, 6, 15, 11, 0);  // Overlaps with first
        LocalDateTime end2 = LocalDateTime.of(2023, 6, 15, 13, 0);
        
        SpaceReservation reservation1 = new SpaceReservation();
        reservation1.setId(1L);
        reservation1.setStartDateTime(start1);
        reservation1.setEndDateTime(end1);
        reservation1.setResourceGroup(resourceGroup);
        
        SpaceReservation reservation2 = new SpaceReservation();
        reservation2.setId(2L);
        reservation2.setStartDateTime(start2);
        reservation2.setEndDateTime(end2);
        reservation2.setResourceGroup(resourceGroup);
        
        // Check overlap condition
        boolean overlaps = reservation2.getStartDateTime().isBefore(reservation1.getEndDateTime()) &&
                           reservation2.getEndDateTime().isAfter(reservation1.getStartDateTime());
        
        assertTrue(overlaps, "The reservations should overlap");
    }

    @Test
    void testNonOverlappingReservations() {
        ResourceGroup resourceGroup = new ResourceGroup();
        resourceGroup.setId(1L);
        
        // Create two non-overlapping reservations
        LocalDateTime start1 = LocalDateTime.of(2023, 6, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2023, 6, 15, 12, 0);
        
        LocalDateTime start2 = LocalDateTime.of(2023, 6, 15, 13, 0);  // After first ends
        LocalDateTime end2 = LocalDateTime.of(2023, 6, 15, 15, 0);
        
        SpaceReservation reservation1 = new SpaceReservation();
        reservation1.setId(1L);
        reservation1.setStartDateTime(start1);
        reservation1.setEndDateTime(end1);
        reservation1.setResourceGroup(resourceGroup);
        
        SpaceReservation reservation2 = new SpaceReservation();
        reservation2.setId(2L);
        reservation2.setStartDateTime(start2);
        reservation2.setEndDateTime(end2);
        reservation2.setResourceGroup(resourceGroup);
        
        // Check overlap condition
        boolean overlaps = reservation2.getStartDateTime().isBefore(reservation1.getEndDateTime()) &&
                           reservation2.getEndDateTime().isAfter(reservation1.getStartDateTime());
        
        assertFalse(overlaps, "The reservations should not overlap");
    }

    @Test
    void testReservationSameResourceGroup() {
        ResourceGroup resourceGroup = new ResourceGroup();
        resourceGroup.setId(1L);
        resourceGroup.setName("Conference Room");
        
        SpaceReservation reservation1 = new SpaceReservation();
        reservation1.setResourceGroup(resourceGroup);
        
        SpaceReservation reservation2 = new SpaceReservation();
        reservation2.setResourceGroup(resourceGroup);
        
        assertEquals(reservation1.getResourceGroup().getId(), 
                     reservation2.getResourceGroup().getId(),
                     "Both reservations should have the same resource group ID");
    }
}
