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

    @Test void saveSchedule_shouldDelegate() {
        EmployeeSchedule e = new EmployeeSchedule(null,"bob", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        when(repo.save(e)).thenReturn(e);
        assertEquals(e, svc.saveSchedule(e));
    }
    @Test void listSchedules_shouldReturnAll() {
        var list = List.of(new EmployeeSchedule(), new EmployeeSchedule());
        when(repo.findAll()).thenReturn(list);
        assertEquals(2, svc.listSchedules().size());
    }
    @Test void updateSchedule_whenExists() {
        EmployeeSchedule s = new EmployeeSchedule(null,"u",LocalDateTime.now(),LocalDateTime.now());
        when(repo.existsById(5L)).thenReturn(true);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var res = svc.updateSchedule(5L, s);
        assertEquals(5L, res.getId());
    }

    @Test void deleteSchedule_invokesDelete() {
        svc.deleteSchedule(6L);
        verify(repo).deleteById(6L);
    }
}
