package main

import ai.Constraint
import ai.SearchType
import ai.WordleMaster
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View

import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.example.wordlemaster.R
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.math.floor
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    private var wordleMaster: WordleMaster? = null
    private var rows: Array<Array<GameSquare?>?> = arrayOfNulls(6)
    private var currentRow: Int = -1;

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val word: String = data?.getStringExtra("result") ?: "error"
            // Displays recommended word
            for (i in 0 until rows[currentRow]?.size!!){
                rows[currentRow]?.get(i)!!.char = word[i]
            }
        }
    }

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

        val listA: Array<String> =  readInputStream(applicationContext.resources.openRawResource(R.raw.wordle_words)).split("\n").toTypedArray()
        val listB: Array<String> =  readInputStream(applicationContext.resources.openRawResource(R.raw.wordle_words_possible_entries)).split("\n").toTypedArray()

        wordleMaster = WordleMaster(readInputStream(applicationContext.resources.openRawResource(R.raw.wordle_words)).split("\n").toTypedArray())
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

        // Caps max button size to ensure that 7 rows will fit on screen
        if (((buttonSize + linearLayoutPaddingDown) * 7) > mainLinearLayout.height){
            val oldButtonSize = buttonSize
            buttonSize = floor((mainLinearLayout.height / 7.0) - 4).toInt()
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
                // Sets font
                button.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_bold), Typeface.BOLD)
                // Sets font size
                button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sqrt(buttonSize.toFloat() * 8))
                // Sets gravity
                button.gravity = Gravity.CENTER

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
        val suggestButtonLayoutParams = LinearLayout.LayoutParams(buttonSize*5 + buttonMargin*8, buttonSize)
        suggestButtonLayoutParams.setMargins(linearLayoutPaddingSideways + buttonMargin, 0, linearLayoutPaddingSideways + buttonMargin, 0)
        suggestButton.layoutParams = suggestButtonLayoutParams
        // Sets buttons text
        suggestButton.text = getString(R.string.recommend_button_text)
        // Sets on click event
        suggestButton.setOnClickListener {
            recommendMoveButton(suggestButton);
        }
        // Sets buttons appearance
        suggestButton.setBackgroundColor(Color.rgb(120, 124, 126))
        suggestButton.setTextColor(Color.WHITE)
        // Sets font
        suggestButton.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_bold), Typeface.BOLD)
        // Sets font size
        suggestButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sqrt(buttonSize.toFloat() * 8))
        // Sets gravity
        suggestButton.gravity = Gravity.CENTER
        // Adds this button to linearLayout
        mainLinearLayout.addView(suggestButton);

        //supportActionBar?.hide()
    }

    // Converts an input stream to a string (reads the stream)
    private fun readInputStream(inputStream: InputStream): String {
        return try {
            val result = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                result.write(buffer, 0, length)
            }
            result.toString(StandardCharsets.UTF_8.name())
        } catch (e: IOException) {
            println(e)
            ""
        }
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
                    wordleMaster!!.addConstraints(
                        Constraint(
                            gameSquare.char,
                            gameSquare.constraintType,
                            i
                        )
                    )
                }
            }

            // Updates current row
            currentRow++

            // Get recommended word
            val intent: Intent = Intent(applicationContext, WordSearchProgressPopUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("wordleMaster", wordleMaster)
            startActivity(intent)
            resultLauncher.launch(intent)

        }
    }
}

