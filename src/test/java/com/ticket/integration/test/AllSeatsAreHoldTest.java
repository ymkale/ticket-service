package com.ticket.integration.test;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.stream.IntStream;

public class AllSeatsAreHoldTest {

    private TicketService ticketService;
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() throws Exception {
        System.out.println("Setting up ...");
        ticketService = new TicketServiceImpl();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @After
    public void destroy() throws Exception {
        System.out.println("Tearing down ...");
        ticketService = null;
        cleanUpRepositoryState();
    }

    @Test
    public void testAllRowsAreFilledWithRoundOneAndTwo() throws Exception {
        Assert.assertEquals("Unexpected number of seats available", 260, ticketService.numSeatsAvailable());

        IntStream.rangeClosed(1, 26)
                .forEach(i -> {
                    SeatHold xx = ticketService.findAndHoldSeats(10, "xyz-customer@mail.com");
                    Assert.assertNotNull("Returned null, most likely all seats are booked", xx);
                });
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
