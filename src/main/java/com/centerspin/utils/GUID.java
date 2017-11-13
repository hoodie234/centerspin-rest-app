package com.centerspin.utils;


public class GUID {

    
    private static final String POSSIBLE_CHARS = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    public static String generate() {
        
        String gui = "";
        
        for (int i = 0; i < 8; i++) {
            
            int randomInt = (int)(Math.random()*POSSIBLE_CHARS.length());
            gui += POSSIBLE_CHARS.charAt(randomInt);
        }
        
        return gui;
    }
}
