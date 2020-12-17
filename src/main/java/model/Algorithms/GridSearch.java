package model.Algorithms;

import model.Solutions.Solution;


public class GridSearch extends Algorithm {

    public GridSearch(String name) {
        super(name);
    }

    @Override
    public void runAlgorithm() {

    }

    @Override
    public Solution[][] getGenerations() {
        return new Solution[0][];
    }

    @Override
    public Solution[] getOptimalGeneration() {
        return new Solution[0];
    }
}
