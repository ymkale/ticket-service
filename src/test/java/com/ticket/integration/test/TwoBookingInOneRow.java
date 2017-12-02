package com.ticket.integration.test;

import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

public class TwoBookingInOneRow {

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
    public void testAllRowsAreFilledExceptThreeNonAdjacent() throws Exception {
        SeatHold seatHold1 = ticketService.findAndHoldSeats(7, "xyz-customer@mail.com");
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("A1", "A2", "A3", "A4", "A5", "A6", "A7"), seatHold1.getSeatNumbers());

        SeatHold seatHold2 = ticketService.findAndHoldSeats(2, "xyz-customer@mail.com");
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("A8", "A9"), seatHold2.getSeatNumbers());

        String confirmationId = ticketService.reserveSeats(seatHold2.getHoldId(), "xyz-customer@mail.com");
        Assert.assertNotNull("Null returned, expected non-null value", confirmationId);
        Assert.assertFalse("Unexpected response,expecting confirmationId", confirmationId.contains("expired"));

        SeatHold seatHold3 = ticketService.findAndHoldSeats(3, "xyz-customer@mail.com");
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("B1", "B2", "B3"), seatHold3.getSeatNumbers());
    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}
