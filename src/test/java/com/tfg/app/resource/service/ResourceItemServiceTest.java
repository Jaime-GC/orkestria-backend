package com.tfg.app.resource.service;

import com.tfg.app.resource.model.ResourceItem;
import com.tfg.app.resource.repository.ResourceItemRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceItemServiceTest {
    @Mock ResourceItemRepository repo;
    @InjectMocks ResourceService svc;

    @Test void saveItem_shouldDelegate() {
        ResourceItem i = new ResourceItem();
        when(repo.save(i)).thenReturn(i);
        assertEquals(i, svc.saveItem(i));
    }
    @Test void listItems_shouldReturnAll() {
        var list = List.of(new ResourceItem(), new ResourceItem());
        when(repo.findAll()).thenReturn(list);
        assertEquals(2, svc.listItems().size());
    }
    @Test void updateItem_whenExists() {
        ResourceItem i = new ResourceItem(null,"I",null);
        when(repo.existsById(3L)).thenReturn(true);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var res = svc.updateItem(3L, i);
        assertEquals(3L, res.getId());
    }

    @Test void deleteItem_invokesDelete() {
        svc.deleteItem(4L);
        verify(repo).deleteById(4L);
    }
}
