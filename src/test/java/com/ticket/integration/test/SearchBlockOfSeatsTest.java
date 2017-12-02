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

public class SearchBlockOfSeatsTest {

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
    public void testAllRowsAreFilledWithRoundOneExceptThreeSeats() throws Exception {
        SeatHold seatHold1 = ticketService.findAndHoldSeats(7, "xyz-customer@mail.com");

        IntStream.rangeClosed(1, 25)
                .forEach(i -> {
                    int noOfSeats = 10;

                    if (i == 15 || i == 20) {
                        noOfSeats = 6;
                    }
                    SeatHold xx = ticketService.findAndHoldSeats(noOfSeats, "xyz-customer@mail.com");
                    Assert.assertNotNull("Returned null, most likely all seats are booked", xx);
                });

        SeatHold seatHold3 = ticketService.findAndHoldSeats(3, "xyz-customer@mail.com");
        SeatHold seatHold4 = ticketService.findAndHoldSeats(4, "xyz-customer@mail.com");
        SeatHold seatHold5 = ticketService.findAndHoldSeats(4, "xyz-customer@mail.com");

        Assert.assertEquals("Unexpected number of available seats", 3, seatHold3.getSeatNumbers().size());
        Assert.assertEquals("Unexpected number of available seats", 4, seatHold4.getSeatNumbers().size());
        Assert.assertEquals("Unexpected number of available seats", 4, seatHold5.getSeatNumbers().size());
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