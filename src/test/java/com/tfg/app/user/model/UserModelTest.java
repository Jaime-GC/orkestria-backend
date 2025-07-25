package com.tfg.app.user.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void testUserConstructorAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(User.Role.CLIENT);
        
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(User.Role.CLIENT, user.getRole());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Create two users with the same values in all fields
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRole(User.Role.CLIENT);
        
        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("user1");  // Same username
        user2.setEmail("user1@example.com");  // Same email
        user2.setRole(User.Role.CLIENT);  // Same role
        
        // User with different ID
        User user3 = new User();
        user3.setId(2L);
        user3.setUsername("user1");
        user3.setEmail("user1@example.com");
        user3.setRole(User.Role.CLIENT);
        
        // Verify equals
        assertEquals(user1, user1);  // Same object
        assertEquals(user1, user2);  // Objects with same values
        assertNotEquals(user1, user3); // Different ID
        assertNotEquals(user1, null);  // Null check
        assertNotEquals(user1, "not a user");  // Different type
        
        // Verify hashCode
        assertEquals(user1.hashCode(), user2.hashCode());
    }
    
    @Test
    void testEnumValues() {
        assertEquals(2, User.Role.values().length);
        assertEquals(User.Role.EMPLOYEE, User.Role.valueOf("EMPLOYEE"));
        assertEquals(User.Role.CLIENT, User.Role.valueOf("CLIENT"));
    }
    
    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        String toString = user.toString();
        
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
    }
}