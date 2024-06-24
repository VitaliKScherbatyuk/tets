/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * The VerificationCodeGenerator class is designed to generate a random six-digit code
 * that can be used to verify users
 */
public class VerificationCodeGenerator {

    static Logger logger = LoggerFactory.getLogger(VerificationCodeGenerator.class);

    /**
     * Generates a number between 100000 and 999999
     * Returns the code as a string
     * @return the generated code as a string
     */
    public static String generateCode() {

        try {
            Random random = new Random();
            int code = 100000 + random.nextInt(900000);
            return String.valueOf(code);
        } catch (Exception e) {
            logger.error(" VerificationCodeGenerator: Error generating verification code", e);
            return "000000";
        }
    }
}
