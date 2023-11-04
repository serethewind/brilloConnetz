package org.noah;

import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class UserDetailsVerification {


    private static boolean usernameValidation(String username) {
        return username.length() > 4;
    }

    private static boolean emailValidation(String email) {
        String validEmailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(validEmailPattern).matcher(email).matches();
    }

    private static boolean passwordValidation(String password) {
        String validPasswordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        return Pattern.compile(validPasswordPattern).matcher(password).matches();
    }

    private static boolean dateOfBirthValidation(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        LocalDate currentDate = LocalDate.now();
        return dateOfBirth.isBefore(currentDate.minusYears(16));
    }


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

    public static void main(String[] args) {
        System.out.println(validateUserDetails("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")));
        System.out.println(validateUserDetailsUsingConcurrency("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")));
        System.out.println(JWTService.isTokenValid(validateUserDetails("Johnson", "osasereu@gmail.com", "gtfBrillo#90", LocalDate.parse("2003-12-01")), "Johnson"));
    }


}
