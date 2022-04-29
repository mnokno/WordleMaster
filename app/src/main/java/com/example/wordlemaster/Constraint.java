package com.example.wordlemaster;

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
    public Constraint(char constraintChar, ConstraintType constraintType, int position){
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
