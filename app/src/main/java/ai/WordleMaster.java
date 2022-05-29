package ai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public final class WordleMaster implements Serializable {

    ///////////////////////
    /// Class variables ///
    ///////////////////////

    static private String[] possibleEntries;
    static private String[] possibleEntriesLimited;

    static private String[] possibleSolusions;
    static private String[] possibleSolusionsLimited;

    static private ConstraintGroup currentConstraintGroup = new ConstraintGroup();

    static private float taskPresentageCompletion;
    static public float getTaskPresentageCompletion() { return taskPresentageCompletion; }

    static private String result;
    static public String getResult() { return result; }

    /////////////////////////
    /// Class constructor ///
    /////////////////////////

    // Constructoro
    public static void SetWordSets(String[] entries, String[] solusions){
        possibleEntries = entries;
        possibleEntriesLimited = entries;
        possibleSolusions = solusions;
        possibleSolusionsLimited = solusions;
    }

    ///////////////////////////////
    /// Class support utilities ///
    ///////////////////////////////

    // Adds constraints
    public static void addConstraints(@NonNull Constraint[] constraints){
        currentConstraintGroup.addConstraints(constraints);
    }

    // Adds constraint
    public static void addConstraints(@NonNull Constraint constraint){
        addConstraints(new Constraint[]{constraint});
    }

    // Sets constraints
    public static void setConstraints(@NonNull Constraint[] constraints){
        currentConstraintGroup.clear();
        currentConstraintGroup.addConstraints(constraints);
    }

    ///////////////////////////////
    /// Class solving utilities ///
    ///////////////////////////////

    // Retruns recomended word to guess
    @Nullable
    public static String recomendWord(@NonNull SearchType searchType, Boolean async){

        // Restes results and prgoress counter
        result = null;
        taskPresentageCompletion = 0;

        // Recalculates possible moves
        possibleEntriesLimited = calculatePossibleWords(possibleEntriesLimited, currentConstraintGroup);
        possibleSolusionsLimited = calculatePossibleWords(possibleSolusionsLimited, currentConstraintGroup);

        // Check weather the correct word has been found
        if (possibleEntriesLimited.length <= 2){
            if (async){
                result = possibleEntriesLimited[0];
                taskPresentageCompletion = 1;
                return null;
            }
            else{
                return possibleEntriesLimited[0];
            }
        }
        else{
            if (searchType == SearchType.fastApproximation){
                if (async){
                    new Thread(() -> {
                        approximateBestWord(possibleEntriesLimited, currentConstraintGroup);
                    }).start();
                    return null;
                }
                else{
                    return approximateBestWord(possibleEntriesLimited, currentConstraintGroup);
                }
            }
            else{
                if (possibleEntriesLimited.length == possibleEntries.length){
                    if (async){
                        result = "lares";
                        taskPresentageCompletion = 1;
                        return null;
                    }
                    else{
                        return "lares";
                    }
                }
                else {
                    if (async){
                        new Thread(() -> {
                            findBestWord(possibleEntriesLimited);
                        }).start();
                        return null;
                    }
                    else{
                        return findBestWord(possibleEntriesLimited);
                    }
                }
            }
        }
    }

    // Calculates possible words based on the constraints
    @NonNull
    private static String[] calculatePossibleWords(@NonNull String[] possibleWords, @NonNull ConstraintGroup constraintGroup){

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
    private static String[] calculatePossibleWords(@NonNull String[] possibleWords, @NonNull Constraint constraint){
        // Creats new constraint group
        ConstraintGroup constraintGroup = new ConstraintGroup();
        constraintGroup.addConstraint(constraint);
        // Class teh native method with the constraint wraped in a contrant group
        return calculatePossibleWords(possibleWords, constraintGroup);
    }

    // Return true if given word is valid withiin given constraints
    @NonNull
    private static boolean isValid(@NonNull String word, @NonNull Constraint[] inWordInCorrrectPlace, @NonNull Constraint[] inWordWrongPlace, @NonNull Constraint[] notInWord){

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
    private static String approximateBestWord(@NonNull String[] possibleWords, @NonNull ConstraintGroup constraintGroup){
        // Calculates charmap
        CharMap charMap = countLetters(possibleWords);
        // Clamps the charm map
        charMap.clampSet(possibleWords.length);

        // Creats empty variables
        int bestScore = -1;
        String bestWord = null;

        // Initiates counter
        int count = 0;
        // Find best word
        for (int i = 0; i < possibleEntries.length; i++){
            int currentScore = scoreWord(possibleEntries[i], charMap, constraintGroup);
            if (currentScore > bestScore){
                bestScore = currentScore;
                bestWord = possibleEntries[i];
            }

            // Updates tasks progress
            taskPresentageCompletion = count / (float) possibleEntries.length;
            // Updates counter
            count++;
        }

        // Updates result
        result = bestWord;
        // Updates complition persentage
        taskPresentageCompletion = 1;
        // Retrun best word
        return bestWord;
    }

    // Retruns score for a word based on the suplied CharMap
    @NonNull
    private static int scoreWord(@NonNull String word, @NonNull CharMap charMap, @NonNull ConstraintGroup constraintGroup){

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
    private static CharMap countLetters(@NonNull String[] words){
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
    private static String findBestWord(@NonNull String[] possibleWords){

        // Initall best word, and initilal best score
        String bestWord = null;
        float bestScore = Float.MAX_VALUE;
        String worstWord = null;
        float worstScore = Float.MIN_VALUE;

        // Initiates counter
        int count = 0;
        // Find best word
        for (String word: possibleEntries) {
            float socre = scoreWord(word, possibleEntries, 0);
            if (socre < bestScore){
                bestScore = socre;
                bestWord = word;
            }
            else if (socre > worstScore){
                worstScore = socre;
                worstWord = word;
            }

            // Updates tasks progress
            taskPresentageCompletion = count / (float) possibleEntries.length;
            // Updates counter
            count++;
            // Console progress log
            System.out.println(String.format("%.5g%n", count * 100f / (float) possibleEntries.length).trim() + "%" + " : " + count + "/" + possibleEntries.length + " --- " + word + " : " + socre + " ARP");
        }

        // Updates result
        result = bestWord;
        // Updates progress
        taskPresentageCompletion = 1;
        // Logs results to the console
        System.out.println("BEST: " + bestWord + " --- Score: " + bestScore + " ARP");
        System.out.println("WORST: " + worstWord + " --- Score: " + worstScore + " ARP");
        // Returns best word
        return bestWord;
    }

    // Returns a socre for a word using a precise technique, takes much longer then quick approximation technique
    @NonNull
    private static float scoreWord(@NonNull String word, @NonNull String[] possibleWords, @NonNull int currentPos){

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
