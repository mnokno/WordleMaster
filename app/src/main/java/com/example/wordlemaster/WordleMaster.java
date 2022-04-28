package com.example.wordlemaster;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WordleMaster {

    // Class variables
    private Map<String, String> allWordsDict = new HashMap<String, String>();

    // Class constructor
    public WordleMaster(){

    }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////

    // Populates allWordsDict
    private void popAllWordsDict(){

        // Reads text files
        InputStream inputStreamWords = getResources().openRawResource(R.raw.wordle_words);

    }

}
