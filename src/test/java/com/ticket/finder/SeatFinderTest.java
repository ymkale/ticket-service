package com.ticket.finder;

import com.ticket.model.Seat;
import com.ticket.repository.SeatRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SeatFinderTest {

    @InjectMocks
    private SeatsFinder seatsFinder = new SeatsFinder();

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatFindHelper seatFindHelper;

    @Before
    public void init() throws Exception {
    }

    @After
    public void destroy() throws Exception {
        seatsFinder = null;
    }


    @Test
    public void testFindAndHoldSeatsFullRowEmpty() throws Exception {
        List<String> seats = Arrays.asList("A1", "A2");
        when(seatFindHelper.holdSeats(String.valueOf('A'), 2)).thenReturn(seats);
        List<String> list = seatsFinder.findAndHoldSeats(2);
        Assert.assertEquals(2, list.size());
    }


    @Test
    public void testHoldSeatsInRowWithSomeNotAvailableSeats() throws Exception {
        List<String> seats = Arrays.asList("A3", "A4");
        Set<Seat> seats1 = new Seat.SeatBuilder()
                .setIsReserved(false)
                .setIsOnHold(false)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats();

        seats1.stream()
                .forEach(seat -> {
                    if (Integer.valueOf(seat.getId()) == 1 || Integer.valueOf(seat.getId()) == 3) {
                        seat.setIsReserved(true);
                    }
                });

        when(seatRepository.get("A")).thenReturn(seats1.stream()
                .collect(Collectors.toList()));

        when(seatFindHelper.holdSeats(String.valueOf('A'), 2)).thenReturn(seats);
        List<String> list = seatsFinder.findAndHoldSeats(2);
        verify(seatFindHelper, atLeastOnce()).holdSeats(eq("A"), eq(2));
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("A3", "A4"), list);
    }

    @Test
    public void testHoldSeatsWhenFirstRowFull() throws Exception {
        List<String> seats = Arrays.asList("B1", "B2");

        when(seatRepository.get("A")).thenReturn(new Seat.SeatBuilder()
                .setIsReserved(true)
                .setIsOnHold(true)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats()
                .stream()
                .collect(Collectors.toList()));

        when(seatRepository.get("B")).thenReturn(new Seat.SeatBuilder()
                .setIsReserved(false)
                .setIsOnHold(false)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats()
                .stream()
                .collect(Collectors.toList()));

        when(seatFindHelper.holdSeats(String.valueOf('B'), 2)).thenReturn(seats);

        List<String> availableSeats = seatsFinder.findAndHoldSeats(2);

        verify(seatFindHelper, times(1)).holdSeats(eq("B"), eq(2));

        Assert.assertEquals(2, availableSeats.size());
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("B1", "B2"), availableSeats);
    }

}