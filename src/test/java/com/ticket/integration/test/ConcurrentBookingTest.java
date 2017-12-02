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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConcurrentBookingTest {

    private TicketService ticketService;
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() throws Exception {
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
    public void testMultipleUsersTryingToBookAtSameTime() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Callable<List<String>> task = () -> {
            SeatHold seatHold = ticketService.findAndHoldSeats(1, "last-customer@mail.com");
            return seatHold.getSeatNumbers();
        };

        List<Callable<List<String>>> holdingRequests = new ArrayList<>();
        IntStream.rangeClosed(1, 10)
                .forEach(i -> holdingRequests.add(task));

        List<String> results = executorService.invokeAll(holdingRequests).stream()
                .map(item -> {
                    try {
                        return item.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ArrayList<String>();
                    }
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
        Assert.assertTrue("Duplicated seats returned", results.stream().allMatch(new HashSet<>()::add));
    }

    private void cleanUpRepositoryState() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // This may be not an good option but needed a way to clear up state before the test
        Class cls = Class.forName("com.ticket.repository.SeatRepository");
        Field field = cls.getDeclaredField("seatRepository");
        field.setAccessible(true);
        field.set(null, null);
    }
}