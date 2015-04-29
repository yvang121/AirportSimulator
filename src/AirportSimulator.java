package edu.macalester.comp124.simulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is the main class for conducting the discrete event simulation
 * of an airport.
 *
 * Created by shoop on 4/13/15.
 */
public class AirportSimulator {
    private ArrayList<Runway> arrayOfRunways;
    private Queue<Double> randArrQ;
    private Queue<Double> randDepQ;
    private SystemParameters sysParams;
    private Integer lowestArrTime = 0;
    private Integer lowestDepTime = 0;
    private ArrayList<Airplane> finishedPlaneList;
    private double simulatorParameters[] = {
            60,     // Seconds between events
            2,      // # of Runways

            10,     // Mean arrival time between planes
            3,      // Standard deviation of arriving planes

            8,      // Mean service time for arriving planes
            2,      // Standard deviation service time for arriving planes

            10,     // Mean departure time between planes
            3,      // Standard deviation of departing planes

            8,      // Mean service time for departing planes
            2,      // Standard deviation service time for departing planes

            6,      // Random seed
            1440,    // Total run time in minutes
    };

    /**
     * Output parameters for use later in the main method.
     */
    private static double totalRunwayIdle = 0;
    private static int countRemainingIn = 0;
    private static int countRemainingOut = 0;
    private static int totalPlanes = 0;
    private static double totalServiceTime = 0;
    private static double avgTimeInQueue = 0;

    /**
     * Constructor for AirportSimulator which includes attributes like the array containing the number
     * of specified runways, simulator input parameters, randomly generated arrival/departure
     * times to be held in lists, and finally the array to keep track of which planes have finished
     * servicing.
     */
    public AirportSimulator() {
        arrayOfRunways = new ArrayList<Runway>();
        sysParams = new SystemParameters(simulatorParameters);
        randArrQ = new LinkedList<Double>();
        randDepQ = new LinkedList<Double>();
        finishedPlaneList = new ArrayList<Airplane>();
    }

    /**
     * Creates the random values for use in the simulator
     */
    public void initEvents() {
        // Randomly generate numbers to simulate planes arriving randomly
        RandomGenerator randomArrivalGenerator = new RandomGenerator(sysParams.getArrtime(),
                sysParams.getArrstddev());
        // Randomly generate numbers to simulate planes departing randomly
        RandomGenerator randomDepartureGenerator = new RandomGenerator(sysParams.getDeptime(),
                sysParams.getDepstddev());
        // Get next value and add them to random arrival/departure queues
        double randArrValue = (int) randomArrivalGenerator.getNextValue();
        double randDepValue = (int) randomDepartureGenerator.getNextValue();
        randArrQ.add(randArrValue);
        randDepQ.add(randDepValue);
    }

    /**
     * Creates the runway(s) depending on input parameter '# of Runways'
     */
    public void createArrayRunway() {
        for (int i = 0; i < sysParams.getRunways(); i++) {
            Runway runway = new Runway();
            arrayOfRunways.add(runway);
        }
    }

    /**
     * Simulation method. Uses a for-loop as a clock, checks if the clock is equal to when
     * a new arriving/departing plane is supposed to arrive or depart, then designates which
     * runway queue to put that new airplane in.
     */
    public void startSimulation() {
        double arrivingPlaneTime = 0; // Stores and adds up rand times to match with sim time
        double departingPlaneTime = 0; // Stores and adds up rand times to match with sim time
        Queue<Double> clockArrPlaneTime = new LinkedList<Double>(); // Sum arrival times grow faster than clock.
        Queue<Double> clockDepPlaneTime = new LinkedList<Double>(); // Similarly for depart times. Therefore,
        // stored in list for polling.
        for (double clock = 0; clock <= sysParams.getLength(); clock++) {
            // Generate a random arrival/departure time
            if (arrivingPlaneTime <= sysParams.getLength() || departingPlaneTime <= sysParams.getLength()) {
                initEvents();
                clockArrPlaneTime.offer(arrivingPlaneTime += randArrQ.poll());
                clockDepPlaneTime.offer(departingPlaneTime += randDepQ.poll());
            }
            // If clock time is equal to the time an arriving plane comes, generate arrival event
            if (clock == clockArrPlaneTime.peek()) {
                calcLowestQueueTime();
                ArrivalEvent newArrival = new ArrivalEvent(sysParams);
                for (Runway runway : arrayOfRunways) {
                // Designate which arrival queue to place it in based on lowest arrival queue total service time.
                    if (runway.getTotalArrQueueServTime() == lowestArrTime) {
                        newArrival.getPlane().setTimeIn(clockArrPlaneTime.peek());
                        runway.getArriveQueue().offer(newArrival.getPlane());
                        break;
                    }
                }
                clockArrPlaneTime.poll();
            }
            // If clock time is equal to the time a departing plane comes, generate departure event
            if (clock == clockDepPlaneTime.peek()) {
                calcLowestQueueTime();
                DepartureEvent newDeparture = new DepartureEvent(sysParams);
                for (Runway runway : arrayOfRunways) {
                // Designate which departure queue to place it in based on lowest departure queue total service time
                    if (runway.getTotalDepQueueServTime() == lowestDepTime) {
                        newDeparture.getPlane().setTimeIn(clockDepPlaneTime.peek());
                        runway.getDepartQueue().offer(newDeparture.getPlane());
                        break;
                    }
                }
                clockDepPlaneTime.poll();
            }
            // This section looks for a plane to put on runway. Decrements service time if there's a plane on the runway.
            for (Runway runway : arrayOfRunways) {
                Airplane runwayPlane = runway.getPlaneOnRunway();
                if (runway.getArriveQueue().peek() == null && runway.getDepartQueue().peek() == null && runwayPlane == null) {
                    totalRunwayIdle++;
                } else if (!runway.hasPlaneRunning() && runway.getArriveQueue().peek() != null
                        && runwayPlane == null) {
                    // check if runway has plane on it and check if arrive queue has plane in it
                    runway.setPlaneRunning(true);
                    runway.setPlaneOnRunway(runway.getArriveQueue().peek());
                } else if (!runway.hasPlaneRunning() && runway.getDepartQueue().peek() != null
                        && runwayPlane == null) {
                    // check if runway has plane on it and check if depart queue has plane on it
                    runway.setPlaneRunning(true);
                    runway.setPlaneOnRunway(runway.getDepartQueue().peek());
                } else if (runway.hasPlaneRunning() && runwayPlane != null) {
                    if (runwayPlane.getServiceTime() == 0) {
                    // If service time is equal to 0, set its timeOut to clock, remove, add to finished plane list
                        runwayPlane.setTimeOut(clock);
                        if (runway.getPlaneOnRunway() == runway.getArriveQueue().peek()) {
                            finishedPlaneList.add(runway.getArriveQueue().poll());
                        } else {
                            finishedPlaneList.add(runway.getDepartQueue().poll());
                        }
                        runway.setPlaneRunning(false);
                        runway.setPlaneOnRunway(null);
                    } else {
                        // Decrement service time of plane currently on the runway
                        runwayPlane.setServiceTime(runwayPlane.getServiceTime() - 1);
                    }
                }
            }
        }
    }

    /**
     * Calculates the total service times for each runway given the calculated arrival queue and departure queue
     * service times. Then designates which runway has the lowest arrival/departure times.
     */
    public void calcLowestQueueTime() {
        for (Runway runway : arrayOfRunways) {
            int arrServTime = calcArrTime(runway);
            int depServTime = calcDepTime(runway);
            runway.setTotalArrQueueServTime(arrServTime);
            runway.setTotalDepQueueServTime(depServTime);
            if (lowestArrTime == 0) {
                lowestArrTime = arrServTime;
            } else if (runway.getTotalArrQueueServTime() < lowestArrTime) {
                lowestArrTime = (int) runway.getTotalArrQueueServTime();
            }
            if (lowestDepTime == 0) {
                lowestDepTime = depServTime;
            } else if (runway.getTotalDepQueueServTime() < lowestDepTime) {
                lowestDepTime = (int) runway.getTotalDepQueueServTime();
            }
        }
    }

    /**
     * Calculates the total service time for a given runway arrival queue
     * @param runway that gets passed in to calculate its total service time
     * @return integer to represent total service time
     */
    public int calcArrTime(Runway runway) {
        int totalArrServTime = 0;
        for (Airplane plane : runway.getArriveQueue()) {
            totalArrServTime += plane.getServiceTime();
        }
        return totalArrServTime;
    }

    /**
     * Calculates total service time for given runway departure queue
     * @param runway gets passed in to calc service time for its departure queue
     * @return integer to represent total service time for departure queue
     */
    public int calcDepTime(Runway runway) {
        int totalDepServTime = 0;
        for (Airplane plane : runway.getDepartQueue()) {
            totalDepServTime += plane.getServiceTime();
        }
        return totalDepServTime;
    }

    /**
     * Calculates the amount of time a plane is in the queue until it's taken out
     * @return the average time a plane is in the queue.
     */
    public double calcTimeInQueue() {
        double totalDeltaTime = 0;
        for (Airplane planeDelta : finishedPlaneList) {
            totalDeltaTime += planeDelta.getTimeOut() - planeDelta.getTimeIn();
            totalPlanes++;
        }
        return avgTimeInQueue = Math.ceil(totalDeltaTime / totalPlanes);
    }

    /**
     * Calculates the total service time from the list of finished planes
     */
    public void calcTotalServTime() {
        for (Airplane planeCharlie : finishedPlaneList) {
            totalServiceTime += planeCharlie.getInitialServTime();
        }
    }

    /**
     * Counts the remaining planes in each arrival/departure queue
     */
    public void countRemaining() {
        for (Runway runway : arrayOfRunways) {
            if (runway.getArriveQueue().peek() != null) {
                runway.getArriveQueue().poll();
                countRemainingOut++;
            }
            if (runway.getDepartQueue().peek() != null) {
                runway.getDepartQueue().poll();
                countRemainingIn++;
            }
        }
    }


    public static void main(String[] args) {
        AirportSimulator airportSimulator = new AirportSimulator();
        airportSimulator.createArrayRunway(); // Create runways
        airportSimulator.startSimulation(); // Runway simulation
        airportSimulator.calcTimeInQueue(); // Calculate end output results
        airportSimulator.calcTotalServTime();
        airportSimulator.countRemaining();
        System.out.println("Simulation Complete. Statistics for this run: " + "\n"
                + "Remaining planes in arriving queues: " + countRemainingOut + "\n"
                + "Remaining planes in departing queues: " + countRemainingIn + "\n"
                + "Total planes serviced: " + totalPlanes + "\n"
                + "Average time plane is in a queue: " + avgTimeInQueue + " minutes "
                    + "(Approx. " + Math.round(avgTimeInQueue/60) + " hour(s))" + "\n"
                + "Total runway idle time: " + totalRunwayIdle + " minutes "
                    + "(Approx. " + Math.round(totalRunwayIdle/60.0) + " hour(s))" + "\n"
                + "Total service time: " + (totalServiceTime) + " minutes "
                    + "(Approx. " + Math.round(totalServiceTime/60.0) + " hour(s))"
        );
    }
}
