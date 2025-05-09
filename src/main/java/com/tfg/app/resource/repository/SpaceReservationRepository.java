package com.tfg.app.resource.repository;

import com.tfg.app.resource.model.SpaceReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpaceReservationRepository extends JpaRepository<SpaceReservation,Long> {
    List<SpaceReservation> findByResourceItemId(Long resourceItemId);

    @Query("SELECT r FROM SpaceReservation r WHERE r.resourceItem.id = :itemId "
         + "AND r.startDateTime < :end AND r.endDateTime > :start")
    List<SpaceReservation> findOverlapping(@Param("itemId") Long itemId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);


    List<SpaceReservation> findAllByResourceItemId(Long resourceItemId);
}
