package ai;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Constraint implements Serializable {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    private final char constraintChar;
    private final ConstraintType constraintType;
    private final int position;

    ////////////////////////////
    /// Class initialization ///
    ////////////////////////////
    public Constraint(char constraintChar, @NonNull ConstraintType constraintType, int position){
        assert (position >= 0 && position <= 4) : "position has to be between 0 and 4 not " + String.valueOf(position);
        this.constraintChar = constraintChar;
        this.constraintType = constraintType;
        this.position = position;
    }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////
    public char getConstraintChar(){
        return constraintChar;
    }

    public ConstraintType getConstraintType(){
        return constraintType;
    }

    public int getPosition(){
        return position;
    }
}
