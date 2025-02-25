package tn.esprit.spring;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.Repositories.EtudiantRepository;
import tn.esprit.spring.services.etudiant.EtudiantService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class EtudiantServiceMockTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    private EtudiantService etudiantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        etudiantService = new EtudiantService(etudiantRepository);
    }

    // Test for addOrUpdate method
    @Test
    void testAddOrUpdateEtudiant() {
        Etudiant etudiant = new Etudiant("John", "Doe", Long.valueOf(12345678), "ESPRIT", LocalDate.of(2000, 1, 1));

        // Mock the repository behavior
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        Etudiant savedEtudiant = etudiantService.addOrUpdate(etudiant);

        // Assert the returned object is not null and matches expected values
        assertNotNull(savedEtudiant);
        assertEquals("John", savedEtudiant.getNomEt());
        assertEquals("Doe", savedEtudiant.getPrenomEt());
        verify(etudiantRepository, times(1)).save(etudiant);  // Verify save was called once
    }

    // Test for findAll method
    @Test
    void testFindAllEtudiants() {
        Etudiant etudiant1 = new Etudiant("John", "Doe", Long.valueOf(123456), "ESPRIT", LocalDate.of(2000, 1, 1));
        Etudiant etudiant2 = new Etudiant("Jane", "Smith", Long.valueOf(1234567), "ESPRIT", LocalDate.of(1999, 5, 15));

        // Mock repository response
        when(etudiantRepository.findAll()).thenReturn(Arrays.asList(etudiant1, etudiant2));

        List<Etudiant> etudiants = etudiantService.findAll();

        // Assert the returned list is not null and contains the expected number of students
        assertNotNull(etudiants);
        assertEquals(2, etudiants.size());
        verify(etudiantRepository, times(1)).findAll();  // Verify findAll was called once
    }

    // Test for findById method
    @Test
    void testFindById() {
        Etudiant etudiant = new Etudiant("Jane", "Smith", Long.valueOf(123456), "ESPRIT", LocalDate.of(1999, 5, 15));

        // Mock repository response
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        Etudiant foundEtudiant = etudiantService.findById(1L);

        // Assert the returned student is not null and matches the expected name
        assertNotNull(foundEtudiant);
        assertEquals("Jane", foundEtudiant.getNomEt());
        verify(etudiantRepository, times(1)).findById(1L);  // Verify findById was called once
    }

    // Test for deleteById method
    @Test
    void testDeleteById() {
        Etudiant etudiant = new Etudiant("Mark", "Johnson", Long.valueOf(1), "ESPRIT", LocalDate.of(1995, 11, 30));

        // When we call the deleteById method, we don't expect any result, just a call
        etudiantService.deleteById(etudiant.getIdEtudiant());

        // Verify that deleteById was called once with the correct ID
        verify(etudiantRepository, times(1)).deleteById(etudiant.getIdEtudiant());
    }

    // Test for delete method
    @Test
    void testDeleteEtudiant() {
        Etudiant etudiant = new Etudiant("Sara", "Brown", Long.valueOf(123456), "ESPRIT", LocalDate.of(2001, 6, 20));

        // When we call the delete method, we expect it to interact with the repository
        etudiantService.delete(etudiant);

        // Verify that delete was called once with the correct Etudiant object
        verify(etudiantRepository, times(1)).delete(etudiant);
    }
}