package tn.esprit.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.universite1.UniversiteService;

import java.util.Optional;
import java.util.List;

class UniversiteServiceImpTest {

        @Mock
        private UniversiteRepository universiteRepository;

        @InjectMocks
        private UniversiteService universiteService;

        private Universite universite;

        @BeforeEach
        void setUp() {
            // Initialisation des mocks et des objets
            MockitoAnnotations.openMocks(this);
            universite = new Universite();
            universite.setIdUniversite(1L);
            universite.setNomUniversite("Test University");
        }

        // Test avec Mockito : Add or Update
        @Test
        void testAddOrUpdate() {
            // Simuler le comportement du repository save()
            when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

            // Appeler la méthode addOrUpdate
            Universite result = universiteService.addOrUpdate(universite);

            // Vérifier que la méthode save() du repository a été appelée et que les résultats sont corrects
            assertNotNull(result);
            assertEquals("Test University", result.getNomUniversite());
            verify(universiteRepository, times(1)).save(universite);
        }

        // Test avec Mockito : Find All
        @Test
        void testFindAll() {
            // Simuler la réponse du repository
            when(universiteRepository.findAll()).thenReturn(List.of(universite));

            // Appeler la méthode findAll()
            List<Universite> result = universiteService.findAll();

            // Vérifier le résultat
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("Test University", result.get(0).getNomUniversite());
        }

        // Test avec JUnit : Find By Id
        @Test
        void testFindById() {
            // Simuler le comportement du repository
            when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

            // Appeler la méthode findById
            Universite result = universiteService.findById(1L);

            // Vérifier le résultat
            assertNotNull(result);
            assertEquals(1L, result.getIdUniversite());
            assertEquals("Test University", result.getNomUniversite());
        }

        // Test avec JUnit : Delete By Id
        @Test
        void testDeleteById() {
            // Appeler la méthode deleteById
            universiteService.deleteById(1L);

            // Vérifier que la méthode deleteById() du repository a été appelée
            verify(universiteRepository, times(1)).deleteById(1L);
        }
    }


