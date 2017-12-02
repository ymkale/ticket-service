package com.ticket.model;

import java.time.LocalDateTime;

public class SeatHoldDetails {
    private Integer holdId;
    private LocalDateTime dateCreated;
    private String confirmationNumber;

    public SeatHoldDetails(Integer holdId) {
        this.holdId = holdId;
        this.dateCreated = LocalDateTime.now().plusSeconds(30);
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getHoldId() {
        return holdId;
    }

    public void setHoldId(Integer holdId) {
        this.holdId = holdId;
    }
}
