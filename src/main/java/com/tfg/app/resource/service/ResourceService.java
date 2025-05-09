package com.tfg.app.resource.service;

import com.tfg.app.resource.model.*;
import com.tfg.app.resource.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceService {

    @Autowired
    private ResourceGroupRepository groupRepo;

    @Autowired
    private ResourceItemRepository itemRepo;

    @Autowired
    private EmployeeScheduleRepository scheduleRepo;

    @Autowired
    private SpaceReservationRepository reservationRepo;

    // ResourceGroup CRUD
    public ResourceGroup saveGroup(ResourceGroup g) { return groupRepo.save(g); }
    public List<ResourceGroup> listGroups() { return groupRepo.findAll(); }
    public Optional<ResourceGroup> findGroup(Long id) { return groupRepo.findById(id); }
    public void deleteGroup(Long id) { groupRepo.deleteById(id); }
    public ResourceGroup updateGroup(Long id, ResourceGroup g) {
        if (!groupRepo.existsById(id)) {
            throw new IllegalArgumentException("ResourceGroup not found");
        }
        g.setId(id);
        return groupRepo.save(g);
    }

    // ResourceItem CRUD
    public ResourceItem saveItem(ResourceItem i) { return itemRepo.save(i); }
    public List<ResourceItem> listItems() { return itemRepo.findAll(); }
    public Optional<ResourceItem> findItem(Long id) { return itemRepo.findById(id); }
    public void deleteItem(Long id) { itemRepo.deleteById(id); }
    public ResourceItem updateItem(Long id, ResourceItem i) {
        if (!itemRepo.existsById(id)) {
            throw new IllegalArgumentException("ResourceItem not found");
        }
        i.setId(id);
        return itemRepo.save(i);
    }

    // EmployeeSchedule CRUD
    public EmployeeSchedule saveSchedule(EmployeeSchedule s) { return scheduleRepo.save(s); }
    public List<EmployeeSchedule> listSchedules() { return scheduleRepo.findAll(); }
    public Optional<EmployeeSchedule> findSchedule(Long id) { return scheduleRepo.findById(id); }
    public void deleteSchedule(Long id) { scheduleRepo.deleteById(id); }
    public EmployeeSchedule updateSchedule(Long id, EmployeeSchedule s) {
        if (!scheduleRepo.existsById(id)) {
            throw new IllegalArgumentException("EmployeeSchedule not found");
        }
        s.setId(id);
        return scheduleRepo.save(s);
    }

    // Reservations
    public SpaceReservation createReservation(SpaceReservation r) {
        Long id = r.getResourceItem().getId();
        LocalDateTime start = r.getStartDateTime();
        LocalDateTime end   = r.getEndDateTime();

        // Trae todas las reservas de este recurso
        List<SpaceReservation> existing = reservationRepo.findAllByResourceItemId(id);

        // Filtra aquellas que se solapan: existing.start < newEnd && existing.end > newStart
        boolean conflict = existing.stream().anyMatch(ex ->
            ex.getStartDateTime().isBefore(end) &&
            ex.getEndDateTime().isAfter(start)
        );

        if (conflict) {
            throw new IllegalStateException("Conflicto de solapamiento");
        }
        return reservationRepo.save(r);
    }
    public List<SpaceReservation> listReservations(Long itemId) {
        return reservationRepo.findByResourceItemId(itemId);
    }

    public List<SpaceReservation> findOverlappingReservations(Long itemId, LocalDateTime from, LocalDateTime to) {
        return reservationRepo.findOverlapping(itemId, from, to);
    }

    // Find a single reservation
    public Optional<SpaceReservation> findReservation(Long id) {
        return reservationRepo.findById(id);
    }

    // Update a reservation
    public SpaceReservation updateReservation(Long id, SpaceReservation r) {
        return reservationRepo.findById(id).map(existingReservation -> {
            existingReservation.setStartDateTime(r.getStartDateTime());
            existingReservation.setEndDateTime(r.getEndDateTime());
            existingReservation.setResourceItem(r.getResourceItem());
            existingReservation.setReservedBy(r.getReservedBy());
            return reservationRepo.save(existingReservation);
        }).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    // Delete a reservation
    public void deleteReservation(Long id) {
        reservationRepo.deleteById(id);
    }
}
