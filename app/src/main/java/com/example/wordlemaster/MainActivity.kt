package com.example.wordlemaster

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginLeft
import androidx.core.view.setMargins
import androidx.core.view.size
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        genUI()

    }

    private fun genUI(){

        // Gets screen size
        val constraintLayout = findViewById<LinearLayout>(R.id.mainStackPanel)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val linearLayoutPaddingLength: Int = floor(displayMetrics.widthPixels * 0.075).toInt()
        val buttonSize: Int = floor(displayMetrics.widthPixels * 0.15).toInt()
        val buttonPaddingLength: Int = floor(displayMetrics.widthPixels * 0.01).toInt()

        // Generates horizontal layouts for button
        for (i in 0 until 6){


            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            linearLayout.setPadding(linearLayoutPaddingLength, buttonPaddingLength, linearLayoutPaddingLength, buttonPaddingLength)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            constraintLayout.addView(linearLayout);

            // Generates buttons
            for (j in 0 until  5){

                val button = Button(this)
                val layoutParams = LinearLayout.LayoutParams(buttonSize, buttonSize)
                layoutParams.setMargins(buttonPaddingLength, 0, buttonPaddingLength, 0)
                button.layoutParams = layoutParams
                button.text = "${constraintLayout.width}"
                button.setOnClickListener(View.OnClickListener {
                    button.text = button.text as String + '.'
                })
                button.setBackgroundColor(Color.GREEN)
                button.setTextColor(Color.RED)

                linearLayout.addView(button);
            }
        }
    }

    fun sendMessage(view: View) {
        // Do something in response to button
        (view as Button).text = "${findViewById<LinearLayout>(R.id.mainStackPanel).width}"
    }

}