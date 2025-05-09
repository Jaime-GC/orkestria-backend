package com.tfg.app.resource.service;

import com.tfg.app.resource.model.SpaceReservation;
import com.tfg.app.resource.model.ResourceItem;
import com.tfg.app.resource.repository.SpaceReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpaceReservationServiceTest {

    @Mock
    private SpaceReservationRepository repo;

    @InjectMocks
    private ResourceService svc;

    private final ResourceItem item = ResourceItem.builder()
        .id(1L)
        .name("Sala A")
        .build();

    private final LocalDateTime start = LocalDateTime.of(2025,5,10,9,0);
    private final LocalDateTime end   = LocalDateTime.of(2025,5,10,11,0);

    @Test
    void createReservation_noOverlap() {
        SpaceReservation r = SpaceReservation.builder()
            .resourceItem(item)
            .startDateTime(start)
            .endDateTime(end)
            .reservedBy("alice")
            .build();

        when(repo.findAllByResourceItemId(1L)).thenReturn(Collections.emptyList());
        when(repo.save(r)).thenReturn(r);

        SpaceReservation result = svc.createReservation(r);

        assertEquals(r, result);
        verify(repo).save(r);
    }

    @Test
    void createReservation_withOverlap() {
        SpaceReservation r = SpaceReservation.builder()
            .resourceItem(item)
            .startDateTime(start)
            .endDateTime(end)
            .reservedBy("alice")
            .build();

        // Creamos una reserva existente que solapa completamente
        SpaceReservation existing = SpaceReservation.builder()
            .resourceItem(item)
            .startDateTime(start.minusHours(1))
            .endDateTime(end.plusHours(1))
            .reservedBy("bob")
            .build();

        when(repo.findAllByResourceItemId(1L))
            .thenReturn(Collections.singletonList(existing));

        // Ahora esperamos IllegalStateException, no NPE (NullPointerException)
        // porque el método findAllByResourceItemId devuelve una lista vacía
        assertThrows(IllegalStateException.class, () -> svc.createReservation(r));
    }

    @Test
    void findReservation_whenExists() {
        SpaceReservation old = SpaceReservation.builder()
            .id(2L)
            .resourceItem(item)
            .startDateTime(start)
            .endDateTime(end)
            .reservedBy("x")
            .build();

        when(repo.findById(2L)).thenReturn(Optional.of(old));

        Optional<SpaceReservation> opt = svc.findReservation(2L);
        assertTrue(opt.isPresent());
        assertEquals(old, opt.get());
    }

    @Test
    void updateReservation_whenExists() {
        SpaceReservation old = SpaceReservation.builder()
            .id(8L)
            .resourceItem(item)
            .startDateTime(start)
            .endDateTime(end)
            .reservedBy("x")
            .build();
        SpaceReservation upd = SpaceReservation.builder()
            .resourceItem(item)
            .startDateTime(start)
            .endDateTime(end)
            .reservedBy("y")
            .build();

        when(repo.findById(8L)).thenReturn(Optional.of(old));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = svc.updateReservation(8L, upd);
        assertEquals("y", res.getReservedBy());
    }

    @Test
    void deleteReservation_invokesDelete() {
        svc.deleteReservation(9L);
        verify(repo).deleteById(9L);
    }
}
