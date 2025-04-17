package tn.esprit.spring.RestControllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.Services.Foyer.IFoyerService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

class FoyerRestControllerTest {

    @Mock
    private IFoyerService service;

    @InjectMocks
    private FoyerRestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addOrUpdate() {
        Foyer foyer = new Foyer();
        when(service.addOrUpdate(foyer)).thenReturn(foyer);

        Foyer result = controller.addOrUpdate(foyer);

        assertNotNull(result);
        verify(service, times(1)).addOrUpdate(foyer);
    }

    @Test
    void findAll() {
        List<Foyer> foyers = Arrays.asList(new Foyer(), new Foyer());
        when(service.findAll()).thenReturn(foyers);

        List<Foyer> result = controller.findAll();

        assertEquals(2, result.size());
        verify(service, times(1)).findAll();
    }

    @Test
    void findById() {
        Foyer foyer = new Foyer();
        when(service.findById(1L)).thenReturn(foyer);

        Foyer result = controller.findById(1L);

        assertNotNull(result);
        verify(service, times(1)).findById(1L);
    }

    @Test
    void delete() {
        Foyer foyer = new Foyer();
        doNothing().when(service).delete(foyer);

        controller.delete(foyer);

        verify(service, times(1)).delete(foyer);
    }

    @Test
    void deleteById() {
        doNothing().when(service).deleteById(1L);

        controller.deleteById(1L);

        verify(service, times(1)).deleteById(1L);
    }

    @Test
    void affecterFoyerAUniversite() {
        Universite universite = new Universite();
        when(service.affecterFoyerAUniversite(1L, "TestUniversity")).thenReturn(universite);

        Universite result = controller.affecterFoyerAUniversite(1L, "TestUniversity");

        assertNotNull(result);
        verify(service, times(1)).affecterFoyerAUniversite(1L, "TestUniversity");
    }

    @Test
    void desaffecterFoyerAUniversite() {
        Universite universite = new Universite();
        when(service.desaffecterFoyerAUniversite(1L)).thenReturn(universite);

        Universite result = controller.desaffecterFoyerAUniversite(1L);

        assertNotNull(result);
        verify(service, times(1)).desaffecterFoyerAUniversite(1L);
    }

    @Test
    void ajouterFoyerEtAffecterAUniversite() {
        Foyer foyer = new Foyer();
        when(service.ajouterFoyerEtAffecterAUniversite(foyer, 1L)).thenReturn(foyer);

        Foyer result = controller.ajouterFoyerEtAffecterAUniversite(foyer, 1L);

        assertNotNull(result);
        verify(service, times(1)).ajouterFoyerEtAffecterAUniversite(foyer, 1L);
    }
}
