package com.ticket.finder;

import com.ticket.model.SeatHoldDetails;
import com.ticket.repository.SeatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.ticket.model.TicketAppConstants.*;

public class SeatsFinder {

    private SeatRepository seatRepository = SeatRepository.getInstance();
    private SeatFindHelper seatFindHelper = new SeatFindHelper();
    private char rowId = 'A';
    private ReadWriteLock lock = new ReentrantReadWriteLock(true); //fair mode

    /**
     * Returns a list of best available seats.
     *
     * @param noOfSeats
     * @return
     */
    public List<String> findAndHoldSeats(int noOfSeats) {
        try {
            lock.writeLock().lock();
            boolean seatsFound = searchRowsForSeatsTogether(noOfSeats);
            if (seatsFound) {
                return seatFindHelper.holdSeats(String.valueOf(rowId), noOfSeats);
            } else {
                rowId = 'A'; //Reset back to first row
                List<String> seatsNumbers = new ArrayList<>();
                while (rowId <= LAST_ROW && noOfSeats > 0) {
                    int numSeatsFound = seatFindHelper.grabSeatsWhereverAvailable(String.valueOf(rowId), seatsNumbers, noOfSeats);
                    noOfSeats = noOfSeats - numSeatsFound;
                    rowId++;
                }
                return seatsNumbers;
            }
        } catch (Exception ex) {
            //Or we could catch a specific exception
            throw ex;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Reserves specified seatNumbers
     *
     * @param seatNumbers
     * @return
     */

    public boolean reserve(List<String> seatNumbers) throws Exception {
        try {
            lock.writeLock().lock();
            seatNumbers.stream()
                    .forEach(rowNumber ->
                                    seatRepository.get(String.valueOf(rowNumber.charAt(0)))
                                            .stream()
                                            .filter(seat -> seat.getId().equalsIgnoreCase(String.valueOf(rowNumber.charAt(1))))
                                            .forEach(seat1 -> seat1.setIsReserved(true))
                    );
            return true;
        } catch (Exception ex) {
            throw ex;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Release seats for re-booking
     *
     * @param seats
     */
    public void releaseHold(List<String> seats) throws Exception {
        try {
            lock.writeLock().lock();
            seats.stream()
                    .map(seatNumber -> seatNumber.charAt(0))
                    .collect(Collectors.toSet())
                    .forEach(row ->
                            seatRepository.get(String.valueOf(row)).stream()
                                    .forEach(seat -> seat.setIsOnHold(false)));
        } catch (Exception ex) {
            throw ex;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns number of available seats for hold/reserve
     *
     * @return
     */
    public Long numSeatsAvailable() throws Exception {
        try {
            lock.readLock().lock();
            return (26 * MAX_SEATS_IN_EACH_ROW) - seatRepository.noOfSeats().stream().mapToLong(value ->
                            value.stream().filter(seat -> seat.isReserved() || seat.isOnHold()).count()
            ).sum();
        } catch (Exception ex) {
            //log something here
            throw ex;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Hold seats for holdId. Seat will be removed from availability pool
     *
     * @param seatHoldDetails
     * @param seatNumbers
     * @return
     */
    public boolean saveSeatsForHoldId(SeatHoldDetails seatHoldDetails, List<String> seatNumbers) throws Exception {
        try {
            lock.writeLock().lock();

            // Check if holdId is already exits in the map, it shouldn't
            if (seatRepository.seatsOnHoldContains(seatHoldDetails.getHoldId()) ||
                    seatRepository.seatHoldDetailsContains(seatHoldDetails.getHoldId())) {
                return false; //throw new RuntimeException("Duplicate seatHoldDetails exception");
            } else {
                seatRepository.saveSeatsOnHold(seatHoldDetails.getHoldId(), seatNumbers);
                seatRepository.saveHoldIdDetails(seatHoldDetails.getHoldId(), seatHoldDetails);
                return true;
            }
        } catch (Exception ex) {
            //log something here
            throw ex;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns a List Of seats for given holdId
     *
     * @param holdId
     * @return
     */
    public List<String> getSeatsNumbersById(int holdId) {
        return seatRepository.seatNumbersByHoldId(holdId);
    }

    /**
     * Returns confirmation by holdId
     *
     * @param holdId
     * @return
     */
    public SeatHoldDetails getHoldById(int holdId) {
        return seatRepository.seatHoldDetailsByHoldId(holdId);
    }

    /**
     * Clears expired seatHold
     */
    public void cleanUpExpiredSeatHold() throws Exception {
        seatRepository.cleanUpExpiredSeatHold();
    }

    private boolean searchRowsForSeatsTogether(int noOfSeats) {
        rowId = 'A'; //Reset to first row
        while (rowId <= LAST_ROW) {
            boolean rowHasBlockOfEmptySeats = searchRowsForBlockOfEmptySeats(noOfSeats);
            if (rowHasBlockOfEmptySeats) {
                return true; //Stop searching and return.
            }
            if (rowId > MID_ROW) {
                FILL_UP_PERCENTAGE = 100;
            }
            rowId++;
        }
        return false;
    }

    private boolean searchRowsForBlockOfEmptySeats(int noOfSeats) {
        if (seatRepository.get(String.valueOf(rowId)) == null) {
            return true;
        } else {
            long count = seatRepository.get(String.valueOf(rowId)).stream().filter(seat -> seat.isReserved() || seat.isOnHold()).count();
            return !((count == MAX_SEATS_IN_EACH_ROW ||
                    (count * 100) / MAX_SEATS_IN_EACH_ROW >= FILL_UP_PERCENTAGE ||
                    (MAX_SEATS_IN_EACH_ROW - count < noOfSeats)));
        }
    }
}
