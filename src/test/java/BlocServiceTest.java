import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.Services.Bloc.BlocService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

//@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BlocServiceTest {

    @Autowired
    private BlocService blocService;

    @Test
    public Bloc testaddOrUpdate2() { //Cascade
        Bloc bloc = new Bloc();
        // Set up the Bloc object with necessary data
        Bloc result = blocService.addOrUpdate2(bloc);
        assertNotNull(result);
        return result;
    }

    @Test
    public Bloc testaddOrUpdate() {
        Bloc bloc = new Bloc();
        // Set up the Bloc object with necessary data
        Bloc result = blocService.addOrUpdate(bloc);
        assertNotNull(result);
        return result;
    }

    @Test
    public List<Bloc> testfindAll() {
        List<Bloc> result = blocService.findAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        return result;
    }

    @Test
    public Bloc testfindById() {
        long id = 1L; // Example ID
        Bloc result = blocService.findById(id);
        assertNotNull(result);
        return result;
    }

    @Test
    public void testdeleteById() {
        long id = 1L; // Example ID
        blocService.deleteById(id);
        // Verify deletion if necessary
    }

    @Test
    public void testdelete() {
        Bloc bloc = new Bloc();
        // Set up the Bloc object with necessary data
        blocService.delete(bloc);
        // Verify deletion if necessary
    }

    @Test
    public Bloc testaffecterChambresABloc() {
        List<Long> numChambre = List.of(1L, 2L); // Example chambre numbers
        String nomBloc = "ExampleBloc";
        Bloc result = blocService.affecterChambresABloc(numChambre, nomBloc);
        assertNotNull(result);
        return result;
    }

    @Test
    public Bloc testaffecterBlocAFoyer() {
        String nomBloc = "ExampleBloc";
        String nomFoyer = "ExampleFoyer";
        Bloc result = blocService.affecterBlocAFoyer(nomBloc, nomFoyer);
        assertNotNull(result);
        return result;
    }
}
