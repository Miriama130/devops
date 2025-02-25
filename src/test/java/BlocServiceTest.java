import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Bloc.BlocService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private BlocService blocService;

    private Bloc bloc;
    private Chambre chambre1, chambre2;

    @BeforeEach
    void setUp() {
        bloc = new Bloc();
        bloc.setNomBloc("Bloc A");
        chambre1 = new Chambre();
        chambre1.setNumeroChambre(101L);
        chambre2 = new Chambre();
        chambre2.setNumeroChambre(102L);
        bloc.setChambres(Arrays.asList(chambre1, chambre2));
    }

    @Test
    void testAddOrUpdate() {
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);
        Bloc savedBloc = blocService.addOrUpdate(bloc);
        assertNotNull(savedBloc);
        verify(blocRepository, times(1)).save(bloc);
        verify(chambreRepository, times(2)).save(any(Chambre.class));
    }

    @Test
    void testFindAll() {
        when(blocRepository.findAll()).thenReturn(Arrays.asList(bloc));
        List<Bloc> blocs = blocService.findAll();
        assertFalse(blocs.isEmpty());
        assertEquals(1, blocs.size());
    }

    @Test
    void testFindById() {
        when(blocRepository.findById(anyLong())).thenReturn(Optional.of(bloc));
        Bloc foundBloc = blocService.findById(1L);
        assertNotNull(foundBloc);
    }

    @Test
    void testDeleteById() {
        doNothing().when(blocRepository).deleteById(anyLong());
        blocService.deleteById(1L);
        verify(blocRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAffecterBlocAFoyer() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer 1");

        when(blocRepository.findByNomBloc(anyString())).thenReturn(bloc);
        when(foyerRepository.findByNomFoyer(anyString())).thenReturn(foyer);
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc updatedBloc = blocService.affecterBlocAFoyer("Bloc A", "Foyer 1");
        assertNotNull(updatedBloc.getFoyer());
        verify(blocRepository, times(1)).save(bloc);
    }
}