package com.example.wordlemaster;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WordleMaster {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    private Context context;
    private Map<String, String> allWordsDict = new HashMap<String, String>();

    /////////////////////////
    /// Class constructor ///
    /////////////////////////
    public WordleMaster(Context context){
        this.context = context;
        popAllWordsDict();
    }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////

    // Populates allWordsDict
    private void popAllWordsDict(){

        // Reads text files
        InputStream inputStreamWords = context.getResources().openRawResource(R.raw.wordle_words);
        InputStream inputStreamFlattened = context.getResources().openRawResource(R.raw.wordle_words_flattened);

        // Reads and converts into an array of strings
        String wordsN = readInputStream(inputStreamWords);
        String wordsF = readInputStream(inputStreamFlattened);


    }

    private String readInputStream(InputStream inputStream) {
        try{
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        }
        catch (IOException e){
            System.out.println(e);
            return "";
        }
    }

    // Test
    public String Test(){
        return "TEW";
    }

}
