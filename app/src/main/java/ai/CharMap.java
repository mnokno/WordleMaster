package ai;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.Arrays;

public class CharMap {

    ///////////////////////
    /// Class variables ///
    ///////////////////////
    private final int[] map = new int[26];

    ////////////////////////////
    /// Class initialization ///
    ////////////////////////////

    // Class constructor
    public CharMap(){
        initMap();
    }

    // Initiates each filed of array map to 0
    private void initMap(){
        for (int i = 0; i < 26; i++) {
            map[i] = 0;
        }
    }

    ///////////////////////
    /// Class utilities ///
    ///////////////////////

    // Used to get value from map
    @NonNull
    public int get(@NonNull char c){
        return map[charToIndex(c)];
    }

    // Used to update value mapped to given character by 1
    public void update(@NonNull char c){
        map[charToIndex(c)]++;
    }

    // Used to update value mapped to given character by val
    public void update(@NonNull char c, @NonNull int val){
        map[charToIndex(c)] += val;
    }

    // Maps character to an index
    @NonNull
    private int charToIndex(@NonNull char c){
        return  (int)c - 97;
    }

    // Maps index to a character
    @NonNull
    private char indexToChar(@NonNull int index){
        return (char)(index + 97);
    }

    // Clamps this char map to a given set size
    public void clampSet(@NonNull int setSize){
        // Claculates midpoint
        int midPoint = (int)Math.floor(setSize / 2);

        // Clamps each character
        for (int i = 0; i < map.length; i++){
            if (map[i] > midPoint){
                map[i] = midPoint - map[i];
            }
        }
    }

    //////////////////////////
    /// Generic overwrites ///
    //////////////////////////

    @Override
    @NonNull
    public String toString() {

        // Create empty string
        StringBuilder formatted = new StringBuilder();

        // Formats all the data stored in the map
        for (int i = 0; i < 26; i++){
            formatted.append(indexToChar(i)).append(" - ").append(map[i]).append("\n");
        }

        // Returns the formatted string
        return formatted.toString().trim();
    }
}
