package com.example.wordlemaster;

public class Constraint {

    ///////////////////////
    /// Class variables ///
    ///////////////////////
    private char constraintChar;
    private ConstraintType constraintType;

    ////////////////////////////
    /// Class initialization ///
    ////////////////////////////
    public Constraint(char constraintChar, ConstraintType constraintType){
        this.constraintChar = constraintChar;
        this.constraintType = constraintType;
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

}
