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
        InputStream inputStreamWordsA = context.getResources().openRawResource(R.raw.wordle_words);
        InputStream inputStreamWordsB = context.getResources().openRawResource(R.raw.wordle_words);
        InputStream inputStreamFlattened = context.getResources().openRawResource(R.raw.wordle_words_flattened);

        // Reads and converts into an array of strings
        allWords = readInputStream(inputStreamWordsA).split("\n");
        possibleWords = readInputStream(inputStreamWordsB).split("\n");
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
    private CharMap countLetters(String[] words){
        // Creates an empty hash map
        CharMap charMap = new CharMap();

        // Calculates the char map
        for (int i = 0; i < words.length; i++){
            String usedCharacters = "";
            for (int j = 0; j < 5; j++){
                if (usedCharacters.indexOf(words[i].charAt(j)) == -1){
                    charMap.update(words[i].charAt(j));
                    usedCharacters += words[i].charAt(j);
                }
            }
        }
        // Returns the hash map
        return  charMap;
    }

    // Retruns recomended word to guess
    public String RecomendWord(){

        // Check weather the correct word has been found
        if (possibleWords.length == 1){
            return possibleWords[0];
        }
        else{
            // Calculates charmap
            CharMap charMap = countLetters(possibleWords);

            // Creats empty variables
            int bestScore = -1;
            String bestWord = null;

            // Find best word
            for (int i = 0; i < possibleWords.length; i++){
                int currentScore = ScoreWord(possibleWords[i], charMap);
                if (currentScore > bestScore){
                    bestScore = currentScore;
                    bestWord = possibleWords[i];
                }
            }

            // Retrun best word
            return bestWord;
        }
    }

    // Retruns score for a word based on the suplied CharMap
    private int ScoreWord(String word, CharMap charMap){
        // Creat initial score
        int score = 0;

        // Scores the word
        String usedCharacters = "";
        for (int i = 0; i < word.length(); i++){
            if (usedCharacters.indexOf(word.charAt(i)) == -1){
                score += charMap.get(word.charAt(i));
                usedCharacters += word.charAt(i);
            }
        }

        // Return the score
        return score;
    }

    // Test
    public String Test(){
        return "TEW";
    }

}
