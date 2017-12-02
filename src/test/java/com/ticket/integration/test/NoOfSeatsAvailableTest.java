package com.ticket.integration.test;

import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Created by xkt676 on 11/29/17.
 */
public class NoOfSeatsAvailableTest {

    private TicketService ticketService;

    @Before
    public void init() throws Exception {
        cleanUpRepositoryState();
        ticketService = new TicketServiceImpl();
    }

    @After
    public void destroy() throws Exception {
        ticketService = null;
    }

    @Test
    public void testNumSeatsAvailable() throws Exception {
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals("Unexpected number of available seats", 260, availableSeats);
        SeatHold seatHold = ticketService.findAndHoldSeats(10, "xyz-customer@mail.com");
        Assert.assertEquals("Unexpected number of available seats", 10, seatHold.getSeatNumbers().size());
        Assert.assertEquals("Unexpected number of available seats", 250, ticketService.numSeatsAvailable());
    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}
