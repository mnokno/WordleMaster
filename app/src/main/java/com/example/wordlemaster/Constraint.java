package com.example.wordlemaster;

public class Constraint {

    ///////////////////////
    /// Class variables ///
    ///////////////////////
    private char constraintChar;
    private ConstraintType constraintType;
    private byte position;

    ////////////////////////////
    /// Class initialization ///
    ////////////////////////////
    public Constraint(char constraintChar, ConstraintType constraintType, byte position){
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

    public byte getPosition(){
        return position;
    }
}
