package com.example.wordlemaster;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class WordleMaster {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    private Context context;
    private String[] allWords;
    private String[] possibleWords;

    /////////////////////////
    /// Class constructor ///
    /////////////////////////
    public WordleMaster(Context context){
        this.context = context;
        popAllWordsArray();
    }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////

    // Populates allWords array
    private void popAllWordsArray(){

        // Reads text files
        InputStream inputStreamWords = context.getResources().openRawResource(R.raw.wordle_words);
        InputStream inputStreamFlattened = context.getResources().openRawResource(R.raw.wordle_words_flattened);

        // Reads and converts into an array of strings
        allWords = readInputStream(inputStreamWords).split("\n");
        possibleWords = readInputStream(inputStreamWords).split("\n");
        //String[] wordsF = readInputStream(inputStreamFlattened).split("\n");

    }

    // Converts an input stream to a string (reads the stream)
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

    // Counts accurance of each letter in suplided list of words, words most be flattend, ducilated letters in a word are't coutned
    private Map<Character, Integer> CountLetters(String[] words){
        // Creates an empty hash map
        Map<Character, Integer> letterMap = new HashMap<Character, Integer>();

        // Calculates the char map
        for (int i = 0; i < words.length; i++){
            String usedLetters = "";
            for (int j = 0; j < 5; j++){
                if (!usedLetters.contains(words[i])){

                }
            }
        }

        // Returns the hash map
        return  letterMap;
    }

    // Test
    public String Test(){
        return "TEW";
    }

}
