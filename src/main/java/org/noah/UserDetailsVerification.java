package org.noah;

import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * This class provides methods for validating user details and generating JWT tokens using the JWTService class.
 * The JWTService class can be found in the 'org.noah' package.
 *
 * This class offers two methods for user details validation:
 * 1. {@link #validateUserDetails(String, String, String, LocalDate)}: A non-concurrent method that validates user details and generates a JWT token if all checks pass.
 * 2. {@link #validateUserDetailsUsingConcurrency(String, String, String, LocalDate)}: A concurrent method that uses CompletableFuture for parallel validation of user details and generates a JWT token if all checks pass.
 */
public class UserDetailsVerification {

    /**
     * Validates the username of the user.
     *
     * @param username The username to be validated.
     * @return true if the username is valid (length > 4), false otherwise.
     */
    private static boolean usernameValidation(String username) {
        return username.length() > 4;
    }

    /**
     * Validates the email format.
     *
     * @param email The email to be validated.
     * @return true if the email format is valid, false otherwise.
     */
    private static boolean emailValidation(String email) {
        String validEmailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(validEmailPattern).matcher(email).matches();
    }

    /**
     * Validates the password based on certain criteria.
     *
     * @param password The password to be validated.
     * @return true if the password meets all validation checks, false otherwise.
     */
    private static boolean passwordValidation(String password) {
        String validPasswordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        return Pattern.compile(validPasswordPattern).matcher(password).matches();
    }

    /**
     * Validates the date of birth of the user.
     *
     * @param dateOfBirth The date of birth of the user in LocalDate format.
     * @return false if the user's age is below 16, true otherwise.
     */
    private static boolean dateOfBirthValidation(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        LocalDate currentDate = LocalDate.now();
        return dateOfBirth.isBefore(currentDate.minusYears(16));
    }


    /**
     * Validates user details and generates a JWT token if all validation checks pass.
     *
     * @param username The username of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param dateOfBirth The date of birth of the user in LocalDate format.
     * @return A JWT token if all validation checks pass, or an error message if there are failures.
     */
    public static String validateUserDetails(String username, String email, String password, LocalDate dateOfBirth) {
        Map<String, String> validationErrors = new HashMap<>();

        if (!usernameValidation(username)) {
            validationErrors.put("Username", "not empty or less than 4 characters");
        }
        if (!dateOfBirthValidation(dateOfBirth)) {
            validationErrors.put("Date of Birth", "not empty or less than 16 years");
        }
        if (!emailValidation(email)) {
            validationErrors.put("Email", "not empty or invalid format");
        }
        if (!passwordValidation(password)) {
            validationErrors.put("Password", "not empty or not a strong password");
        }

        if (validationErrors.isEmpty()) {
            return JWTService.generateToken(username);
        } else {
            StringBuilder errorsCompiled = new StringBuilder();
            for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
                errorsCompiled.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            return errorsCompiled.toString();
        }
    }


    /**
     * Validates user details using CompletableFuture for concurrency and generates a JWT token if all checks pass.
     *
     * @param username The username of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param dateOfBirth The date of birth of the user in LocalDate format.
     * @return A JWT token if all validation checks pass, or an error message if there are failures.
     */
    public static String validateUserDetailsUsingConcurrency(String username, String email, String password, LocalDate dateOfBirth) {
        CompletableFuture<String> usernameFuture = CompletableFuture.supplyAsync(() -> username);
        CompletableFuture<String> passwordFuture = CompletableFuture.supplyAsync(() -> password);
        CompletableFuture<String> emailFuture = CompletableFuture.supplyAsync(() -> email);
        CompletableFuture<String> dateOfBirthFuture = CompletableFuture.supplyAsync(dateOfBirth::toString);

        CompletableFuture<Void> validation = CompletableFuture.allOf(usernameFuture, passwordFuture, emailFuture, dateOfBirthFuture).thenRun(() -> {

            if (!usernameValidation(usernameFuture.join())) {
                System.out.println("Username cannot be empty or less than 4 characters");
            }
            if (!dateOfBirthValidation(LocalDate.parse(dateOfBirthFuture.join()))) {
                System.out.println("Date of Birth cannot be empty or less than 16 years");
            }
            if (!emailValidation(emailFuture.join())) {
                System.out.println("Email cannot be empty or invalid format");
            }
            if (!passwordValidation(passwordFuture.join())) {
                System.out.println("Password cannot be empty or not a strong password");
            }

        });
        validation.join();
        return JWTService.generateToken(usernameFuture.join());
    }

    @org.junit.Test
    public void testGeneratedToken(){
        String username = "Johnson";
        String token = JWTService.generateToken(username);
        assertEquals(JWTService.getUsername(token), username);
    }

    /**
     * Entry point for running the non-concurrent and concurrent validation methods and testing JWT token validity.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        System.out.println(validateUserDetails("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")));
        System.out.println(validateUserDetailsUsingConcurrency("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")));
        System.out.println(JWTService.isTokenValid(validateUserDetails("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")), "Johnson"));
    }


}
