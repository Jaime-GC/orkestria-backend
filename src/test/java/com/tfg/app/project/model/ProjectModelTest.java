package com.tfg.app.project.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectModelTest {

    @Test
    void testProjectConstructorAndGetters() {
        // Create a project using empty constructor and setters
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Description");
        project.setStartDate(LocalDate.now());
        project.setStatus(Project.ProjectStatus.IN_PROGRESS); // Fixed: use enum
        
        // Verify that values have been assigned correctly
        assertEquals(1L, project.getId());
        assertEquals("Test Project", project.getName());
        assertEquals("Description", project.getDescription());
        assertEquals(LocalDate.now(), project.getStartDate());
        assertEquals(Project.ProjectStatus.IN_PROGRESS, project.getStatus());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Create two identical projects
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Test Project");
        project1.setDescription("Description");
        
        Project project2 = new Project();
        project2.setId(1L);
        project2.setName("Test Project");
        project2.setDescription("Description");
        
        // A different project
        Project project3 = new Project();
        project3.setId(2L);
        project3.setName("Test Project");
        
        // Verify equals
        assertEquals(project1, project2);  // Should be equal with same values
        assertNotEquals(project1, project3); // Different IDs
        assertNotEquals(project1, null);    // Null check
        assertNotEquals(project1, "not a project"); // Different type
        
        // Verify hashCode
        assertEquals(project1.hashCode(), project2.hashCode());
    }
    
    @Test
    void testToString() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        
        String toString = project.toString();
        
        // Verify that toString contains basic information
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Project"));
    }
    
    @Test
    void testProjectStatusEnum() {
        // Verify enum values
        assertEquals(3, Project.ProjectStatus.values().length);
        assertEquals(Project.ProjectStatus.PLANNED, Project.ProjectStatus.valueOf("PLANNED"));
        assertEquals(Project.ProjectStatus.IN_PROGRESS, Project.ProjectStatus.valueOf("IN_PROGRESS"));
        assertEquals(Project.ProjectStatus.COMPLETE, Project.ProjectStatus.valueOf("COMPLETE"));
    }
}