package tn.esprit.spring.Services.universite1;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UniversiteService implements IUniversiteService {
    UniversiteRepository repo;

    @Override
    public Universite addOrUpdate(Universite u) {
        return repo.save(u);
    }

    @Override
    public List<Universite> findAll() {
        return repo.findAll();
    }


    @Override
    public Universite findById(long id) {
        Optional<Universite> universiteOptional = repo.findById(id);
        if (universiteOptional.isPresent()) {
            return universiteOptional.get();
        } else {
            throw new EntityNotFoundException("L'université avec l'ID " + id + " n'a pas été trouvée.");
        }
    }


    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Universite u) {
        repo.delete(u);
    }
}
