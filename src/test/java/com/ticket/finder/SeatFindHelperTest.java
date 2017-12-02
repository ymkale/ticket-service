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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SeatFindHelperTest {

    @InjectMocks
    private SeatFindHelper seatFindHelper = new SeatFindHelper();

    @Mock
    private SeatRepository seatRepository;

    @Before
    public void init() throws Exception {
    }

    @After
    public void destroy() throws Exception {
        seatFindHelper = null;
    }

    @Test
    public void testHoldsSeatsWhenRowIsEmpty() throws Exception {
        String row = "A";
        Mockito.when(seatRepository.containsKey(row)).thenReturn(false);

        Set<Seat> seatSet = new Seat.SeatBuilder()
                .setIsOnHold(false)
                .setIsReserved(false)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats();

        Mockito.when(seatRepository.get(row)).thenReturn(seatSet
                .stream()
                .collect(Collectors.toList()));
        List<String> seats = seatFindHelper.holdSeats(row, 2);
        Assert.assertEquals("Unexpected Number of seats", 2, seats.size());
        assertThat(seats, is(Arrays.asList("A1", "A2")));
    }

    @Test
    public void testHoldSeatsWhenNotEmpty() throws Exception {
        String row = "A";
        Mockito.when(seatRepository.containsKey(row)).thenReturn(true);
        Set<Seat> seatSet = new Seat.SeatBuilder()
                .setIsOnHold(false)
                .setIsReserved(false)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats();

        Mockito.when(seatRepository.get(row)).thenReturn(seatSet
                .stream()
                .collect(Collectors.toList()));
        List<String> seats = seatFindHelper.holdSeats(row, 2);
        Assert.assertEquals("Unexpected Number of seats", 2, seats.size());
        assertThat(seats, is(Arrays.asList("A1", "A2")));
    }


    @Test
    public void testHoldSeatsWhenSomeSeatsAreOnHold() throws Exception {
        String row = "A";

        Set<Seat> seatSet = new Seat.SeatBuilder()
                .setIsOnHold(false)
                .setIsReserved(false)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats();

        List<Seat> seats = seatSet
                .stream()
                .collect(Collectors.toList());

        seats.stream()
                .sorted((o1, o2) -> Integer.compare(Integer.valueOf(o1.getId()), Integer.valueOf(o2.getId())))
                .limit(2)
                .forEach(seat1 -> seat1.setIsOnHold(true));

        Mockito.when(seatRepository.containsKey(row)).thenReturn(true);

        Mockito.when(seatRepository.get(row))
                .thenReturn(seats)
                .thenReturn(seats)
                .thenReturn(seats);
        List<String> availableSeats = seatFindHelper.holdSeats(row, 2);
        Assert.assertEquals("Unexpected Number of seats", 2, availableSeats.size());
        System.out.println(" :" + availableSeats.toString());
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("A3", "A4"), availableSeats);
    }

    @Test
    public void testHoldSeatsWhenLast2SeatsNotAvailable() throws Exception {
        String row = "A";

        Set<Seat> seatSet = new Seat.SeatBuilder()
                .setIsOnHold(false)
                .setIsReserved(false)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats();
        List<Seat> seats = seatSet
                .stream()
                .collect(Collectors.toList());

        seats.stream()
                .sorted((o1, o2) -> Integer.compare(Integer.valueOf(o1.getId()), Integer.valueOf(o2.getId())))
                .filter(seat -> Integer.valueOf(seat.getId()) >= 8)
                .forEach(seat1 -> seat1.setIsOnHold(true));

        Mockito.when(seatRepository.containsKey(row)).thenReturn(true);

        Mockito.when(seatRepository.get(row))
                .thenReturn(seats)
                .thenReturn(seats)
                .thenReturn(seats);
        List<String> availableSeats = seatFindHelper.holdSeats(row, 2);
        Assert.assertEquals("Unexpected Number of seats", 2, availableSeats.size());
        System.out.println(" :" + availableSeats.toString());
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("A1", "A2"), availableSeats);
    }

    @Test
    public void testHoldSeatsWhenLastMiddleSeatsNotAvailable() throws Exception {
        String row = "A";
        Set<Seat> seatSet = new Seat.SeatBuilder()
                .setIsOnHold(false)
                .setIsReserved(false)
                .setNoOfSeats(10)
                .setFromSeatNo(0)
                .buildSeats();
        List<Seat> seats = seatSet
                .stream()
                .collect(Collectors.toList());

        seats.stream()
                .sorted((o1, o2) -> Integer.compare(Integer.valueOf(o1.getId()), Integer.valueOf(o2.getId())))
                .filter(seat -> (Integer.valueOf(seat.getId()) == 1) ||
                        (Integer.valueOf(seat.getId()) == 2) ||
                        (Integer.valueOf(seat.getId()) == 3) ||
                        (Integer.valueOf(seat.getId()) == 7))
                .forEach(seat1 -> seat1.setIsOnHold(true));

        Mockito.when(seatRepository.containsKey(row)).thenReturn(true);

        Mockito.when(seatRepository.get(row))
                .thenReturn(seats)
                .thenReturn(seats)
                .thenReturn(seats);
        List<String> availableSeats = seatFindHelper.holdSeats(row, 2);
        Assert.assertEquals("Unexpected Number of seats", 2, availableSeats.size());
        System.out.println(" :" + availableSeats.toString());
        Assert.assertEquals("Unexpected seats returned", Arrays.asList("A4", "A5"), availableSeats);
    }

}