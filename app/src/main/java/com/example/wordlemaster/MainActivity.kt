package com.example.wordlemaster

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.*
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    private var wordleMaster: WordleMaster? = null
    private var rows: Array<Array<GameSquare?>?> = arrayOfNulls(6)
    private var currentRow: Int = -1;

    ////////////////////////////
    /// Class initialization ///
    ////////////////////////////

    // Called when mainActivity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for(i in 0 until 6){
            rows[i] = arrayOfNulls<GameSquare>(5);
        }

        wordleMaster = WordleMaster(applicationContext)
        GlobalScope.launch(Dispatchers.Main) { genUI() }

    }

    // Generates UI
    private suspend fun genUI(){

        // Gets screen size
        val mainLinearLayout = findViewById<LinearLayout>(R.id.mainStackPanel)

        // Waits for the mainLinearLayout to be laid out so that is has dimensions
        while (!mainLinearLayout.isLaidOut) { delay(10L) }

        // Calculates margins and square size
        val linearLayoutPaddingDown: Int = floor(mainLinearLayout.width * 0.02).toInt()
        var linearLayoutPaddingSideways: Int = 0
        var buttonSize: Int = floor(mainLinearLayout.width * 0.18).toInt()
        val buttonMargin: Int = floor(mainLinearLayout.width * 0.01).toInt()

        // Camps max button size to ensure that 7 rows will fit on screen
        if ((buttonSize * 7) > mainLinearLayout.height){
            val oldButtonSize = buttonSize
            buttonSize = floor((mainLinearLayout.height / 7.0) - 2).toInt()
            linearLayoutPaddingSideways = floor((oldButtonSize - buttonSize) * 2.5).toInt()
        }

        // Generates horizontal layouts for button
        for (i in 0 until 6){

            // Creates horizontal layouts for button
            val linearLayout = LinearLayout(this)

            // Sets layout parameters
            val linearLayoutParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            linearLayout.setPadding(linearLayoutPaddingSideways, 0, linearLayoutPaddingSideways, linearLayoutPaddingDown)
            linearLayout.layoutParams = linearLayoutParams

            // Sets orientation
            linearLayout.orientation = LinearLayout.HORIZONTAL
            // Adds it to the main stack panel
            mainLinearLayout.addView(linearLayout);

            // Generates buttons
            for (j in 0 until  5){

                // Creates new button
                val button = Button(this)

                // Sets layout parameters
                val layoutParams = LinearLayout.LayoutParams(buttonSize, buttonSize)
                layoutParams.setMargins(buttonMargin, 0, buttonMargin, 0)
                button.layoutParams = layoutParams

                // Sets on click event
                button.setOnClickListener(View.OnClickListener {
                    if (currentRow == i){
                        rows[i]?.get(j)?.nextConstraint()
                    }
                })

                // Sets buttons appearance
                button.setBackgroundColor(Color.rgb(120, 124, 126))
                button.setTextColor(Color.WHITE)

                // Adds this button to reference array
                rows[i]?.set(j, GameSquare(button));
                rows[i]?.get(j)?.char = ' ';

                // Adds this button to linearLayout
                linearLayout.addView(button);

            }
        }

        // Generates "suggest guess button"
        val suggestButton = Button(this)
        // Sets layout parameters
        val suggestButtonLayoutParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, buttonSize)
        suggestButtonLayoutParams.setMargins(linearLayoutPaddingSideways + buttonMargin, 0, linearLayoutPaddingSideways + buttonMargin, 0)
        suggestButton.layoutParams = suggestButtonLayoutParams
        // Sets buttons text
        suggestButton.text = "Recommend guess"
        // Sets on click event
        suggestButton.setOnClickListener(View.OnClickListener {
            recommendMoveButton(suggestButton);
        })
        // Sets buttons appearance
        suggestButton.setBackgroundColor(Color.rgb(120, 124, 126))
        suggestButton.setTextColor(Color.WHITE)
        // Adds this button to linearLayout
        mainLinearLayout.addView(suggestButton);
    }

    //////////////////////
    /// Event handling ///
    //////////////////////

    // Recommends a move
    private fun recommendMoveButton(view: View?){
        // Ensures that a user is not out of rows
        if (currentRow < 5){

            // Reads constraints if there are any to read
            if (currentRow != -1){
                for (i in 0 until rows[currentRow]?.size!!){
                    val gameSquare: GameSquare = rows[currentRow]?.get(i)!!;
                    wordleMaster!!.addConstraints(Constraint(gameSquare.char, gameSquare.constraintType, i))
                }
            }

            // Updates current row
            currentRow++
            // Get recommended word
            val word: String = wordleMaster!!.recomendWord()
            // Displays recommended word
            for (i in 0 until rows[currentRow]?.size!!){
                rows[currentRow]?.get(i)!!.char = word[i];
            }
        }
    }
}

