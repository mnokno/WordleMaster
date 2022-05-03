package ai;

import androidx.annotation.NonNull;
import com.example.wordlemaster.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WordleMaster implements Serializable {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    private String[] allWords;
    private String[] currentPossibleWords;
    private ConstraintGroup currentConstraintGroup = new ConstraintGroup();
    public String data;

    /////////////////////////
    /// Class constructor ///
    /////////////////////////

    // Constructoro
    public WordleMaster(String[] words){
        allWords = words;
        currentPossibleWords = words;
    }

    ///////////////////////////////
    /// Class support utilities ///
    ///////////////////////////////

    // Adds constraints
    public void addConstraints(@NonNull Constraint[] constraints){
        currentConstraintGroup.addConstraints(constraints);
    }

    // Adds constraint
    public void addConstraints(@NonNull Constraint constraint){
        addConstraints(new Constraint[]{constraint});
    }

    // Sets constraints
    public void setConstraints(@NonNull Constraint[] constraints){
        currentConstraintGroup.clear();
        currentConstraintGroup.addConstraints(constraints);
    }

    ///////////////////////////////
    /// Class solving utilities ///
    ///////////////////////////////

    // Retruns recomended word to guess
    @NonNull
    public String recomendWord(@NonNull SearchType searchType){

        // Recalculates possible moves
        currentPossibleWords = calculatePossibleWords(currentPossibleWords, currentConstraintGroup);

        // Check weather the correct word has been found
        if (currentPossibleWords.length <= 2){
            return currentPossibleWords[0];
        }
        else{
            if (searchType == SearchType.fastApproximation){
                return approximateBestWord(currentPossibleWords, currentConstraintGroup);
            }
            else{
                if (currentPossibleWords.length == allWords.length){
                    //new Thread(() -> {
                    //    // code goes here.
                    //}).start();
                    return "lares";
                }
                else {
                    return findBestWord(currentPossibleWords);
                }
            }
        }
    }

    // Calculates possible words based on the constraints
    @NonNull
    private String[] calculatePossibleWords(@NonNull String[] possibleWords, @NonNull ConstraintGroup constraintGroup){

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
    @NonNull
    private String[] calculatePossibleWords(@NonNull String[] possibleWords, @NonNull Constraint constraint){
        // Creats new constraint group
        ConstraintGroup constraintGroup = new ConstraintGroup();
        constraintGroup.addConstraint(constraint);
        // Class teh native method with the constraint wraped in a contrant group
        return calculatePossibleWords(possibleWords, constraintGroup);
    }

    // Return true if given word is valid withiin given constraints
    @NonNull
    private boolean isValid(@NonNull String word, @NonNull Constraint[] inWordInCorrrectPlace, @NonNull Constraint[] inWordWrongPlace, @NonNull Constraint[] notInWord){

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

    ////////////////////////////////
    /// Fast aproximation seatch ///
    ////////////////////////////////

    // Returns best word using fast aroximation search
    @NonNull
    private String approximateBestWord(@NonNull String[] possibleWords, @NonNull ConstraintGroup constraintGroup){
        // Calculates charmap
        CharMap charMap = countLetters(possibleWords);
        // Clamps the charm map
        charMap.clampSet(possibleWords.length);

        // Creats empty variables
        int bestScore = -1;
        String bestWord = null;

        // Find best word
        for (int i = 0; i < allWords.length; i++){
            int currentScore = scoreWord(allWords[i], charMap, constraintGroup);
            if (currentScore > bestScore){
                bestScore = currentScore;
                bestWord = allWords[i];
            }
        }

        // Retrun best word
        return bestWord;
    }

    // Retruns score for a word based on the suplied CharMap
    @NonNull
    private int scoreWord(@NonNull String word, @NonNull CharMap charMap, @NonNull ConstraintGroup constraintGroup){

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

    // Counts accurance of each letter in suplided list of words, words most be flattend, ducilated letters in a word are't coutned
    @NonNull
    private CharMap countLetters(@NonNull String[] words){
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

    //////////////////////////
    /// Slow exact seatch ///
    /////////////////////////

    // Finds best words using eaxt search
    @NonNull
    private String findBestWord(@NonNull String[] possibleWords){

        // Initall best word, and initilal best score
        String bestWord = null;
        float bestScore = Float.MAX_VALUE;
        String worstWord = null;
        float worstScore = Float.MIN_VALUE;


        // Find best word
        int count = 0;
        int total = allWords.length;
        for (String word: allWords) {
            float socre = scoreWord(word, possibleWords, 0);
            if (socre < bestScore){
                bestScore = socre;
                bestWord = word;
            }
            else if (socre > worstScore){
                worstScore = socre;
                worstWord = word;
            }

            count++;
            System.out.println(String.format("%.5g%n", count * 100f / (float)total).trim() + "%" + " : " + count + "/" + total + " --- " + word + " : " + socre + " ARP");
        }

        // Returns best word
        System.out.println("BEST: " + bestWord + " --- Score: " + bestScore + " ARP");
        System.out.println("WORST: " + worstWord + " --- Score: " + worstScore + " ARP");
        return bestWord;
    }

    // Returns a socre for a word using a precise technique, takes much longer then quick approximation technique
    @NonNull
    private float scoreWord(@NonNull String word, @NonNull String[] possibleWords, @NonNull int currentPos){

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
}
