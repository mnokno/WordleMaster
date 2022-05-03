package main

import ai.SearchType
import ai.WordleMaster
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.TextView
import com.example.wordlemaster.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class WordSearchProgressPopUpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search_progress_pop_up)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics);
        //window.setBackgroundDrawable(ColorDrawable(0))

        val width: Int = displayMetrics.widthPixels;
        val height: Int = displayMetrics.heightPixels;

        window.setLayout((width * 0.6).toInt(), (height * 0.6).toInt());
    }
}