package ai;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class ConstraintGroup implements Serializable {

    ///////////////////////
    /// Class variables ///
    ///////////////////////
    private final ArrayList<Constraint>[] constraints = (ArrayList<Constraint>[]) new ArrayList[3];
    private final ArrayList<Constraint>[] addedConstraints = (ArrayList<Constraint>[]) new ArrayList[3];

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
    public void addConstraint(@NonNull Constraint constraint){
        // Clears addedConstraints
        for (int i = 0; i < addedConstraints.length; i++){
            addedConstraints[i].clear();
        }

        // Adds the constraint
        constraints[constraint.getConstraintType().ordinal()].add(constraint);
        addedConstraints[constraint.getConstraintType().ordinal()].add(constraint);
    }

    // Adds a list of constraints to the ConstraintGroup
    public void addConstraints(@NonNull Constraint[] toAdd){
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
    @NonNull
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
    @NonNull
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
    @NonNull
    public Constraint[] getAllConstraints(@NonNull ConstraintType constraintType){
        // Returns a list of constraints with the corresponding type
        Constraint[] allC = new Constraint[constraints[constraintType.ordinal()].size()];
        constraints[constraintType.ordinal()].toArray(allC);
        return allC;
    }

    // Gets all of a given type added constraints
    @NonNull
    public Constraint[] getAllPreviouslyAddedConstraints(@NonNull ConstraintType constraintType){
        // Returns a list of constraints with the corresponding type
        Constraint[] allC = new Constraint[addedConstraints[constraintType.ordinal()].size()];
        addedConstraints[constraintType.ordinal()].toArray(allC);
        return allC;
    }
}
