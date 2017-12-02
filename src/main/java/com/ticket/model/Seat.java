package com.ticket.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.ticket.model.TicketAppConstants.EXPIRATION_SEC;

public class Seat {
    private String id;
    private boolean isReserved = false;
    private boolean isOnHold = false;
    private LocalDateTime dateCreated;

    private Seat(String id, boolean isReserved, boolean isOnHold) {
        this.id = id;
        this.isOnHold = isOnHold;
        this.isReserved = isReserved;
        this.dateCreated = LocalDateTime.now().plusSeconds(EXPIRATION_SEC);
    }

    public String getId() {
        return id;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setIsReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    public boolean isOnHold() {
        return (isOnHold && this.dateCreated.isAfter(LocalDateTime.now()));
    }

    public void setIsOnHold(boolean isOnHold) {
        this.isOnHold = isOnHold;
    }

    public static class SeatBuilder {
        private boolean isReserved = false;
        private boolean isOnHold = false;
        private int fromSeatNo;
        private int noOfSeats;

        public SeatBuilder setIsOnHold(boolean isOnHold) {
            this.isOnHold = isOnHold;
            return this;
        }

        public SeatBuilder setIsReserved(boolean isReserved) {
            this.isReserved = isReserved;
            return this;
        }

        public SeatBuilder setFromSeatNo(int fromSeatNo) {
            this.fromSeatNo = fromSeatNo;
            return this;
        }

        public SeatBuilder setNoOfSeats(int noOfSeats) {
            this.noOfSeats = noOfSeats;
            return this;
        }

        public Set<Seat> buildSeats() {
            Set<Seat> seats = new HashSet<>();
            while (this.noOfSeats > 0) {
                fromSeatNo++;
                seats.add(new Seat(String.valueOf(this.fromSeatNo), this.isReserved, this.isOnHold));
                noOfSeats--;
            }
            return seats;
        }
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", isReserved=" + isReserved +
                ", isOnHold=" + isOnHold +
                '}';
    }
}
