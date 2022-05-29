package main

import ai.SearchType
import ai.WordleMaster
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.TextView
import com.example.wordlemaster.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WordSearchProgressPopUpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search_progress_pop_up)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics);
        val width: Int = displayMetrics.widthPixels;
        val height: Int = displayMetrics.heightPixels;

        val textView: TextView = findViewById(R.id.mainText);
        textView.height = (height * 0.1).toInt();
        textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)


        GlobalScope.launch(Dispatchers.Main) { asyncWordProcessing() }

    }

    private suspend fun asyncWordProcessing(){

        // val wordleMaster : WordleMaster = intent.getSerializableExtra("wordleMaster") as WordleMaster
        // wordleMaster.recomendWord(SearchType.slowExact, true)
        val textView: TextView = findViewById(R.id.mainText);

        if (WordleMaster.getTaskPresentageCompletion() == 1f){

            //val data : String = wordleMaster.data
            textView.text = "Processing: 100%"
            val returnIntent = Intent()
            returnIntent.putExtra("result", WordleMaster.getResult())
            setResult(RESULT_OK, returnIntent)
            delay(100)
            finish()
        }


        while (WordleMaster.getTaskPresentageCompletion() != 1f){
            delay(100)
            //val data : String = wordleMaster.data
            textView.text = "Processing: " + String.format("%.3g%n", WordleMaster.getTaskPresentageCompletion() * 100).trim() + "%"
        }

        val returnIntent = Intent()
        returnIntent.putExtra("result", WordleMaster.getResult())
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}