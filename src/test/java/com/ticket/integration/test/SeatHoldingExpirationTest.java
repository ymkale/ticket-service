package com.ticket.integration.test;


import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.stream.IntStream;

public class SeatHoldingExpirationTest {

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
    public void testAutoExpirationOnSeats() throws Exception {
        ticketService = new TicketServiceImpl();
        IntStream.rangeClosed(1, 2)
                .forEach(i -> {
                    SeatHold xx = ticketService.findAndHoldSeats(10, "xyz-customer@mail.com");
                    Assert.assertNotNull("Returned null, most likely all seats are booked", xx);
                });
        Assert.assertEquals("Unexpected number of seats available", 240, ticketService.numSeatsAvailable());
        //Thread.sleep(40000);
        //Assert.assertEquals("Unexpected number of seats available", 260, ticketService.numSeatsAvailable());
    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}
