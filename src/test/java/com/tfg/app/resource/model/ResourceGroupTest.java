package com.tfg.app.resource.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResourceGroupTest {

    @Test
    void testResourceGroupConstructorAndGetters() {
        ResourceGroup parent = new ResourceGroup();
        parent.setId(1L);
        parent.setName("Parent Group");
        
        ResourceGroup group = new ResourceGroup();
        group.setId(2L);
        group.setName("Test Group");
        group.setParent(parent);
        
        assertEquals(2L, group.getId());
        assertEquals("Test Group", group.getName());
        assertEquals(parent, group.getParent());
        
        // Test parent properties
        assertEquals(1L, parent.getId());
        assertEquals("Parent Group", parent.getName());
        assertNull(parent.getParent());
    }
    
    @Test
    void testEqualsAndHashCode() {
        ResourceGroup group1 = new ResourceGroup();
        group1.setId(1L);
        group1.setName("Test");
        
        ResourceGroup group2 = new ResourceGroup();
        group2.setId(1L);
        group2.setName("Different");
        
        ResourceGroup group3 = new ResourceGroup();
        group3.setId(2L);
        group3.setName("Test");
        
        // Test equals
        assertEquals(group1, group1);  // Same object
        assertEquals(group1, group2);  // Same ID
        assertNotEquals(group1, group3); // Different ID
        
        // Test hashCode
        assertEquals(group1.hashCode(), group2.hashCode());
        assertNotEquals(group1.hashCode(), group3.hashCode());
    }
}