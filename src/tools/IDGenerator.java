package tools;

import java.util.Random;

public class IDGenerator {
    private static final int MIN_ID = 1000; // Minimum 4-digit number
    private static final int MAX_ID = 9999; // Maximum 4-digit number

    public static int generateRandomID() {
        Random random = new Random();
        return random.nextInt(MAX_ID - MIN_ID + 1) + MIN_ID;
    }

    public static void main(String[] args) {
        int randomID = generateRandomID();
        System.out.println("Random 4-digit ID: " + randomID);
    }
}
