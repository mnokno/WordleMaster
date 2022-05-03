package main

import ai.SearchType
import ai.WordleMaster
import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.TextView
import com.example.wordlemaster.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class WordSearchProgressPopUpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search_progress_pop_up)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics);

        val width: Int = displayMetrics.widthPixels;
        val height: Int = displayMetrics.heightPixels;

        //window.setLayout((width * 0.5).toInt(), (height * 0.5).toInt());
        val textView: TextView = findViewById(R.id.mainText);
        textView.height = (height * 0.1).toInt();


        GlobalScope.launch(Dispatchers.Main) { test() }

    }

    public suspend fun test(){

        val wordleMaster : WordleMaster = intent.getSerializableExtra("test") as WordleMaster
        wordleMaster.recomendWord(SearchType.slowExact, true)
        val textView: TextView = findViewById(R.id.mainText);
        textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)

        while (wordleMaster.taskPresentageCompletion != 1f){
            delay(100)
            //val data : String = wordleMaster.data
            textView.text = "Processing: " + String.format("%.3g%n", wordleMaster.taskPresentageCompletion * 100).trim() + "%"
        }

        finish()
    }
}