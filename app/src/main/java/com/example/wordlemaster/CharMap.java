package com.example.wordlemaster;

import android.content.Context;

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
    public int get(char c){
        return map[charToIndex(c)];
    }

    // Used to update value mapped to given character by 1
    public void update(char c){
        map[charToIndex(c)]++;
    }

    // Used to update value mapped to given character by val
    public void update(char c, int val){
        map[charToIndex(c)] += val;
    }

    // Maps character to an index
    private int charToIndex(char c){
        return  (int)c - 97;
    }
}
