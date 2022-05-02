package ai;

import androidx.annotation.NonNull;

public class Constraint {

    ///////////////////////
    /// Class variables ///
    ///////////////////////
    private char constraintChar;
    private ConstraintType constraintType;
    private int position;

    ////////////////////////////
    /// Class initialization ///
    ////////////////////////////
    public Constraint(@NonNull char constraintChar, @NonNull ConstraintType constraintType, @NonNull int position){
        this.constraintChar = constraintChar;
        this.constraintType = constraintType;
        this.position = position;
    }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////
    @NonNull
    public char getConstraintChar(){
        return constraintChar;
    }

    @NonNull
    public ConstraintType getConstraintType(){
        return constraintType;
    }

    @NonNull
    public int getPosition(){
        return position;
    }
}
