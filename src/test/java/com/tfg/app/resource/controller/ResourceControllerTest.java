package com.tfg.app.resource.controller;

import com.tfg.app.resource.model.*;
import com.tfg.app.resource.service.ResourceService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    @Mock ResourceService svc;
    @InjectMocks ResourceController ctrl;

    // Group CRUD
    @Test void createGroup_returnsCreated() {
        ResourceGroup g = new ResourceGroup();
        when(svc.saveGroup(g)).thenReturn(g);
        var resp = ctrl.createGroup(g);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test void listGroups_returnsAll() {
        var list = List.of(new ResourceGroup(), new ResourceGroup());
        when(svc.listGroups()).thenReturn(list);
        assertEquals(2, ctrl.listGroups().size());
    }

    @Test void getGroup_notFound() {
        when(svc.findGroup(1L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, ctrl.getGroup(1L).getStatusCode());
    }

    @Test void updateGroup_returnsOk() {
        ResourceGroup g = new ResourceGroup();
        when(svc.saveGroup(any())).thenReturn(g);
        var resp = ctrl.updateGroup(2L, g);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test void deleteGroup_returnsNoContent() {
        var resp = ctrl.deleteGroup(3L);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(svc).deleteGroup(3L);
    }

    // Item CRUD
    @Test void createItem_returnsCreated() {
        ResourceItem i = new ResourceItem();
        when(svc.saveItem(i)).thenReturn(i);
        assertEquals(HttpStatus.CREATED, ctrl.createItem(i).getStatusCode());
    }

    @Test void getItem_notFound() {
        when(svc.findItem(1L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, ctrl.getItem(1L).getStatusCode());
    }

    // Schedule CRUD
    @Test void createSchedule_returnsCreated() {
        EmployeeSchedule s = new EmployeeSchedule();
        when(svc.saveSchedule(s)).thenReturn(s);
        assertEquals(HttpStatus.CREATED, ctrl.createSchedule(s).getStatusCode());
    }

    // Reservations endpoints
    @Test void listReservations_delegates() {
        when(svc.listReservations(1L)).thenReturn(List.of());
        assertTrue(ctrl.listReservations(1L).isEmpty());
    }

    @Test void createReservation_itemNotFound() {
        when(svc.findItem(2L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND,
            ctrl.createReservation(2L, new SpaceReservation()).getStatusCode());
    }

    @Test void getReservation_notFound() {
        when(svc.findReservation(3L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND,
            ctrl.getReservation(1L, 3L).getStatusCode());
    }

    @Test void updateReservation_notFound() {
        when(svc.findItem(4L)).thenReturn(Optional.of(new ResourceItem()));
        when(svc.findReservation(5L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND,
            ctrl.updateReservation(4L, 5L, new SpaceReservation()).getStatusCode());
    }

    @Test void deleteReservation_returnsNoContent() {
        var resp = ctrl.deleteReservation(1L, 6L);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(svc).deleteReservation(6L);
    }
}
