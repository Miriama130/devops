package tn.esprit.spring.Schedular;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.spring.Services.Chambre.IChambreService;
import tn.esprit.spring.Services.Reservation.IReservationService;

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
