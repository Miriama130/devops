package tn.esprit.spring.restControllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.services.bloc.IBlocService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoyerRestControllerTest {

    @Mock
    private IBlocService blocService;

    @InjectMocks
    private BlocRestController blocRestController;

    private Bloc sampleBloc;

    @BeforeEach
    void setUp() {
        sampleBloc = new Bloc();
        sampleBloc.setNomBloc("Sample Bloc");
    }

    @Test
    void addOrUpdate_ShouldReturnSavedBloc() {
        when(blocService.addOrUpdate(any(Bloc.class))).thenReturn(sampleBloc);

        Bloc result = blocRestController.addOrUpdate(sampleBloc);

        assertNotNull(result);
        assertEquals(sampleBloc, result);
        verify(blocService, times(1)).addOrUpdate(sampleBloc);
    }

    @Test
    void findAll_ShouldReturnAllBlocs() {
        List<Bloc> blocs = Arrays.asList(sampleBloc, new Bloc());
        when(blocService.findAll()).thenReturn(blocs);

        List<Bloc> result = blocRestController.findAll();

        assertEquals(2, result.size());
        verify(blocService, times(1)).findAll();
    }

    @Test
    void findAll_ShouldHandleEmptyList() {
        when(blocService.findAll()).thenReturn(Collections.emptyList());

        List<Bloc> result = blocRestController.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ShouldReturnBlocWhenExists() {
        when(blocService.findById(anyLong())).thenReturn(sampleBloc);

        Bloc result = blocRestController.findById(1L);

        assertNotNull(result);
        assertEquals(sampleBloc, result);
        verify(blocService, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnNullWhenNotExists() {
        when(blocService.findById(anyLong())).thenReturn(null);

        Bloc result = blocRestController.findById(99L);

        assertNull(result);
    }

    @Test
    void delete_ShouldCallService() {
        doNothing().when(blocService).delete(any(Bloc.class));

        blocRestController.delete(sampleBloc);

        verify(blocService, times(1)).delete(sampleBloc);
    }

    @Test
    void deleteById_ShouldCallService() {
        doNothing().when(blocService).deleteById(anyLong());

        blocRestController.deleteById(1L);

        verify(blocService, times(1)).deleteById(1L);
    }

    @Test
    void affecterChambresABloc_ShouldReturnBloc() {
        List<Long> chambreIds = Arrays.asList(1L, 2L);
        when(blocService.affecterChambresABloc(anyList(), anyString()))
                .thenReturn(sampleBloc);

        Bloc result = blocRestController.affecterChambresABloc(chambreIds, "Test Bloc");

        assertNotNull(result);
        assertEquals(sampleBloc, result);
        verify(blocService, times(1)).affecterChambresABloc(chambreIds, "Test Bloc");
    }

    @Test
    void affecterChambresABloc_ShouldHandleEmptyList() {
        when(blocService.affecterChambresABloc(anyList(), anyString()))
                .thenReturn(sampleBloc);

        Bloc result = blocRestController.affecterChambresABloc(Collections.emptyList(), "Test Bloc");

        assertNotNull(result);
    }

    @Test
    void affecterBlocAFoyer_ShouldReturnBloc() {
        when(blocService.affecterBlocAFoyer(anyString(), anyString()))
                .thenReturn(sampleBloc);

        Bloc result = blocRestController.affecterBlocAFoyer("Test Bloc", "Test Foyer");

        assertNotNull(result);
        assertEquals(sampleBloc, result);
        verify(blocService, times(1)).affecterBlocAFoyer("Test Bloc", "Test Foyer");
    }

    @Test
    void affecterBlocAFoyer_ShouldHandleEmptyNames() {
        when(blocService.affecterBlocAFoyer(anyString(), anyString()))
                .thenReturn(null);

        Bloc result = blocRestController.affecterBlocAFoyer("", "");

        assertNull(result);
    }
}