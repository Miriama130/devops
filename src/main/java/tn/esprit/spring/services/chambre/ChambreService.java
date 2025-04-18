package tn.esprit.spring.services.chambre;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    ChambreRepository repo;
    BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }

    @Override
    public Chambre findById(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + id));
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        return repo.countByTypeCAndBlocIdBloc(type, idBloc);
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        return List.of();
    }

    @Override
    public List<Chambre> getchNonReserved(String nomFoyer, TypeChambre type) {

        // Afficher les chambres non réservée, par typeChambre,
        // appartenant à un foyer donné par son nom, effectué durant
        // l’année universitaire actuelle.

        // Début "récuperer l'année universitaire actuelle"
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int numReservation;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() > 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        }
        // Fin "récuperer l'année universitaire actuelle"
        List<Chambre> listChambreDispo = new ArrayList<>();
        for (Chambre c : repo.findAll()) {
            if (c.getTypeC().equals(type) && c.getBloc().getFoyer().getNomFoyer().equals(nomFoyer)) { // Les chambres du foyer X et qui ont le type Y
                numReservation = 0;
                // nchoufou les réservations mta3 AU hethy binesba lil bit heki
                for (Reservation reservation : c.getReservations()) {
                    if (reservation.getAnneeUniversitaire().isBefore(dateFinAU) && reservation.getAnneeUniversitaire().isAfter(dateDebutAU)) {
                        numReservation++;
                    }
                }
                // nvérifi bil type w nombre des places elli l9ahom fer8in fi kol bit
                if (c.getTypeC().equals(TypeChambre.SIMPLE) && numReservation == 0) listChambreDispo.add(c);
                else if (c.getTypeC().equals(TypeChambre.DOUBLE) && numReservation < 2) {
                    listChambreDispo.add(c);
                } else if (c.getTypeC().equals(TypeChambre.TRIPLE) && numReservation < 3) {
                    listChambreDispo.add(c);
                }
            }
        }
        return listChambreDispo;
    }

    @Override
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() + " ayant une capacité " + b.getCapaciteBloc());
            if (!b.getChambres().isEmpty()) {
                log.info("La liste des chambres pour ce bloc: ");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() + " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("********************");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = (double) (repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100) / totalChambre;
        double pDouble = (double) (repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100) / totalChambre;
        double pTriple = (double) (repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100) / totalChambre;
        log.info("Nombre total des chambre: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à " + pTriple);

    }

    @Override
    public void nbpDISPO() {
        // Récupérer l’année universitaire actuelle
        LocalDate[] academicYear = getAcademicYear();
        LocalDate dateDebutAU = academicYear[0];
        LocalDate dateFinAU = academicYear[1];

        repo.findAll().forEach(c -> logAvailableSpots(c, dateDebutAU, dateFinAU));
    }

    // Méthode pour récupérer l’année universitaire actuelle
    private LocalDate[] getAcademicYear() {
        int year = LocalDate.now().getYear() % 100;
        int currentYear = Integer.parseInt("20" + year);

        if (LocalDate.now().getMonthValue() > 7) {
            return new LocalDate[]{LocalDate.of(currentYear, 9, 15), LocalDate.of(currentYear + 1, 6, 30)};
        } else {
            return new LocalDate[]{LocalDate.of(currentYear - 1, 9, 15), LocalDate.of(currentYear, 6, 30)};
        }
    }

    // Méthode pour afficher les places disponibles d’une chambre
    private void logAvailableSpots(Chambre c, LocalDate dateDebutAU, LocalDate dateFinAU) {
        long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                c.getIdChambre(), true, dateDebutAU, dateFinAU);

        int maxCapacity = getMaxCapacity(c.getTypeC());
        int availableSpots = maxCapacity - (int) nbReservation;

        if (availableSpots > 0) {
            log.info("Le nombre de places disponible pour la chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est " + availableSpots);
        } else {
            log.info("La chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est complète");
        }
    }

    // Méthode pour récupérer la capacité maximale d’une chambre
    private int getMaxCapacity(TypeChambre type) {
        switch (type) {
            case SIMPLE: return 1;
            case DOUBLE: return 2;
            case TRIPLE: return 3;
            default: return Integer.MAX_VALUE;
        }
    }

}
