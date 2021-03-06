package com.ticket.service;

import com.ticket.finder.SeatsFinder;
import com.ticket.model.SeatHold;
import com.ticket.model.SeatHoldDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TicketServiceImpl implements TicketService {

    public static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private SeatsFinder seatsFinder = new SeatsFinder();

    /**
     * The number of seats in the venue that are neither held nor reserved
     *
     * @return the number of tickets available in the venue
     */
    public int numSeatsAvailable() {
        Long aLong;
        try {
            cleanUpExpiredSeatHold();
            aLong = seatsFinder.numSeatsAvailable();
            if (aLong != null) {
                return aLong.intValue();
            } else {
                logger.warn("could not retrieve available seats {}");
                return 0;
            }
        } catch (Exception e) {
            logger.warn("exception while retrieving available seats {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Find and hold the best available seats for a customer
     *
     * @param numSeats      the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related
     * information
     */
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        try {
            if (seatsFinder.numSeatsAvailable() >= numSeats) {
                SeatHoldDetails seatHoldDetails = new SeatHoldDetails(createUniqueSeatHoldId());
                boolean saved = saveSeatsForHoldId(seatHoldDetails, seatsFinder.findAndHoldSeats(numSeats));
                if (saved)
                    return new SeatHold(seatHoldDetails.getHoldId(), customerEmail,
                            seatsFinder.getSeatsNumbersById(seatHoldDetails.getHoldId()));
                else
                    return null;
            } else {
                logger.info("not enough seats are available {}", numSeats);
                return null; // Or throw exception or May be return an Optional.of SeatHold.
            }
        } catch (Exception e) {
            logger.error("exception while holding seats {}", e.getMessage());
            return null;
        }
    }

    /**
     * Commit seats held for a specific customer
     *
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the
     *                      seat hold is assigned
     * @return a reservation confirmation code
     */

    public String reserveSeats(int seatHoldId, String customerEmail) {
        try {
            SeatHoldDetails seatHold = seatsFinder.getHoldById(seatHoldId);
            if (seatHold != null && isValid(seatHold)) {
                boolean reserved = seatsFinder.reserve(seatsFinder.getSeatsNumbersById(seatHoldId));
                if (reserved) {
                    seatHold.setConfirmationNumber(UUID.randomUUID().toString());
                    return seatHold.getConfirmationNumber();
                } else {
                    logger.warn("could not reserve seats for holdId {}", seatHold.getHoldId());
                    return null; // Or We could throw exception with error response.
                }
            } else {
                seatsFinder.releaseHold(seatsFinder.getSeatsNumbersById(seatHoldId));
                return "HoldId expired/NotFound, Please start over...";
            }
        } catch (Exception e) {
            logger.error("exception while reserving seats {}", e.getMessage());
            return null;
        }
    }

    /**
     * Clears expired seatHold
     */
    public void cleanUpExpiredSeatHold() {
        try {
            seatsFinder.cleanUpExpiredSeatHold();
        } catch (Exception e) {
            logger.error("exception while seats clean up {}", e.getMessage());
        }
    }

    /**
     * Save seats by holdId
     *
     * @param seatHoldDetails
     * @param seatNumbers
     * @return
     */
    private boolean saveSeatsForHoldId(SeatHoldDetails seatHoldDetails, List<String> seatNumbers) {
        // Check if holdId is already exits in the map, it shouldn't
        try {
            return seatsFinder.saveSeatsForHoldId(seatHoldDetails, seatNumbers);
        } catch (Exception e) {
            logger.error("exception while saving seats hold {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns Unique seatHoldId
     *
     * @return
     */
    private Integer createUniqueSeatHoldId() {
        return ThreadLocalRandom.current().nextInt(1000, Integer.MAX_VALUE);
    }

    /**
     * Checks if holdId is valid
     * Returns true if hold is not expired otherwise false.
     * SeatHold is valid for 1min
     *
     * @param seatHold
     * @return
     */
    private boolean isValid(SeatHoldDetails seatHold) {
        return seatHold.getDateCreated().isAfter(LocalDateTime.now());
    }
}
