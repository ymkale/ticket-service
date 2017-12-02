package com.ticket.finder;

import com.ticket.model.Seat;
import com.ticket.repository.SeatRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ticket.model.TicketAppConstants.MAX_SEATS_IN_EACH_ROW;

public class SeatFindHelper {

    private SeatRepository seatRepository = SeatRepository.getInstance();

    /**
     * Returns a list of allocated seat numbers for the given row
     *
     * @param noOfSeats
     * @param row
     * @return
     */
    public List<String> holdSeats(String row, int noOfSeats) {

        if (!seatRepository.containsKey(row)) {
            seatRepository.put(row,
                    new Seat.SeatBuilder()
                            .setIsOnHold(false)
                            .setIsReserved(false)
                            .setNoOfSeats(MAX_SEATS_IN_EACH_ROW)
                            .setFromSeatNo(0)
                            .buildSeats()
                            .stream()
                            .collect(Collectors.toList()));
        }

        Set<Seat> allottedSeats = seatRepository.get(row)
                .stream()
                .filter(seat1 -> !seat1.isOnHold() && !seat1.isReserved())
                .sorted((o1, o2) -> Integer.compare(Integer.valueOf(o1.getId()), Integer.valueOf(o2.getId())))
                .limit(noOfSeats)
                .collect(Collectors.toSet());

        allottedSeats.stream().forEach(seat1 -> seat1.setIsOnHold(true));

        return allottedSeats.stream()
                .map(seat -> Integer.valueOf(seat.getId()))
                .sorted()
                .map(id -> row + id)
                .collect(Collectors.toList());
    }

    /**
     * Holds seats wherever available in any row and Returns the size of the found seats
     *
     * @param row
     * @param availableSeats
     * @param noOfSeats
     * @return
     */
    public int grabSeatsWhereverAvailable(String row, List<String> availableSeats, int noOfSeats) {

        Set<Seat> seats = seatRepository.get(row)
                .stream()
                .filter(seat -> !seat.isOnHold() && !seat.isReserved())
                .limit(noOfSeats)
                .collect(Collectors.toSet());

        seats.stream()
                .forEach(seat -> seat.setIsOnHold(true));

        availableSeats.addAll(seats.stream()
                .map(seat -> Integer.valueOf(seat.getId()))
                .sorted()
                .map(id -> String.valueOf(row) + id)
                .collect(Collectors.toList()));
        return seats.size();
    }
}
