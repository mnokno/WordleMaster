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
            button.text = charValue.toString().uppercase()
            this.charValue = charValue
        }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////

    fun nextConstraint() {
        when (constraintType) {
            ConstraintType.notInWord -> {
                constraintType = ConstraintType.inWordWrongPlace
                button.setBackgroundColor(Color.rgb(201, 180, 88))
            }
            ConstraintType.inWordWrongPlace -> {
                constraintType = ConstraintType.inWordCorrectPlace
                button.setBackgroundColor(Color.rgb(106, 170, 100))
            }
            else -> {
                constraintType = ConstraintType.notInWord
                button.setBackgroundColor(Color.rgb(120, 124, 126))
            }
        }
    }
}