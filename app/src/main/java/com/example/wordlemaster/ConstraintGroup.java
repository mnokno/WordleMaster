package com.example.wordlemaster;

import java.util.ArrayList;
import java.util.List;

public class ConstraintGroup {

    ///////////////////////
    /// Class variables ///
    ///////////////////////
    private ArrayList<Constraint>[] constraints = (ArrayList<Constraint>[]) new ArrayList[3];
    private ArrayList<Constraint>[] addedConstraints = (ArrayList<Constraint>[]) new ArrayList[3];

    ////////////////////////////
    /// Class initialization ///
    ////////////////////////////

    public ConstraintGroup(){
        // Initiates lists
        for (int i = 0; i < constraints.length; i++){
            constraints[i] = new ArrayList<Constraint>();
            addedConstraints[i] = new ArrayList<Constraint>();
        }
    }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////

    // Adds a single constraint to the ConstraintGroup
    public void addConstraint(Constraint constraint){
        // Clears addedConstraints
        for (int i = 0; i < addedConstraints.length; i++){
            addedConstraints[i].clear();
        }

        // Adds the constraint
        constraints[constraint.getConstraintType().ordinal()].add(constraint);
        addedConstraints[constraint.getConstraintType().ordinal()].add(constraint);
    }

    // Adds a list of constraints to the ConstraintGroup
    public void addConstraints(Constraint[] toAdd){
        // Clears addedConstraints
        for (int i = 0; i < addedConstraints.length; i++){
            addedConstraints[i].clear();
        }

        // Adds constraints
        for (Constraint constraint : toAdd) {
            constraints[constraint.getConstraintType().ordinal()].add(constraint);
            addedConstraints[constraint.getConstraintType().ordinal()].add(constraint);
        }
    }

    // Clears the ConstraintGroup
    public void clear(){
        // Clears addedConstraints
        for (int i = 0; i < constraints.length; i++){
            constraints[i].clear();
            addedConstraints[i].clear();
        }
    }

    // Gets all constraints
    public Constraint[] getAllConstraints(){
        // Joins the constrains together
        Constraint[] allC = new Constraint[constraints[0].size() + constraints[1].size() + constraints[2].size()];
        int count = 0;
        for (ArrayList<Constraint> cList: constraints) {
            for (Constraint c: cList) {
                allC[count] = c;
                count++;
            }
        }

        // Returns constrains
        return allC;
    }

    // Gets all added constraints
    public Constraint[] getAllPreviouslyAddedConstraints(){
        // Joins the constrains together
        Constraint[] allC = new Constraint[addedConstraints[0].size() + addedConstraints[1].size() + addedConstraints[2].size()];
        int count = 0;
        for (ArrayList<Constraint> cList: addedConstraints) {
            for (Constraint c: cList) {
                allC[count] = c;
                count++;
            }
        }

        // Returns constrains
        return allC;
    }

    // Gets all of a given type constraints
    public Constraint[] getAllConstraints(ConstraintType constraintType){
        // Returns a list of constraints with the corresponding type
        Constraint[] allC = new Constraint[constraints[constraintType.ordinal()].size()];
        constraints[constraintType.ordinal()].toArray(allC);
        return allC;
    }

    // Gets all of a given type added constraints
    public Constraint[] getAllPreviouslyAddedConstraints(ConstraintType constraintType){
        // Returns a list of constraints with the corresponding type
        Constraint[] allC = new Constraint[addedConstraints[constraintType.ordinal()].size()];
        addedConstraints[constraintType.ordinal()].toArray(allC);
        return allC;
    }
}
