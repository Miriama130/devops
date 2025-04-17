package tn.esprit.spring.schedular;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.spring.services.Chambre.IChambreService;
import tn.esprit.spring.services.Reservation.IReservationService;

@Component
@AllArgsConstructor
public class Schedular {

    IChambreService iChambreService;
   IReservationService iReservationService;

   @Scheduled(cron = "0 * * * * *")
    void service1() {
        iChambreService.listeChambresParBloc();
   }

}
