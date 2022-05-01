package com.example.wordlemaster

import android.graphics.Color
import android.widget.Button

class GameSquare(private val button: Button) {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    var constraintType = ConstraintType.notInWord
        private set

    private var charValue = 0.toChar()
    var char: Char
        get() = charValue
        set(charValue) {
            button.text = charValue.toString()
            this.charValue = charValue
        }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////

    fun nextConstraint() {
        when (constraintType) {
            ConstraintType.notInWord -> {
                constraintType = ConstraintType.inWordWrongPlace
                button.setBackgroundColor(Color.YELLOW)
            }
            ConstraintType.inWordWrongPlace -> {
                constraintType = ConstraintType.inWordCorrectPlace
                button.setBackgroundColor(Color.GREEN)
            }
            else -> {
                constraintType = ConstraintType.notInWord
                button.setBackgroundColor(Color.GRAY)
            }
        }
    }
}