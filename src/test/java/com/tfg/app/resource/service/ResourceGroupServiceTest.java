package com.tfg.app.resource.service;

import com.tfg.app.resource.model.ResourceGroup;
import com.tfg.app.resource.repository.ResourceGroupRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceGroupServiceTest {
    @Mock ResourceGroupRepository repo;
    @InjectMocks ResourceService svc;

    @Test void saveGroup_shouldDelegate() {
        ResourceGroup g = new ResourceGroup();
        when(repo.save(g)).thenReturn(g);
        assertEquals(g, svc.saveGroup(g));
        verify(repo).save(g);
    }
    @Test void listGroups_shouldReturnAll() {
        var list = List.of(new ResourceGroup(), new ResourceGroup());
        when(repo.findAll()).thenReturn(list);
        assertEquals(2, svc.listGroups().size());
    }
    @Test void updateGroup_whenExists() {
        ResourceGroup g = new ResourceGroup();
        g.setName("G");
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        ResourceGroup res = svc.updateGroup(1L, g);
        assertEquals(1L, res.getId());
    }

    @Test void deleteGroup_invokesDelete() {
        svc.deleteGroup(2L);
        verify(repo).deleteById(2L);
    }
}
