package com.example.wordlemaster;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WordleMaster {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    private Context context;
    private String[] allWords;
    private String[] currentPossibleWords;
    private ConstraintGroup currentConstraintGroup = new ConstraintGroup();

    /////////////////////////
    /// Class constructor ///
    /////////////////////////

    // Constructoro
    public WordleMaster(Context context){
        this.context = context;
        popAllWordsArray();
    }

    ///////////////////////////////
    /// Class support utilities ///
    ///////////////////////////////

    // Populates allWords array
    private void popAllWordsArray(){

        // Reads text files
        InputStream inputStreamWordsA = context.getResources().openRawResource(R.raw.wordle_words);
        InputStream inputStreamWordsB = context.getResources().openRawResource(R.raw.wordle_words);
        InputStream inputStreamFlattened = context.getResources().openRawResource(R.raw.wordle_words_flattened);

        // Reads and converts into an array of strings
        allWords = readInputStream(inputStreamWordsA).split("\n");
        currentPossibleWords = readInputStream(inputStreamWordsB).split("\n");
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

        // Prints list of possible words
        //TODO
        System.out.println("............................");
        for (String word: currentPossibleWords) {
            System.out.println(word);
        }
        System.out.println("Total words: " + currentPossibleWords.length);
        System.out.println("............................");

        // Recalculates possible moves
        currentPossibleWords = calculatePossibleWords(currentPossibleWords, currentConstraintGroup);

        // Check weather the correct word has been found
        if (currentPossibleWords.length <= 2){
            return currentPossibleWords[0];
        }
        else{
            // Calculates charmap
            CharMap charMap = countLetters(currentPossibleWords);
            // Clamps the charm map
            charMap.clampSet(currentPossibleWords.length);

            // Creats empty variables
            int bestScore = -1;
            String bestWord = null;

            // Find best word
            for (int i = 0; i < allWords.length; i++){
                int currentScore = scoreWord(allWords[i], charMap, currentConstraintGroup);
                if (currentScore > bestScore){
                    bestScore = currentScore;
                    bestWord = allWords[i];
                }
            }

            // Retrun best word
            return bestWord;
        }
    }

    // Adds constraints
    public void addConstraints(Constraint[] constraints){
        currentConstraintGroup.addConstraints(constraints);
    }

    // Adds constraint
    public void addConstraints(Constraint constraint){
        addConstraints(new Constraint[]{constraint});
    }

    // Sets constraints
    public void setConstraints(Constraint[] constraints){
        currentConstraintGroup.clear();
        currentConstraintGroup.addConstraints(constraints);
    }

    // Calculates possible words based on the constraints
    private String[] calculatePossibleWords(String[] possibleWords, ConstraintGroup constraintGroup){

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

        // Retruns new possible words
        String[] newPossibleWords = new String[validWords.size()];
        validWords.toArray(newPossibleWords);
        return newPossibleWords;
    }

    // Calculates possible words based on the constraint
    private String[] calculatePossibleWords(String[] possibleWords, Constraint constraint){
        // Creats new constraint group
        ConstraintGroup constraintGroup = new ConstraintGroup();
        constraintGroup.addConstraint(constraint);
        // Class teh native method with the constraint wraped in a contrant group
        return calculatePossibleWords(possibleWords, constraintGroup);
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

    ///////////////////////////////
    /// Class solving utilities ///
    ///////////////////////////////

    // Retruns score for a word based on the suplied CharMap
    private int scoreWord(String word, CharMap charMap, ConstraintGroup constraintGroup){

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

    // Returns a socre for a word using a precise technique, takes much longer then quick approximation technique
    private float scoreWord(String word, String[] possibleWords, int currentPos){

        // Creats initial score
        float score = 0;

        // Checks if there are any possible words
        if (possibleWords.length == 0){
            return 0;
        }

        // Calculates sets
        String[][] sets = new String[3][];
        sets[0] = calculatePossibleWords(possibleWords, new Constraint(word.charAt(currentPos), ConstraintType.notInWord, currentPos));
        sets[1] = calculatePossibleWords(possibleWords, new Constraint(word.charAt(currentPos), ConstraintType.inWordWrongPlace, currentPos));
        sets[2] = calculatePossibleWords(possibleWords, new Constraint(word.charAt(currentPos), ConstraintType.inWordCorrectPlace, currentPos));

        // If its the last char in a string, then we return the score for this char
        if (currentPos + 1 == word.length()){
            return ((float) sets[0].length * (float) sets[0].length / (float) possibleWords.length) +
                    ((float) sets[1].length * (float) sets[1].length / (float) possibleWords.length) +
                    ((float) sets[2].length * (float) sets[2].length / (float) possibleWords.length);
        }
        // Its not the last char and a curcrsive calls need to be made to evaluate the strengh of ubsequent characters
        else{
            return ((float) (scoreWord(word, sets[0], currentPos + 1)) * (float) sets[0].length / (float) possibleWords.length) +
                    ((float) (scoreWord(word, sets[1], currentPos + 1)) * (float) sets[1].length / (float) possibleWords.length) +
                    ((float) (scoreWord(word, sets[2], currentPos + 1)) * (float) sets[2].length / (float) possibleWords.length);
        }
    }

    public String test(){
        
        // Initall best word, and initilal best score
        String bestWord = "somthing went wrong!!!";
        float bestScore = Float.MAX_VALUE;


        // Find best word
        int count = 0;
        int total = allWords.length;
        for (String word: allWords) {
            float socre = scoreWord(word, currentPossibleWords, 0);
            if (socre < bestScore){
                bestScore = socre;
                bestWord = word;
            }

            count++;
            System.out.println((count * 100f / (float)total) + "%" + " : " + count + "/" + total);
        }

        // Returns best word
        for (String word: currentPossibleWords) {
            System.out.println(word);
        }
        System.out.println(bestScore);
        return bestWord;
    }
}
