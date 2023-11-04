package org.noah;

import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * This class makes use of the static methods of the JWTService class for token generation and validation
 * The JWTSERVICE class can be found in the 'org.noah' package
 */
public class UserDetailsVerification {

    /**
     *
     * @param username the username of the user
     * @return a boolean which determines if the username argument passed in is valid
     */
    private static boolean usernameValidation(String username) {
        return username.length() > 4;
    }

    /**
     *
     * @param email the email of the user
     * @return a boolean true indicating that the email format is valid
     */
    private static boolean emailValidation(String email) {
        String validEmailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(validEmailPattern).matcher(email).matches();
    }

    /**
     *
     * @param password the password of the user
     * @return a boolean true indicating that the password meets all validation checks
     */
    private static boolean passwordValidation(String password) {
        String validPasswordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        return Pattern.compile(validPasswordPattern).matcher(password).matches();
    }

    /**
     *
     * @param dateOfBirth the date of birth of the user in LocalDate format
     * @return a boolean false if the user's is below 16
     */
    private static boolean dateOfBirthValidation(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        LocalDate currentDate = LocalDate.now();
        return dateOfBirth.isBefore(currentDate.minusYears(16));
    }


    /**
     *
     * @param username
     * @param email
     * @param password
     * @param dateOfBirth
     * @return string.
     *         If all the validation checks for the parameters passes, JWT Token is generated.
     *         If there are any failures, a string of the input type and the corresponding error message is returned.
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
     *
     * @param username
     * @param email
     * @param password
     * @param dateOfBirth
     * @return string.
     *         This method is concurrent and uses CompletableFutures
     *         If all the validation checks for the parameters passes, JWT Token is generated.
     *         If there are any failures, a string of the input type and the corresponding error message is returned.
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
     *
     * @param args
     *        The non-concurrent and concurrent methods for validating user details are run
     *        The validity of the JWT token for the concurrent method is subsequently tested
     */
    public static void main(String[] args) {
        System.out.println(validateUserDetails("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")));
        System.out.println(validateUserDetailsUsingConcurrency("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")));
        System.out.println(JWTService.isTokenValid(validateUserDetails("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")), "Johnson"));
    }


}
