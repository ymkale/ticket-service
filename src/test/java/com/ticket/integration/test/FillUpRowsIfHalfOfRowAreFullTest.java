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

public class FillUpRowsIfHalfOfRowAreFullTest {
    //System.out.println("seatHold:" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(seatHold));

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
    public void testToFillUpRowsMoreIfRowReachedToMid() throws Exception {
        IntStream.rangeClosed(1, 10)
                .forEach(i -> {
                    SeatHold xx = ticketService.findAndHoldSeats(10, "xyz-customer@mail.com");
                    Assert.assertEquals("Unexpected number of available seats", 10, xx.getSeatNumbers().size());
                });

        SeatHold seatHold3 = ticketService.findAndHoldSeats(2, "xyz-customer@mail.com");
        SeatHold seatHold4 = ticketService.findAndHoldSeats(2, "xyz-customer@mail.com");
        SeatHold seatHold5 = ticketService.findAndHoldSeats(1, "xyz-customer@mail.com");

        Assert.assertEquals("Unexpected number of available seats", 2, seatHold3.getSeatNumbers().size());
        Assert.assertEquals("Unexpected number of available seats", 2, seatHold4.getSeatNumbers().size());
        Assert.assertEquals("Unexpected number of available seats", 1, seatHold5.getSeatNumbers().size());


    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}
