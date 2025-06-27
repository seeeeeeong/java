package com.example.java.thread;

public class ThreadIdName {

    // --------------------------------------------------
    // Custom thread class extending java.lang.Thread
    // --------------------------------------------------
    static class MyThread extends Thread {

        @Override
        public void run() {
            // Print the thread's name and ID at the start
            System.out.println(getName() + " - begin");
            System.out.println("Thread ID: " + getId());

            // End of thread execution
            System.out.println(getName() + " - end");
        }
    }

    public static void main(String[] args) {
        // --------------------------------------------------
        // 1. Main thread setup and identification
        // --------------------------------------------------
        Thread mainThread = Thread.currentThread();        // Get the current thread (main)
        mainThread.setName("Main thread");                 // Set a custom name

        System.out.println(mainThread.getName() + " - begin");  // Print start message
        System.out.println("Thread ID: " + mainThread.getId()); // Print main thread ID

        // --------------------------------------------------
        // 2. Create and configure a new worker thread
        // --------------------------------------------------
        Thread thread = new MyThread();                    // Create instance of MyThread
        thread.setName("Worker thread");                   // Set custom name for worker thread

        // Start the worker thread (runs asynchronously)
        thread.start();

        // --------------------------------------------------
        // 3. Pause the main thread for 500ms
        //    - Gives worker thread a chance to run first
        // --------------------------------------------------
        try {
            Thread.sleep(500); // Main thread sleeps for 0.5 seconds
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --------------------------------------------------
        // 4. Main thread ends
        // --------------------------------------------------
        System.out.println(mainThread.getName() + " - end");
    }

}
