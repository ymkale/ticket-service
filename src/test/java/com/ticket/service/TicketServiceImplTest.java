package com.ticket.service;

import com.ticket.finder.SeatsFinder;
import com.ticket.model.SeatHold;
import com.ticket.model.SeatHoldDetails;
import com.ticket.repository.SeatRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    @InjectMocks
    private TicketServiceImpl ticketService = new TicketServiceImpl();

    @Mock
    private SeatsFinder seatsFinder;

    @Mock
    private SeatRepository seatRepository;

    @Before
    public void init() throws Exception {
    }

    @After
    public void destroy() throws Exception {
        ticketService = null;
    }

    @Test
    public void testNumSeatsAvailable() throws Exception {
        when(seatsFinder.numSeatsAvailable()).thenReturn(45l);
        int numOfSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals("Unexpected number of seats returned", 45, numOfSeats);
    }

    // @Test(expected = RuntimeException.class)
    @Test()
    public void testFailedToGetNumSeatsAvailable() throws Exception {
        when(seatsFinder.numSeatsAvailable()).thenReturn(null);
        int numOfSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals("Unexpected number of seats returned", 0, numOfSeats);
    }

    @Test
    public void testFindAndHoldSeats() throws Exception {
        when(seatsFinder.numSeatsAvailable()).thenReturn(260l);
        when(seatsFinder.findAndHoldSeats(2)).thenReturn(Arrays.asList("A1", "A2"));
        when(seatsFinder.getSeatsNumbersById(anyInt())).thenReturn(Arrays.asList("A1", "A2"));
        when(seatsFinder.saveSeatsForHoldId(anyObject(), eq(Arrays.asList("A1", "A2")))).thenReturn(true);
        SeatHold seatHold = ticketService.findAndHoldSeats(2, "xyz@mail.com");

        Assert.assertNotNull("Null returned, expected non-null value", seatHold);
        Assert.assertNotNull("Seat hold id is null,expected non-null id", seatHold.getHoldId());
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("A1", "A2"), seatHold.getSeatNumbers());
    }

    @Test
    public void testFindAndHoldSeatsNotAvailable() throws Exception {
        when(seatsFinder.numSeatsAvailable()).thenReturn(0l);
        when(seatsFinder.findAndHoldSeats(2)).thenReturn(Arrays.asList("A1", "A2"));

        SeatHold seatHold = ticketService.findAndHoldSeats(2, "xyz@mail.com");

        Assert.assertNull(seatHold);
    }

    @Test
    public void testReserveSeats() throws Exception {
        int dummyHoldId = 2323232;
        List<String> seatNumbers = Arrays.asList("B2", "B3");
        SeatHoldDetails seatHoldDetails = new SeatHoldDetails(dummyHoldId);
        seatHoldDetails.setDateCreated(LocalDateTime.now().plusMinutes(1));

        when(seatRepository.seatHoldDetailsByHoldId(dummyHoldId)).thenReturn(seatHoldDetails);
        when(seatRepository.seatNumbersByHoldId(dummyHoldId)).thenReturn(seatNumbers);
        when(seatsFinder.reserve(seatNumbers)).thenReturn(true);

        String confirmationId = ticketService.reserveSeats(dummyHoldId, "xyz@mail.com");

        Assert.assertNotNull("Null returned, expected non-null value", confirmationId);
    }

    @Test
    public void testReserveSeatsCouldNotReserve() throws Exception {
        int dummyHoldId = 2323232;
        List<String> seatNumbers = Arrays.asList("B2", "B3");
        SeatHoldDetails seatHoldDetails = new SeatHoldDetails(dummyHoldId);
        seatHoldDetails.setDateCreated(LocalDateTime.now().plusMinutes(1));

        when(seatsFinder.getHoldById(dummyHoldId)).thenReturn(seatHoldDetails);
        when(seatsFinder.getSeatsNumbersById(dummyHoldId)).thenReturn(seatNumbers);
        when(seatsFinder.reserve(seatNumbers)).thenReturn(false);

        String confirmationId = ticketService.reserveSeats(dummyHoldId, "xyz@mail.com");
        Assert.assertNull(confirmationId);
    }

    @Test
    public void testReserveSeatsExpiredHold() {
        int dummyHoldId = 2323232;
        SeatHoldDetails seatHoldDetails = new SeatHoldDetails(dummyHoldId);
        seatHoldDetails.setDateCreated(LocalDateTime.now().minusMinutes(1));

        String confirmationId = ticketService.reserveSeats(dummyHoldId, "xyz@mail.com");

        Assert.assertNotNull("Null returned, expected non-null value", confirmationId);
        Assert.assertTrue("expected expired/NotFound message.", confirmationId.contains("expired"));
    }
}
