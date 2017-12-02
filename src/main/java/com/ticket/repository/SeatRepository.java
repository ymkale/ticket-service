package com.ticket.repository;

import com.ticket.model.Seat;
import com.ticket.model.SeatHoldDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeatRepository {

    private static SeatRepository seatRepository;
    private Map<String, List<Seat>> rowSeatMap = new HashMap<>();
    private Map<Integer, List<String>> seatsOnHold = new HashMap<>();
    private Map<Integer, SeatHoldDetails> seatHoldDetails = new HashMap<>();

    public static SeatRepository getInstance() {
        if (seatRepository == null) {
            synchronized (SeatRepository.class) {
                seatRepository = new SeatRepository();
            }
        }
        return seatRepository;
    }

    public List<Seat> get(String row) {
        return rowSeatMap.get(row);
    }

    public boolean containsKey(String row) {
        return rowSeatMap.containsKey(row);
    }

    public List<Seat> put(String key, List<Seat> seats) {
        return rowSeatMap.put(key, seats);
    }

    public Collection<List<Seat>> noOfSeats() {
        try {
            return rowSeatMap.values();

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * @param holdId
     * @return
     */
    public SeatHoldDetails seatHoldDetailsByHoldId(Integer holdId) {
        return seatHoldDetails.get(holdId);
    }

    /**
     * Returns list of seat numbers associated with given holdId
     *
     * @param holdId
     * @return
     */
    public List<String> seatNumbersByHoldId(Integer holdId) {
        return seatsOnHold.get(holdId);
    }

    /**
     * Save seatHoldDetails with specified holdId
     *
     * @param key
     * @param seatHoldDetails
     * @return
     */
    public SeatHoldDetails saveHoldIdDetails(Integer key, SeatHoldDetails seatHoldDetails) {
        return this.seatHoldDetails.put(key, seatHoldDetails);
    }

    /**
     * Save seats on hold with specified holdId
     *
     * @param holdId
     * @param seatsNumbers
     * @return
     */
    public List<String> saveSeatsOnHold(Integer holdId, List<String> seatsNumbers) {
        return seatsOnHold.put(holdId, seatsNumbers);
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * holdId
     *
     * @param holdId
     * @return
     */
    public boolean seatsOnHoldContains(Integer holdId) {
        return seatsOnHold.containsKey(holdId);
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * holdId
     *
     * @param holdId
     * @return
     */
    public boolean seatHoldDetailsContains(Integer holdId) {
        return seatHoldDetails.containsKey(holdId);
    }

    /**
     * Clears expired seats
     */
    public void cleanUpExpiredSeatHold() throws Exception {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(() -> seatHoldDetails.entrySet()
                    .removeIf(entry -> entry.getValue().getDateCreated().isBefore(LocalDateTime.now())));
            executor.shutdown();
        } catch (Exception ex) {
            //log something here
            throw ex;
        }
    }
}
