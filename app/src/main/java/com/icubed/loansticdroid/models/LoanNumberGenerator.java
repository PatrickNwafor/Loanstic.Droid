package com.icubed.loansticdroid.models;

import java.util.Random;

public class LoanNumberGenerator {

    public static String generateLoanNumber(String loanTypeAbbreviation){
        String numbers = String.valueOf(generateRandomNumber());
        String alpha = String.valueOf(generateRandomAlphabet()).toUpperCase();

        return loanTypeAbbreviation+numbers+alpha;
    }

    private static int generateRandomNumber(){
        final int min = 100000000;
        final int max = 999999999;
        return new Random().nextInt((max - min) + 1) + min;
    }

    private static char generateRandomAlphabet(){
        Random r = new Random();
        String alphabet = "123xyz";
        return alphabet.charAt(r.nextInt(alphabet.length()));
    }
}
