package edu.macalester.comp124.simulator;

/**
 * Created by Ye Vang on 4/16/2015.
 * The Departure Event denotes when a new departing plane is generated. To simulate airplanes leaving
 * at random times, we generate a new departing event, which makes the plane and sets its randomly
 * generated service time to that plane. This departure event class simply encapsulates those two
 * processes into one object.
 */
public class DepartureEvent {
    private Airplane plane;
    private RandomGenerator randGen;

    public DepartureEvent(SystemParameters param) {
        randGen = new RandomGenerator(param.getDepservetime(), param.getDepservestddev());
        double randServTime = Math.floor(randGen.getNextValue());
        plane = new Airplane();
        plane.setServiceTime(randServTime);
        plane.setInitialServTime(randServTime);
    }

    /**
     * Extremely useful for placing plane into a queue.
     * @return plane generated in a departure event to be placed into a departure queue.
     */
    public Airplane getPlane() {
        return plane;
    }

    public void setPlane(Airplane plane) {
        this.plane = plane;
    }
}