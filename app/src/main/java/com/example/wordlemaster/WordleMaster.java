package com.example.wordlemaster;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private ConstraintGroup constraintGroup = new ConstraintGroup();

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
    public String recomendWord(){

        // Recalculates possible moves
        recalculatePossibleWords();

        // Check weather the correct word has been found
        if (possibleWords.length <= 2){
            return possibleWords[0];
        }
        else{
            // Calculates charmap
            CharMap charMap = countLetters(possibleWords);
            // Clamps the charm map
            charMap.clampSet(possibleWords.length);

            // Creats empty variables
            int bestScore = -1;
            String bestWord = null;

            // Find best word
            for (int i = 0; i < allWords.length; i++){
                int currentScore = scoreWord(allWords[i], charMap);
                if (currentScore > bestScore){
                    bestScore = currentScore;
                    bestWord = allWords[i];
                }
            }

            // Prints list of possible words
            //TODO
            System.out.println("............................");
            for (String word: possibleWords) {
                System.out.println(word);
            }
            System.out.println("Total words: " + possibleWords.length);
            System.out.println("............................");
            // Retrun best word
            return bestWord;
        }
    }

    // Retruns score for a word based on the suplied CharMap
    private int scoreWord(String word, CharMap charMap){

        // Creat initial score
        int score = 0;

        // Gets constraints
        Constraint[] inWordInCorrrectPlace = constraintGroup.getAllConstraints(ConstraintType.inWordCorrectPlace);
        Constraint[] inWordWrongPlace = constraintGroup.getAllConstraints(ConstraintType.inWordWrongPlace);
        Constraint[] notInWord = constraintGroup.getAllConstraints(ConstraintType.notInWord);

        // Scores the word
        String usedCharacters = "";
        for (int i = 0; i < word.length(); i++){
            if (usedCharacters.indexOf(word.charAt(i)) == -1){

                boolean isVlaid = true;
                for (Constraint constraint: inWordInCorrrectPlace) {
                    if (constraint.getConstraintChar() == word.charAt(i)){
                        isVlaid = false;
                        break;
                    }
                }
                if (isVlaid){
                    for (Constraint constraint: inWordWrongPlace) {
                        if (constraint.getConstraintChar() == word.charAt(i)){
                            if (constraint.getPosition() == i){
                                isVlaid = false;
                                break;
                            }
                            for (Constraint c: inWordInCorrrectPlace) {
                                if (c.getPosition() == i){
                                    isVlaid = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isVlaid){
                    for (Constraint constraint: notInWord) {
                        if (constraint.getConstraintChar() == word.charAt(i)){
                            isVlaid = false;
                            break;
                        }
                    }
                }


                if (isVlaid){
                    score += charMap.get(word.charAt(i));
                    usedCharacters += word.charAt(i);
                }
            }
        }

        // Return the score
        return score;
    }

    // Adds constraints
    public void addConstraints(Constraint[] constraints){
        constraintGroup.addConstraints(constraints);
    }

    // Adds constraint
    public void addConstraints(Constraint constraint){
        addConstraints(new Constraint[]{constraint});
    }

    // Sets constraints
    public void setConstraints(Constraint[] constraints){
        constraintGroup.clear();
        constraintGroup.addConstraints(constraints);
    }

    // Recalculates possible words based on the constraints
    private void recalculatePossibleWords(){

        // Creates a list for stroing valid words
        ArrayList<String> validWords = new ArrayList<String>();

        // Gets constraints
        Constraint[] inWordInCorrrectPlace = constraintGroup.getAllConstraints(ConstraintType.inWordCorrectPlace);
        Constraint[] inWordWrongPlace = constraintGroup.getAllConstraints(ConstraintType.inWordWrongPlace);
        Constraint[] notInWord = constraintGroup.getAllConstraints(ConstraintType.notInWord);

        // Adds valid words to the validWords list
        for (String word: possibleWords) {
            if (isValid(word, inWordInCorrrectPlace, inWordWrongPlace, notInWord)){
                validWords.add(word);
            }
        }

        // Sets the valid words list as possible words
        String[] newPossibleWords = new String[validWords.size()];
        validWords.toArray(newPossibleWords);
        possibleWords = newPossibleWords;

    }

    // Return true if given word is valid withiin given constraints
    private boolean isValid(String word, Constraint[] inWordInCorrrectPlace, Constraint[] inWordWrongPlace, Constraint[] notInWord){

        // For letter in specific place constraint
        for (Constraint constraint: inWordInCorrrectPlace) {
            if (word.charAt(constraint.getPosition()) != constraint.getConstraintChar()){
                return false;
            }
        }

        // For letter in a word but in a wrong place
        for (Constraint constraint: inWordWrongPlace) {
            int index = word.indexOf(constraint.getConstraintChar());
            if (index == -1){
                return false;
            }
            else if (index == constraint.getPosition()){
                return false;
            }
        }

        // Letter not in a word
        for (Constraint constraint: notInWord) {
            if (word.indexOf(constraint.getConstraintChar()) != -1){
                return false;
            }
        }

        // Word is vlaid, return true
        return true;
    }

}
