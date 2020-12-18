package model.Algorithms;

import model.Solutions.Solution;
import model.Targets.TargetFunction;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class Algorithm {

    /** Target function to be optimised */
    TargetFunction target;

    private String name;

    /** Starting values. */
    double[] startingValues;

    /** Search space. */
    double[] searchSpace;

    /* The property change support object to use when notifying listeners of the model. */
    protected final PropertyChangeSupport notifier;

    public Algorithm(String name) {
        this.name = name;
        notifier = new PropertyChangeSupport(this);
    }

    /** Set the target function for the algorithm.
     * @param target A TargetFunction object to be optimised. */
    public void setTarget(TargetFunction target) {
        this.target = target;
    }

    /** Setter method for the starting values.
     * @param startingValues The starting values. */
    public void setStartingValues(double[] startingValues){
        this.startingValues = startingValues;
    }

    /** Setter method for the search space values.
     * @param searchSpace The search space values. */
    public void setSearchSpace (double[] searchSpace){
        this.searchSpace = searchSpace;
    }

    /**
     * Utility method to add an observer using this object's private (encapsulated) property change support object.
     * @param listener the listener to add
     */
    public void addObserver(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    /** Controls each step of the algorithm and controls the necessary steps. */
    public abstract void runAlgorithm();

    /** Get an array containing an array of Solutions for each generation of the algorithm.
     * @return 2D array of all Solution generations of the algorithm. */
    public abstract Solution[][] getGenerations();

    /** Get an array with the Solutions of the last generation.
     * @return Array with the Solutions of the last generation*/
    public abstract Solution[] getOptimalGeneration();


    public String getName() {
        return this.name;
    }
}


