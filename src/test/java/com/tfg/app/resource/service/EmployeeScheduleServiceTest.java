package com.tfg.app.resource.service;

import com.tfg.app.resource.model.EmployeeSchedule;
import com.tfg.app.resource.repository.EmployeeScheduleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeeScheduleServiceTest {
    @Mock EmployeeScheduleRepository repo;
    @InjectMocks ResourceService svc;

    @Test
    void saveSchedule_shouldDelegate() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        EmployeeSchedule e = new EmployeeSchedule(null, "bob", "Bob's Schedule", start, end);
        when(repo.save(e)).thenReturn(e);
        
        // Act
        EmployeeSchedule result = svc.saveSchedule(e);
        
        // Assert
        assertEquals(e, result);
        verify(repo).save(e);
    }
    
    @Test
    void listSchedules_shouldReturnAll() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        EmployeeSchedule schedule1 = new EmployeeSchedule(1L, "user1", "Meeting", now, now.plusHours(1));
        EmployeeSchedule schedule2 = new EmployeeSchedule(2L, "user2", "Training", now.plusHours(2), now.plusHours(4));
        List<EmployeeSchedule> schedules = Arrays.asList(schedule1, schedule2);
        
        when(repo.findAll()).thenReturn(schedules);
        
        // Act
        List<EmployeeSchedule> result = svc.listSchedules();
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(schedule1, result.get(0));
        assertEquals(schedule2, result.get(1));
        verify(repo).findAll();
    }
    
    @Test
    void findSchedule_shouldReturnScheduleById() {
        // Arrange
        Long id = 1L;
        EmployeeSchedule schedule = new EmployeeSchedule(id, "user", "Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        when(repo.findById(id)).thenReturn(Optional.of(schedule));
        
        // Act
        Optional<EmployeeSchedule> result = svc.findSchedule(id);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(schedule, result.get());
        verify(repo).findById(id);
    }
    
    @Test
    void findSchedule_shouldReturnEmptyWhenNotFound() {
        // Arrange
        Long id = 999L;
        when(repo.findById(id)).thenReturn(Optional.empty());
        
        // Act
        Optional<EmployeeSchedule> result = svc.findSchedule(id);
        
        // Assert
        assertFalse(result.isPresent());
        verify(repo).findById(id);
    }
    
    @Test
    void updateSchedule_whenExists() {
        // Arrange
        Long id = 5L;
        LocalDateTime now = LocalDateTime.now();
        EmployeeSchedule s = new EmployeeSchedule(null, "user", "Update Test", now, now.plusHours(1));
        
        when(repo.existsById(id)).thenReturn(true);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        // Act
        EmployeeSchedule result = svc.updateSchedule(id, s);
        
        // Assert
        assertEquals(id, result.getId());
        assertEquals("user", result.getUsername());
        assertEquals("Update Test", result.getTitle());
        assertEquals(now, result.getStartDateTime());
        assertEquals(now.plusHours(1), result.getEndDateTime());
        
        verify(repo).existsById(id);
        verify(repo).save(s);
    }
    
    @Test
    void updateSchedule_throwsExceptionWhenNotExists() {
        // Arrange
        Long id = 999L;
        EmployeeSchedule s = new EmployeeSchedule();
        when(repo.existsById(id)).thenReturn(false);
        
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                                                  () -> svc.updateSchedule(id, s));
        assertEquals("EmployeeSchedule not found", ex.getMessage());
        verify(repo).existsById(id);
        verify(repo, never()).save(any());
    }

    @Test
    void deleteSchedule_invokesDelete() {
        // Arrange
        Long id = 6L;
        
        // Act
        svc.deleteSchedule(id);
        
        // Assert
        verify(repo).deleteById(id);
    }
}
