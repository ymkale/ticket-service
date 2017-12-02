package com.ticket.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;


/**
 * Created by xkt676 on 11/26/17.
 */
public class SearchAndHoldSeatsTest {

    private TicketService ticketService;
    ObjectMapper mapper = new ObjectMapper();

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
    public void testFindAndHoldSeats() throws Exception {
        ticketService = new TicketServiceImpl();
        int totalSeats = ticketService.numSeatsAvailable();
        SeatHold seatHold = ticketService.findAndHoldSeats(3, "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, expected non-null value ", seatHold);
        Assert.assertEquals("Unexpected number of seats on hold", 3, seatHold.getSeatNumbers().size());
        Assert.assertEquals("Unexpected number of seats available", totalSeats - 3, ticketService.numSeatsAvailable());
    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}