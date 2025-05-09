package com.tfg.app.resource.controller;

import com.tfg.app.resource.model.*;
import com.tfg.app.resource.service.ResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.foreign.Linker.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ResourceController {

    @Autowired
    private ResourceService svc;

    // ResourceGroup
    @PostMapping("/resource-groups")
    public ResponseEntity<ResourceGroup> createGroup(@RequestBody ResourceGroup g) {
        if (g.getParent() != null) {
            ResourceGroup parent = svc.findGroup(g.getParent().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent group not found"));
            g.setParent(parent);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveGroup(g));
    }
    @GetMapping("/resource-groups")
    public List<ResourceGroup> listGroups() { return svc.listGroups(); }
    
    @GetMapping("/resource-groups/{id}")
    public ResponseEntity<ResourceGroup> getGroup(@PathVariable Long id) {
        return svc.findGroup(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
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




    // ResourceItem
    @PostMapping("/resource-items")
    public ResponseEntity<ResourceItem> createItem(@RequestBody ResourceItem i) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveItem(i));
    }
    @GetMapping("/resource-items")
    public List<ResourceItem> listItems() { return svc.listItems(); }

    @GetMapping("/resource-items/{id}")
    public ResponseEntity<ResourceItem> getItem(@PathVariable Long id) {
        return svc.findItem(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/resource-items/{id}")
    public ResponseEntity<ResourceItem> updateItem(@PathVariable Long id, @RequestBody ResourceItem i) {
        i.setId(id);
        return ResponseEntity.ok(svc.saveItem(i));
    }
    @DeleteMapping("/resource-items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        svc.deleteItem(id);
        return ResponseEntity.noContent().build();
    }




    // EmployeeSchedule
    @PostMapping("/employee-schedules")
    public ResponseEntity<EmployeeSchedule> createSchedule(@RequestBody EmployeeSchedule s) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveSchedule(s));
    }
    @GetMapping("/employee-schedules")
    public List<EmployeeSchedule> listSchedules() { return svc.listSchedules(); }

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
    @GetMapping("/resource-items/{id}/reservations")
    public List<SpaceReservation> listReservations(@PathVariable("id") Long itemId) {
        return svc.listReservations(itemId);
    }
    @PostMapping("/resource-items/{id}/reservations")
    public ResponseEntity<?> createReservation(
        @PathVariable("id") Long itemId,
        @RequestBody SpaceReservation r) {

        // 1) Comprobar existencia del recurso
        var optItem = svc.findItem(itemId);
        if (optItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        r.setResourceItem(optItem.get());

        try {
            SpaceReservation saved = svc.createReservation(r);

            // 2) Devolvemos solo un Map plano
            Map<String,Object> result = Map.of(
                "id",             saved.getId(),
                "startDateTime",  saved.getStartDateTime().toString(),
                "endDateTime",    saved.getEndDateTime().toString(),
                "reservedBy",     saved.getReservedBy()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalStateException ex) {
            return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body(Map.of("error", ex.getMessage()));
        }
    }
    @GetMapping("/resource-items/{id}/availability")
    public ResponseEntity<List<Map<String,Object>>> checkAvailability(@PathVariable("id") Long itemId, @RequestParam("from") LocalDateTime from, @RequestParam("to")   LocalDateTime to) {

    var reservations = svc.findOverlappingReservations(itemId, from, to);

    // Mapea cada reserva a un Map con sólo los campos primitivos que necesitas
    List<Map<String,Object>> out = reservations.stream().map(r -> Map.<String,Object>of(
        "id",            r.getId(),
        "startDateTime", r.getStartDateTime().toString(),
        "endDateTime",   r.getEndDateTime().toString(),
        "reservedBy",    r.getReservedBy()
    )).toList();

    // Siempre se envía un JSON array, incluso si out está vacío o tiene 1 elemento
    return ResponseEntity.ok(out);
}

    // Get single reservation
    @GetMapping("/resource-items/{itemId}/reservations/{resId}")
    public ResponseEntity<SpaceReservation> getReservation(@PathVariable Long itemId, @PathVariable Long reservId) {
        return svc.findReservation(reservId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Update reservation
    @PutMapping("/resource-items/{itemId}/reservations/{resId}")
    public ResponseEntity<?> updateReservation(
        @PathVariable Long itemId,
        @PathVariable Long resId,
        @RequestBody SpaceReservation r) {

        // Check if the resource item exists
        Optional<ResourceItem> itemOpt = svc.findItem(itemId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check if the reservation exists
        Optional<SpaceReservation> resOpt = svc.findReservation(resId);
        if (resOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Assign the request body to the existing resource item and set the reservation ID
        r.setResourceItem(itemOpt.get());
        r.setId(resId);

        // Perform update
        SpaceReservation updated = svc.updateReservation(resId, r);

        // Return a flat Map response
        Map<String, Object> resp = Map.of(
            "id",             updated.getId(),
            "startDateTime",  updated.getStartDateTime().toString(),
            "endDateTime",    updated.getEndDateTime().toString(),
            "reservedBy",     updated.getReservedBy()
        );
        return ResponseEntity.ok(resp);
    }

    // Delete reservation
    @DeleteMapping("/resource-items/{itemId}/reservations/{resId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long itemId, @PathVariable Long resId) {
        svc.deleteReservation(resId);
        return ResponseEntity.noContent().build();
    }
}
