package com.ticket.model;

import java.time.LocalDateTime;
import java.util.List;

import static com.ticket.model.TicketAppConstants.EXPIRATION_SEC;

public class SeatHold {
    private List<String> seatNumbers;
    private String customerEmailAddress;
    private int holdId;
    private LocalDateTime dateTime;

    public SeatHold(int holdId, String customerEmailAddress, List<String> seatNumbers) {
        this.holdId = holdId;
        this.customerEmailAddress = customerEmailAddress;
        this.seatNumbers = seatNumbers;
        this.dateTime = LocalDateTime.now().plusSeconds(EXPIRATION_SEC);
    }

    public int getHoldId() {
        return holdId;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    @Override
    public String toString() {
        return "SeatHold{" +
                "customerEmailAddress='" + customerEmailAddress + '\'' +
                ", seatNumbers=" + seatNumbers +
                ", holdId=" + holdId +
                ", dateTime=" + dateTime +
                '}';
    }
}
