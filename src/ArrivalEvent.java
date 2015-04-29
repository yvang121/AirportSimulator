package edu.macalester.comp124.simulator;

/**
 * Created by Ye Vang on 4/16/2015.
 * The Arrival Event denotes when a new arriving plane is generated. To simulate airplanes coming
 * in at random times, we generate an arrival event, which makes the plane and sets its randomly
 * generated service time to that plane. This arrival event class simply encapsulates those two
 * processes into one object.
 */
public class ArrivalEvent {
    private Airplane plane;
    private RandomGenerator randGen;

    public ArrivalEvent(SystemParameters param) {
        randGen = new RandomGenerator(param.getArrservetime(), param.getArrservestddev());
        double randServTime = Math.floor(randGen.getNextValue());
        plane = new Airplane();
        plane.setServiceTime(randServTime);
        plane.setInitialServTime(randServTime);
    }

    /**
     * Extremely useful for placing the plane into a queue.
     * @return the plane generated in an arrival event to be placed in an arrival queue.
     */
    public Airplane getPlane() {
        return plane;
    }

    public void setPlane(Airplane plane) {
        this.plane = plane;
    }
}