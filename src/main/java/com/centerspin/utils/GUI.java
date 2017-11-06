package com.centerspin.utils;


public class GUI {

    
    private static final String POSSIBLE_CHARS = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    public static String getNewGUI() {
        
        String gui = "";
        
        for (int i = 0; i < 8; i++) {
            
            int randomInt = (int)(Math.random()*POSSIBLE_CHARS.length());
            gui += POSSIBLE_CHARS.charAt(randomInt);
        }
        
        return gui;
    }
}
