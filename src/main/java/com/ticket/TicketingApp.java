package com.ticket;

import com.ticket.model.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;

import java.util.Scanner;

/**
 * TicketingApp
 */
public class TicketingApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Hello there!\n");
        do {
            System.out.println("Would you like to reserve tickets?");
            while (true) {
                String answer = scanner.nextLine();
                if ("YES".equalsIgnoreCase(answer)) {
                    TicketService ticketService = new TicketServiceImpl();
                    System.out.println("Okay Great!, let's do this.\n");
                    System.out.println("Okay. Please wait while I check availability...\n");

                    sleepForSomeTime();

                    int numOFTickets = ticketService.numSeatsAvailable();

                    System.out.println("\nAvailable Seats : " + numOFTickets + "\n");

                    System.out.println("How many tickets?\n");
                    int noOfTickets = 0;
                    while (true) {
                        try {
                            String strOfTickets = scanner.nextLine();
                            noOfTickets = Integer.valueOf(strOfTickets);
                            break;
                        } catch (Exception ex) {
                            System.out.println("Invalid input. Please enter valid Number ?\n");
                        }
                    }
                    //scanner.nextLine();
                    System.out.println("Please provide your email id ?\n");
                    String emailAddress = scanner.nextLine();
                    System.out.println("Okay. Please wait while I try to hold tickets for you...\n");

                    sleepForSomeTime();

                    SeatHold seatHold = ticketService.findAndHoldSeats(noOfTickets, emailAddress);
                    System.out.println("\nGreat!, We found these Seats for you =>" + seatHold.getSeatNumbers() + "\n");
                    System.out.println("Would you like me to reserve these seats for you?");

                    while (true) {
                        String isReserve = scanner.nextLine();
                        if ("YES".equalsIgnoreCase(isReserve)) {
                            sleepForSomeTime();
                            String confirmationId = ticketService.reserveSeats(seatHold.getHoldId(), emailAddress);
                            if (confirmationId != null) {
                                System.out.println("\nYour seats are confirmed!!\n");
                                if (confirmationId.contains("expired")) {
                                    System.out.println("holdId expired");
                                } else {
                                    System.out.println("Confirmation Number : " + confirmationId);
                                }
                                break;
                            } else {
                                System.out.println("Sorry, we couldn't reserve these tickets for you.Please Try again");
                                break;
                            }
                        } else if ("NO".equalsIgnoreCase(isReserve)) {
                            System.out.println("Okay!\n");
                            break;
                        } else {
                            System.out.println("Invalid answer. Valid Options are [YES,NO]");
                        }
                    }
                    break;
                } else if ("NO".equalsIgnoreCase(answer)) {
                    System.out.print("Okay!");
                    break;
                } else {
                    System.out.println("Invalid answer. Valid Options are [YES,NO]");
                }
            }

            System.out.print("Do you wish to continue ?[Yes,Exit]\n");
            //String answer = scanner.nextLine();

        } while ("YES".equalsIgnoreCase(scanner.nextLine()));

    }

    private static void sleepForSomeTime() {
        try {
            for (double progressPercentage = 0.0; progressPercentage < 1.0; progressPercentage += 0.01) {
                updateProgress(progressPercentage);
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("=>" + e.getMessage());
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
