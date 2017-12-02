package com.ticket;

import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * TicketingApp
 */
public class TicketingApp {

    public static final Logger logger = LoggerFactory.getLogger(TicketingApp.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        logger.info("Hello there!\n");
        do {
            logger.info("Would you like to reserve tickets?");
            while (true) {
                String answer = scanner.nextLine();
                if ("YES".equalsIgnoreCase(answer)) {
                    TicketService ticketService = new TicketServiceImpl();
                    logger.info("Okay Great!, let's do this.\n");
                    logger.info("Okay. Please wait while I check availability...\n");

                    sleepForSomeTime();

                    int numOFTickets = ticketService.numSeatsAvailable();

                    logger.info("Available Seats: {} \n", numOFTickets);

                    logger.info("How many tickets?\n");
                    int noOfTickets;
                    while (true) {
                        try {
                            String strOfTickets = scanner.nextLine();
                            noOfTickets = Integer.valueOf(strOfTickets);
                            break;
                        } catch (Exception ex) {
                            logger.info("Invalid input. Please enter valid Number ?\n");
                        }
                    }
                    logger.info("Please provide your email id ?\n");
                    String emailAddress = scanner.nextLine();
                    logger.info("Please wait while I try to hold tickets for you...\n");

                    sleepForSomeTime();

                    SeatHold seatHold = ticketService.findAndHoldSeats(noOfTickets, emailAddress);
                    logger.info("Great!, We found these Seats for you => {}\n", seatHold.getSeatNumbers());
                    logger.info("Would you like me to reserve these seats for you?");

                    while (true) {
                        String isReserve = scanner.nextLine();
                        if ("YES".equalsIgnoreCase(isReserve)) {
                            sleepForSomeTime();
                            String confirmationId = ticketService.reserveSeats(seatHold.getHoldId(), emailAddress);
                            if (confirmationId != null) {
                                logger.info("Your seats are confirmed!");
                                if (confirmationId.contains("expired")) {
                                    logger.info("holdId expired");
                                } else {
                                    logger.info("Confirmation Number : {}", confirmationId);
                                }
                                break;
                            } else {
                                logger.info("Sorry, we couldn't reserve these tickets for you.Please Try again");
                                break;
                            }
                        } else if ("NO".equalsIgnoreCase(isReserve)) {
                            logger.info("Okay!\n");
                            break;
                        } else {
                            logger.info("Invalid answer. Valid Options are [YES,NO]");
                        }
                    }
                    break;
                } else if ("NO".equalsIgnoreCase(answer)) {
                    logger.info("Okay!");
                    break;
                } else {
                    logger.info("Invalid answer. Valid Options are [YES,NO]");
                }
            }
            logger.info("Do you wish to continue ?[Yes,Exit]\n");

        } while ("YES".equalsIgnoreCase(scanner.nextLine()));

    }

    private static void sleepForSomeTime() {
        try {
            for (double progressPercentage = 0.0; progressPercentage < 1.0; progressPercentage += 0.01) {
                updateProgress(progressPercentage);
                Thread.sleep(10);
            }
            System.out.print("\n");
        } catch (InterruptedException e) {
            logger.info("=>" + e.getMessage());
        }
    }

    static void updateProgress(double progressPercentage) {
        final int width = 50; // progress bar width in chars

        System.out.print("\r[");
        int i = 0;
        for (; i <= (int) (progressPercentage * width); i++) {
            System.out.print("-");
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
    }
}
