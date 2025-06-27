package com.example.java.thread;

import java.util.Scanner;

public class UiAndThread {

    // --------------------------------------------------
    // Custom thread simulating file I/O operations
    // --------------------------------------------------
    static class MyThreadForIo extends Thread {

        @Override
        public void run() {
            System.out.println("* File I/O - start *, ID: " + getId());

            // Simulate I/O progress in 10% increments
            for (int i = 10; i <= 100; i += 10) {
                System.out.printf("TID: %d - %d%%\n", getId(), i);

                try {
                    Thread.sleep(1000);  // Wait 1 second per step
                } catch (Exception e) {
                }
            }

            // Indicate completion
            System.out.println("* File I/O - complete *");
        }
    }

    // --------------------------------------------------
    // Display the menu and get user input
    // --------------------------------------------------
    public static int printMenu() {
        System.out.println("[1] File\t[2] View\t[3] Edit\t[0] Exit");

        // Read user input from standard input
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();  // Returns the selected menu option
    }

    public static void main(String[] args) {
        int input = 0;

        // --------------------------------------------------
        // Main loop: display menu until user selects Exit (0)
        // --------------------------------------------------
        while ((input = printMenu()) != 0) {

            // --------------------------------------------------
            // If option 1 (File) is selected, start a new thread
            // --------------------------------------------------
            if (input == 1) {
                Thread thread = new MyThreadForIo();   // Create I/O simulation thread

                thread.setDaemon(true);                // Set as daemon so JVM exits cleanly
                thread.start();                        // Start the thread (runs run())
            }
        }

        // Program ends when user enters 0
    }
}
