package com.auth8.util;

import com.auth8.persistent.Device;

/**
 * An authentication request object. This object is created when an auth request
 * is posted by the developer. The object then locks the calling request and is
 * stored in a singleton. The client response then retrieves it and unlocks it
 * with the client auth response. Subsequently allowing the original request to
 * complete with the client request.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class AuthRequest {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private volatile boolean running = true;
    private boolean authenticated = false;
    private boolean timedout = false;
    private final int timeout;
    private Device device;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Default constructor with 120 second timeout
     */
    public AuthRequest() {
        timeout = 120;
    }

    /**
     * Constructor with timeout specified in seconds. If the timeout is 0 it 
     * will default to 120 seconds.
     *
     * @param timeout
     * @param device
     */
    public AuthRequest(int timeout, Device device) {
        if (timeout == 0) {
            this.timeout = 120;
        } else {
            this.timeout = timeout;
        }
        this.device = device;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Whether the thread is running or blocked
     *
     * @return true if the thread is running. false if its is blocked
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * The authentication response from the client.
     *
     * @return true if the authentication was successful. false if it failed.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * The device that should auth the request. Normally this would be an account rather than a device.
     *
     * @return The device.
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Whether the request timed out.
     *
     * @return The device.
     */
    public boolean isTimedout() {
        return timedout;
    }

    
//</editor-fold>

    /**
     * Pause the thread and start the countdown
     *
     * @throws InterruptedException
     */
    public void pauseThread() throws InterruptedException {
        running = false;
        countdown();
    }

    /**
     * Resumes the thread with the specified response
     *
     * @param authenticated
     * @param timedout
     */
    public void resumeThread(boolean authenticated, boolean timedout) {
        running = true;
        this.authenticated = authenticated;
        this.timedout = timedout;
        // TODO kill the countdown thread
    }

    /**
     * Implements a timeout countdown to prevent locked threads running for
     * ever.
     */
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
                resumeThread(false, true);
            }
        };
        new Thread(countdownThread, "COUNTDOWN THREAD").start();
    }
}
