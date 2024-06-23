/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.config;

import java.util.Random;

/**
 * The VerificationCodeGenerator class is designed to generate a random six-digit code
 * that can be used to verify users
 */
public class VerificationCodeGenerator {

    /**
     * Generates a number between 100000 and 999999
     * Returns the code as a string
     * @return
     */
    public static String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
