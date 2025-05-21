package com.tfg.app.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.resource.model.*;
import com.tfg.app.resource.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    @MockBean
    private ResourceService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Group CRUD
    @Test
    void createGroup_returnsCreated() throws Exception {
        ResourceGroup g = new ResourceGroup();
        g.setId(10L);
        when(service.saveGroup(any())).thenReturn(g);

        Map<String, Object> payload = Map.of("name", "TestGroup");
        mockMvc.perform(post("/api/resource-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    void listGroups_returnsAll() throws Exception {
        when(service.listGroups()).thenReturn(List.of(new ResourceGroup(), new ResourceGroup()));
        mockMvc.perform(get("/api/resource-groups"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getGroup_notFound() throws Exception {
        when(service.findGroup(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/resource-groups/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateGroup_returnsOk() throws Exception {
        ResourceGroup g = new ResourceGroup();
        g.setName("Updated Group");
        when(service.saveGroup(any())).thenReturn(g);

        mockMvc.perform(put("/api/resource-groups/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(g)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Updated Group")));
    }

    @Test
    void deleteGroup_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/resource-groups/3"))
            .andExpect(status().isNoContent());
        verify(service).deleteGroup(3L);
    }

    // EmployeeSchedule CRUD
    @Test
    void createSchedule_returnsCreated() throws Exception {
        EmployeeSchedule s = new EmployeeSchedule();
        s.setUsername("user1");
        when(service.saveSchedule(any())).thenReturn(s);

        mockMvc.perform(post("/api/employee-schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username", is("user1")));
    }

    @Test
    void listSchedules_returnsAll() throws Exception {
        EmployeeSchedule schedule1 = new EmployeeSchedule(1L, "user1", "Meeting", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        EmployeeSchedule schedule2 = new EmployeeSchedule(2L, "user2", "Training", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));
        when(service.listSchedules()).thenReturn(List.of(schedule1, schedule2));

        mockMvc.perform(get("/api/employee-schedules"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].username", is("user1")))
            .andExpect(jsonPath("$[1].username", is("user2")));
    }

    @Test
    void getSchedule_notFound() throws Exception {
        when(service.findSchedule(999L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/employee-schedules/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_success() throws Exception {
        EmployeeSchedule s = new EmployeeSchedule();
        s.setUsername("updatedUser");
        when(service.saveSchedule(any())).thenReturn(s);

        mockMvc.perform(put("/api/employee-schedules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is("updatedUser")));
    }

    @Test
    void deleteSchedule_success() throws Exception {
        mockMvc.perform(delete("/api/employee-schedules/1"))
            .andExpect(status().isNoContent());
        verify(service).deleteSchedule(1L);
    }

    // Reservations
    @Test
    void createReservation_groupNotFound() throws Exception {
        when(service.findGroup(999L)).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/resource-groups/999/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createReservation_conflict() throws Exception {
        Long groupId = 1L;
        ResourceGroup group = new ResourceGroup();
        group.setId(groupId);

        SpaceReservation reservation = new SpaceReservation();
        reservation.setTitle("Conflicting Meeting");
        reservation.setStartDateTime(LocalDateTime.now());
        reservation.setEndDateTime(LocalDateTime.now().plusHours(1));

        when(service.findGroup(groupId)).thenReturn(Optional.of(group));
        when(service.createReservation(any(SpaceReservation.class))).thenThrow(new IllegalStateException("Conflicto de solapamiento"));

        mockMvc.perform(post("/api/resource-groups/" + groupId + "/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("Conflicto de solapamiento")));
    }

    @Test
    void handleInvalidFormat_returnsProperError() throws Exception {
        String invalidPayload = "{ \"startDateTime\": \"invalid-date\" }";

        mockMvc.perform(post("/api/resource-groups/1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Invalid request format")));
    }
}
