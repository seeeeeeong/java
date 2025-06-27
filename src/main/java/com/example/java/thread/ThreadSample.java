package com.example.java.thread;

public class ThreadSample {

    public static void main(String[] args) {

        // --------------------------------------------------
        // 1. Main thread starts
        // --------------------------------------------------
        System.out.println("main() - begin");

        // --------------------------------------------------
        // 2. Create a new thread
        //    - Using an anonymous class implementing Runnable
        //    - run() will be executed in a separate thread
        // --------------------------------------------------
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // ------------------------------------------
                // This code runs inside the new thread
                // ------------------------------------------
                System.out.println("MyThread.run() - begin");

                // Simulated work (here: just printing)
                System.out.println("MyThread.run() - end");
            }
        });

        // --------------------------------------------------
        // 3. Start the new thread
        //    - The run() method will be called asynchronously
        // --------------------------------------------------
        thread.start();

        // --------------------------------------------------
        // 4. Pause the main thread for 500 milliseconds
        //    - Allows the worker thread to finish first
        // --------------------------------------------------
        try {
            Thread.sleep(500);  // Delay for 0.5 seconds
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --------------------------------------------------
        // 5. Main thread resumes and ends
        // --------------------------------------------------
        System.out.println("main() - end");
    }

}
