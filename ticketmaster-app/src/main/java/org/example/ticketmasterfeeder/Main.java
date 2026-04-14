package org.example.ticketmasterfeeder;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando...");
        ControllerTicketmaster controller = new ControllerTicketmaster();
        controller.start();
    }
}