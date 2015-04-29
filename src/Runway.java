package edu.macalester.comp124.simulator;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Ye Vang on 4/16/2015.
 * The runway class that has two separate queues; one for arrival planes,
 * and the other for departing planes. It also has a plane currently
 * using a runway, a boolean to designate if there's a plane on the runway,
 * and total service times for both queues.
 */
public class Runway {
    private Queue<Airplane> departQueue;
    private Queue<Airplane> arriveQueue;
    private double totalArrQueueServTime;
    private double totalDepQueueServTime;
    private boolean hasPlane;
    private Airplane planeOnRunway;

    public Runway() {
        arriveQueue = new LinkedList<Airplane>();
        departQueue = new LinkedList<Airplane>();
        totalArrQueueServTime = 0;
        totalDepQueueServTime = 0;
        hasPlane = false;
        planeOnRunway = null;
    }

    public boolean hasPlaneRunning() {
        return hasPlane;
    }

    public boolean setPlaneRunning(boolean bool) {
        return hasPlane = bool;
    }

    public Queue<Airplane> getDepartQueue() {
        return departQueue;
    }

    public Queue<Airplane> getArriveQueue() {
        return arriveQueue;
    }

    public double getTotalArrQueueServTime() {
        return totalArrQueueServTime;
    }

    /**
     *
     * @param totalArrQueueServTime keeps track of the total arrival service time in order to designate
     *                              shortest arrival queue.
     */
    public void setTotalArrQueueServTime(double totalArrQueueServTime) {
        this.totalArrQueueServTime = totalArrQueueServTime;
    }

    public double getTotalDepQueueServTime() {
        return totalDepQueueServTime;
    }

    /**
     *
     * @param totalDepQueueServTime keeps track of the departure queue total service time in order to designate
     *                              shortest departure queue
     */
    public void setTotalDepQueueServTime(double totalDepQueueServTime) {
        this.totalDepQueueServTime = totalDepQueueServTime;
    }

    /**
     *
     * @return the plane currently using the runway (i.e. a plane in either the depart/arrive queue ready
     * for takeoff by countdown). Makes it easier to keep track of which plane from which queue is currently
     * using the runway.
     */
    public Airplane getPlaneOnRunway() {
        return planeOnRunway;
    }

    /**
     * Important for specifying which plane is currently 'using' the runway, therefore their service
     * time is currently being decremented. Necessary to keep track of the plane using the runway.
     * @param planeOnRunway passes a plane as a parameter and sets it to be the servicing plane.
     */
    public void setPlaneOnRunway(Airplane planeOnRunway) {
        this.planeOnRunway = planeOnRunway;
    }
}