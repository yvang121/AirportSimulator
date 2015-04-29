package edu.macalester.comp124.simulator;

/**
 * The Airplane object class. Used to be stored in a runway queue, either arriving or departing.
 * Created by Ye Vang on 4/16/2015.
 */
public class Airplane {
    private double initialServTime; // Doesn't change. Used for total service time later.
    private double serviceTime; // Used as a countdown for plane takeoff.
    private double timeIn; // Time plane put into queue
    private double timeOut; // Time plane leaves queue and finishes service time

    /**
     * Each airplane has an initial service time, which is used to calculate total service time
     * of each serviced plane, the service time which acts like a countdown, the time on the clock
     * an airplane was put in, and the time on the clock it was removed from queue (take the difference
     * of that to calculate the total time spent in a queue).
     */
    public Airplane() {
        initialServTime = 0;
        serviceTime = 0;
        timeIn = 0;
        timeOut = 0;
    }

    public double setServiceTime(double time) {
        return serviceTime = time;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public double getTimeOut() {
        return timeOut;
    }

    /**
     * Sets the time a plane was removed from a queue
     * @param timeOut clock time a plane was removed
     */
    public void setTimeOut(double timeOut) {
        this.timeOut = timeOut;
    }

    public double getTimeIn() {
        return timeIn;
    }

    /**
     * Sets the time a plane was inserted into a queue
     * @param timeIn clock time a plane was inserted
     */
    public void setTimeIn(double timeIn) {
        this.timeIn = timeIn;
    }

    /**
     * Used for calculations later i.e. total service time, since service time was being decremented,
     * a static unchanging service time was needed for calculations.
     * @return the service time it was initially set to
     */
    public double getInitialServTime() {
        return initialServTime;
    }

    /**
     * Important for calculating total serviced plane time.
     * @param initialServTime randomly generated service time is assigned to this, but not decremented like service time.
     */
    public void setInitialServTime(double initialServTime) {
        this.initialServTime = initialServTime;
    }
}
