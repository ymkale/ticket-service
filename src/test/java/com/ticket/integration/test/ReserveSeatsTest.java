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
 * Created by xkt676 on 11/29/17.
 */
public class ReserveSeatsTest {

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
    public void testReserveSeats() throws Exception {
        ticketService = new TicketServiceImpl();
        SeatHold seatHold = ticketService.findAndHoldSeats(4, "xyz-customer@mail.com");
        Assert.assertEquals("unexpected number of seats hold", 4, seatHold.getSeatNumbers().size());
        Thread.sleep(1000);  // hold is valid for 15secs
        String confirmationId = ticketService.reserveSeats(seatHold.getHoldId(), "xyz-customer@mail.com");
        Assert.assertFalse(confirmationId.contains("expired"));
    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}
