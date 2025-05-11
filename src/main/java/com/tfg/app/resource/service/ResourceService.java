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
        Long id = r.getResourceGroup().getId();
        LocalDateTime start = r.getStartDateTime();
        LocalDateTime end = r.getEndDateTime();

        // Check if group exists
        ResourceGroup group = groupRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        // Find overlapping reservations
        List<SpaceReservation> existing = reservationRepo.findAllByResourceGroupId(id);

        // Check for conflicts
        boolean conflict = existing.stream().anyMatch(ex ->
            ex.getStartDateTime().isBefore(end) &&
            ex.getEndDateTime().isAfter(start)
        );

        if (conflict) {
            throw new IllegalStateException("Conflicto de solapamiento");
        }
        return reservationRepo.save(r);
    }
    
    public List<SpaceReservation> listReservations(Long groupId) {
        return reservationRepo.findByResourceGroupId(groupId);
    }

    public List<SpaceReservation> findOverlappingReservations(Long groupId, LocalDateTime from, LocalDateTime to) {
        return reservationRepo.findOverlapping(groupId, from, to);
    }

    public Optional<SpaceReservation> findReservation(Long id) {
        return reservationRepo.findById(id);
    }

    public SpaceReservation updateReservation(Long id, SpaceReservation r) {
        return reservationRepo.findById(id).map(existingReservation -> {
            existingReservation.setStartDateTime(r.getStartDateTime());
            existingReservation.setEndDateTime(r.getEndDateTime());
            existingReservation.setResourceGroup(r.getResourceGroup());
            existingReservation.setReservedBy(r.getReservedBy());
            existingReservation.setTitle(r.getTitle());
            return reservationRepo.save(existingReservation);
        }).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    public void deleteReservation(Long id) {
        reservationRepo.deleteById(id);
    }

    // Find children of a resource group - unchanged
    public List<ResourceGroup> findChildrenOfGroup(Long parentId) {
        ResourceGroup parent = new ResourceGroup();
        parent.setId(parentId);
        return groupRepo.findByParent(parent);
    }

    // Todas las reservas sin filtrar por grupo
    public List<SpaceReservation> listAllReservations() {
        return reservationRepo.findAll();
    }
}
