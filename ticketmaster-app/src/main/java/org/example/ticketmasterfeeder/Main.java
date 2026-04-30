package org.example.ticketmasterfeeder;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando...");
        org.example.ticketmasterfeeder.TicketmasterFeederService controller = new org.example.ticketmasterfeeder.TicketmasterFeederService();
        controller.start();
    }
}