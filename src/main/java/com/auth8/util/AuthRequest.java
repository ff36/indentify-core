package com.auth8.util;

/**
 * An authentication request object.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class AuthRequest {

    private volatile boolean running = true;
    private boolean authenticated = false;
    private final int timeout;

    public AuthRequest() {
        timeout = 120;
    }

    public AuthRequest(int timeout) {
        this.timeout = timeout;
    }

    public void pauseThread() throws InterruptedException {
        running = false;
        countdown();
    }

    public void resumeThread() {
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    private void countdown() {

        Runnable countdownThread = new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < timeout; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        // TODO
                    }
                } 
                resumeThread();
            }
        };
        new Thread(countdownThread, "COUNTDOWN THREAD").start();
    }
}
