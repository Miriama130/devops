package tn.esprit.spring;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.util.Optional;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        // Create a mock Reservation instance
        Reservation reservation = new Reservation();

        // Mockito 1: Mock save behavior
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // JUnit 1: Assert the returned reservation is as expected
        Reservation result = reservationService.addOrUpdate(reservation);
        assertEquals(reservation, result);

        // Mockito 2: Verify repository interaction
        verify(reservationRepository, times(1)).save(reservation);
    }


    @Test
    void testFindById() {
        // Create a mock Reservation object
        String id = "123";
        Reservation reservation = new Reservation();

        // Mock the behavior of the findById method
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // Call the service method
        Reservation result = reservationService.findById(id);

        // Assert the result
        assertEquals(reservation, result);

        // Verify the interaction with the repository
        verify(reservationRepository, times(1)).findById(id);
    }

}