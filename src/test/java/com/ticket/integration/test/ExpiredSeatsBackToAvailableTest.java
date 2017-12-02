package com.ticket.integration.test;

import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

public class ExpiredSeatsBackToAvailableTest {

    private TicketService ticketService;

    @Before
    public void init() throws Exception {
        ticketService = new TicketServiceImpl();
    }

    @After
    public void destroy() throws Exception {
        System.out.println("Tearing down ...");
        ticketService = null;
        cleanUpRepositoryState();
    }

    //UnComment line from this method to test expired seats scenario
    @Test
    public void testReBookingExpiredSeats() throws Exception {
        SeatHold seatHold = ticketService.findAndHoldSeats(10, "xyz-customer@mail.com");
        Assert.assertNotNull("Null returned, expected non-null value", seatHold);
        Assert.assertEquals("Unexpected number of available seats", 250, ticketService.numSeatsAvailable());
        //Thread.sleep(30000);  // wait for 30sec..hold is valid for 60secs
        String confirmationId = ticketService.reserveSeats(seatHold.getHoldId(), "xyz-customer@mail.com");
        Assert.assertFalse("expected expired/NotFound message.", confirmationId.contains("expired"));
       // Assert.assertEquals("Unexpected number of available seats", 250, seatHold.getSeatNumbers().size());
    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}
