package com.tfg.app.resource.controller;

import com.tfg.app.resource.model.*;
import com.tfg.app.resource.service.ResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ResourceController {

    @Autowired
    private ResourceService svc;

    // ResourceGroup
    @PostMapping("/resource-groups")
    public ResponseEntity<ResourceGroup> createGroup(@RequestBody Map<String, Object> payload) {
        ResourceGroup g = new ResourceGroup();
        
        // Extract name (required)
        if (payload.containsKey("name")) {
            g.setName((String) payload.get("name"));
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        // Handle parent relationship - support both formats
        if (payload.containsKey("parentId")) {
            // Direct parentId format: { "parentId": 123 }
            Object parentIdObj = payload.get("parentId");
            final Long parentId;
            if (parentIdObj instanceof Number) {
                parentId = ((Number) parentIdObj).longValue();
            } else if (parentIdObj instanceof String) {
                try {
                    parentId = Long.parseLong((String) parentIdObj);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                parentId = null;
            }
            
            if (parentId != null) {
                ResourceGroup parent = svc.findGroup(parentId)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Parent group not found with ID: " + parentId));
                g.setParent(parent);
            }
        } else if (payload.containsKey("parent") && payload.get("parent") instanceof Map) {
            // Nested object format: { "parent": { "id": 123 } }
            Map<String, Object> parentMap = (Map<String, Object>) payload.get("parent");
            if (parentMap.containsKey("id")) {
                Long parentId = ((Number) parentMap.get("id")).longValue();
                ResourceGroup parent = svc.findGroup(parentId)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Parent group not found with ID: " + parentId));
                g.setParent(parent);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveGroup(g));
    }
    
    @GetMapping("/resource-groups")
    public List<ResourceGroup> listGroups() { 
        return svc.listGroups(); 
    }
    
    @GetMapping("/resource-groups/{id}")
    public ResponseEntity<ResourceGroup> getGroup(@PathVariable Long id) {
        return svc.findGroup(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    // Get children of a resource group
    @GetMapping("/resource-groups/{id}/children")
    public ResponseEntity<List<ResourceGroup>> getGroupChildren(@PathVariable Long id) {
        // Check if the group exists
        if (!svc.findGroup(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Find all groups where parent.id = id
        List<ResourceGroup> children = svc.findChildrenOfGroup(id);
        return ResponseEntity.ok(children);
    }
    
    @PutMapping("/resource-groups/{id}")
    public ResponseEntity<ResourceGroup> updateGroup(@PathVariable Long id, @RequestBody ResourceGroup g) {
        g.setId(id);
        return ResponseEntity.ok(svc.saveGroup(g));
    }
    
    @DeleteMapping("/resource-groups/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        svc.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }


    // EmployeeSchedule
    @PostMapping("/employee-schedules")
    public ResponseEntity<EmployeeSchedule> createSchedule(@RequestBody EmployeeSchedule s) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveSchedule(s));
    }
    
    @GetMapping("/employee-schedules")
    public List<EmployeeSchedule> listSchedules() { 
        return svc.listSchedules(); 
    }

    @GetMapping("/employee-schedules/{id}")
    public ResponseEntity<EmployeeSchedule> getSchedule(@PathVariable Long id) {
        return svc.findSchedule(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/employee-schedules/{id}")
    public ResponseEntity<EmployeeSchedule> updateSchedule(@PathVariable Long id, @RequestBody EmployeeSchedule s) {
        s.setId(id);
        return ResponseEntity.ok(svc.saveSchedule(s));
    }
    
    @DeleteMapping("/employee-schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        svc.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    // Reservations
        @GetMapping("/resource-groups/{id}/reservations") 
    public List<SpaceReservation> listReservations(@PathVariable("id") Long groupId) {
        return svc.listReservations(groupId);
    }
    
    @PostMapping("/resource-groups/{id}/reservations")
    public ResponseEntity<?> createReservation(
        @PathVariable("id") Long groupId,
        @RequestBody SpaceReservation r) {
        
        // Check if the resource group exists
        var optGroup = svc.findGroup(groupId);
        if (optGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        r.setResourceGroup(optGroup.get());

        try {
            SpaceReservation saved = svc.createReservation(r);

            // Use HashMap instead of Map.of to handle null values
            Map<String,Object> result = new HashMap<>();
            result.put("id", saved.getId());
            result.put("title", saved.getTitle() != null ? saved.getTitle() : "");
            result.put("startDateTime", saved.getStartDateTime() != null ? saved.getStartDateTime().toString() : "");
            result.put("endDateTime", saved.getEndDateTime() != null ? saved.getEndDateTime().toString() : "");
            result.put("reservedBy", saved.getReservedBy() != null ? saved.getReservedBy() : "");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalStateException ex) {
            return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body(Map.of("error", ex.getMessage()));
        }
    }
    
    @GetMapping("/resource-groups/{id}/availability")
    public List<Map<String,Object>> checkAvailability(
        @PathVariable("id") Long groupId,
        @RequestParam("from") LocalDateTime from,
        @RequestParam("to") LocalDateTime to) {

        var reservations = svc.findOverlappingReservations(groupId, from, to);

        return reservations.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("title", r.getTitle() != null ? r.getTitle() : "");
            map.put("startDateTime", r.getStartDateTime().toString());
            map.put("endDateTime", r.getEndDateTime().toString());
            map.put("reservedBy", r.getReservedBy());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/resource-groups/{groupId}/reservations/{resId}")
    public ResponseEntity<SpaceReservation> getReservation(
        @PathVariable Long groupId, 
        @PathVariable Long resId) {
        return svc.findReservation(resId).map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/resource-groups/{groupId}/reservations/{resId}")
    public ResponseEntity<?> updateReservation(
        @PathVariable Long groupId,
        @PathVariable Long resId,
        @RequestBody SpaceReservation r) {

        // Check if the resource group exists
        Optional<ResourceGroup> groupOpt = svc.findGroup(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check if the reservation exists
        Optional<SpaceReservation> resOpt = svc.findReservation(resId);
        if (resOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        r.setResourceGroup(groupOpt.get());
        r.setId(resId);

        SpaceReservation updated = svc.updateReservation(resId, r);

        // Use HashMap instead of Map.of to handle null values
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", updated.getId());
        resp.put("title", updated.getTitle() != null ? updated.getTitle() : "");
        resp.put("startDateTime", updated.getStartDateTime() != null ? updated.getStartDateTime().toString() : "");
        resp.put("endDateTime", updated.getEndDateTime() != null ? updated.getEndDateTime().toString() : "");
        resp.put("reservedBy", updated.getReservedBy() != null ? updated.getReservedBy() : "");
        
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/resource-groups/{groupId}/reservations/{resId}")
    public ResponseEntity<Void> deleteReservation(
        @PathVariable Long groupId,
        @PathVariable Long resId) {
        svc.deleteReservation(resId);
        return ResponseEntity.noContent().build();
    }

    // Nuevo endpoint: todas las reservas sin groupId
    @GetMapping("/reservations")
    public List<SpaceReservation> listAllReservations() {
        return svc.listAllReservations();
    }
}
