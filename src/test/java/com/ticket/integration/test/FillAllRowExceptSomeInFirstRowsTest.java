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

public class FillAllRowExceptSomeInFirstRowsTest {

    private TicketService ticketService;

    @Before
    public void init() throws Exception {
        ticketService = new TicketServiceImpl();
    }

    @After
    public void destroy() throws Exception {
        ticketService = null;
        cleanUpRepositoryState();
    }

    @Test
    public void testAllRowsAreFilledWithRoundOneAndTwo() throws Exception {
        ticketService = new TicketServiceImpl();

        SeatHold book1 = ticketService.findAndHoldSeats(4, "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, most likely all seats are booked", book1);
        ticketService.reserveSeats(book1.getHoldId(), "xyz-customer@mail.com");


        SeatHold book2 = ticketService.findAndHoldSeats(3, "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, most likely all seats are booked", book2);
        ticketService.reserveSeats(book2.getHoldId(), "xyz-customer@mail.com");

        IntStream.rangeClosed(1, 24)
                .forEach(i -> {
                    SeatHold xx = ticketService.findAndHoldSeats(10, "xyz-customer@mail.com");
                    Assert.assertNotNull("Returned null, most likely all seats are booked", xx);
                    int remainingSeats = ticketService.numSeatsAvailable();
                    ticketService.reserveSeats(xx.getHoldId(), "xyz-customer@mail.com");
                });


        SeatHold book3 = ticketService.findAndHoldSeats(2, "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, most likely all seats are booked", book3);
        ticketService.reserveSeats(book3.getHoldId(), "xyz-customer@mail.com");


        SeatHold book4 = ticketService.findAndHoldSeats(2, "xyz-customer@mail.com");
        ticketService.reserveSeats(book4.getHoldId(), "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, most likely all seats are booked", book4);

        SeatHold book5 = ticketService.findAndHoldSeats(4, "xyz-customer@mail.com");
        ticketService.reserveSeats(book5.getHoldId(), "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, most likely all seats are booked", book5);

        SeatHold book6 = ticketService.findAndHoldSeats(3, "xyz-customer@mail.com");
        ticketService.reserveSeats(book6.getHoldId(), "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, most likely all seats are booked", book6);

        SeatHold book7 = ticketService.findAndHoldSeats(2, "xyz-customer@mail.com");
        Assert.assertNotNull("Returned null, most likely all seats are booked", book7);
        ticketService.reserveSeats(book7.getHoldId(), "xyz-customer@mail.com");


        Assert.assertEquals("Unexpected number of seats available", 0, ticketService.numSeatsAvailable());

    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}
