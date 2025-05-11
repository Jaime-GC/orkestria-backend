package com.tfg.app.resource.repository;

import com.tfg.app.resource.model.SpaceReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpaceReservationRepository extends JpaRepository<SpaceReservation,Long> {
    List<SpaceReservation> findByResourceGroupId(Long resourceGroupId);

    @Query("SELECT r FROM SpaceReservation r WHERE r.resourceGroup.id = :groupId "
         + "AND r.startDateTime < :end AND r.endDateTime > :start")
    List<SpaceReservation> findOverlapping(@Param("groupId") Long groupId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);


    List<SpaceReservation> findAllByResourceGroupId(Long resourceGroupId);
}
