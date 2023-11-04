package org.noah;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The `PancakeOperatorConcurrent` class provides methods to simulate pancake order processing concurrently using CompletableFuture.
 * It allows for the asynchronous processing of pancake orders for multiple rounds.
 */
public class PancakeOperatorConcurrent {

    private static final int NUMBER_OF_USERS = 3;
    private static final int MAXIMUM_PANCAKES_BY_USER = 5;
    private static final int MAXIMUM_PANCAKES_BY_SHOPKEEPER = 12;

    private static int numberOfPancakesMade() {
        Random random = new Random();
        return random.nextInt(MAXIMUM_PANCAKES_BY_SHOPKEEPER + 1);
    }

    /**
     * Simulates a single round of pancake order processing concurrently using CompletableFuture.
     *
     * @param numberOfRounds The number of rounds to simulate.
     * @return A CompletableFuture representing the result of the simulation for the specified round.
     * @throws InterruptedException if the thread is interrupted while waiting for the CompletableFuture result.
     * @throws ExecutionException if the computation threw an exception.
     */
    public static CompletableFuture<String> simulateRoundConcurrently(int numberOfRounds) throws ExecutionException, InterruptedException {
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(30, ChronoUnit.SECONDS);

        CompletableFuture<Integer> totalNumberOfPancakesMadeFuture = CompletableFuture.supplyAsync(PancakeOperatorConcurrent::numberOfPancakesMade);
        CompletableFuture<int[]> pancakesOrderForEachUserFuture = CompletableFuture.supplyAsync(PancakeOperatorConcurrent::pancakesOrderedByEachUser);

        return CompletableFuture.allOf(totalNumberOfPancakesMadeFuture, pancakesOrderForEachUserFuture)
                .thenApply(ignoredVoid -> {
                    int totalNumberOfPancakesMade = totalNumberOfPancakesMadeFuture.join();
                    int[] pancakesOrderForEachUser = pancakesOrderForEachUserFuture.join();

                    String viewPancakesForEachUser = viewPancakesOrderedByEachUser(pancakesOrderForEachUser);
                    int totalNumberOfPancakesEaten = totalPancakesEaten(pancakesOrderForEachUser);
                    boolean areNeedsOfAllUsersMet = totalNumberOfPancakesMade >= totalNumberOfPancakesEaten;
                    String unmetOrders = 0 + ", All orders placed by the user were fulfilled.";
                    int wastage = 0;
                    if (!areNeedsOfAllUsersMet) {
                        unmetOrders = String.valueOf(totalNumberOfPancakesEaten - totalNumberOfPancakesMade);
                    }
                    if (totalNumberOfPancakesMade > totalNumberOfPancakesEaten) {
                        wastage = totalNumberOfPancakesMade - totalNumberOfPancakesEaten;
                    }
                    return report(startTime.toString(), endTime.toString(), totalNumberOfPancakesMade, viewPancakesForEachUser, totalNumberOfPancakesEaten, areNeedsOfAllUsersMet, unmetOrders, wastage);
                });
    }

    /**
     * Generates a report based on the simulation results.
     *
     * @param startTime The starting time of the simulation.
     * @param endTime The ending time of the simulation.
     * @param totalNumberOfPancakesMade The total number of pancakes made by the shopkeeper.
     * @param viewPancakeForEachUser A view of the pancakes ordered by each user.
     * @param totalNumberOfPancakesEaten The total number of pancakes eaten by users.
     * @param areNeedsMet A boolean indicating whether the needs of all users are met.
     * @param unmetOrders The number of unmet pancake orders (if any).
     * @param waste The number of wasted pancakes (if any).
     * @return A report containing simulation results.
     */
    private static String report(String startTime, String endTime, int totalNumberOfPancakesMade, String viewPancakeForEachUser, int totalNumberOfPancakesEaten, boolean areNeedsMet, String unmetOrders, int waste) {

        String report = "Starting time: " + startTime + "\n"
                + "Ending time: " + endTime + "\n"
                + "Number of packages made: " + totalNumberOfPancakesMade + "\n"
                + "Pancakes for the three users: " + viewPancakeForEachUser + "\n"
                + "Number of packages eaten: " + totalNumberOfPancakesEaten + "\n"
                + "Are needs of all users met by shop keeper: " + String.valueOf(areNeedsMet).toUpperCase() + "\n"
                + "if above question is false, how many pancake orders were not met: " + unmetOrders + "\n"
                + "Number of wasted pancakes if any: " + waste + "\n";

        return report;
    }

    /**
     * Generates an array of random pancake orders for each user.
     *
     * @return An array containing the number of pancakes ordered by each user.
     */
    private static int[] pancakesOrderedByEachUser() {
        Random random = new Random();
        int[] pancakeForEachUser = new int[NUMBER_OF_USERS];
        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            pancakeForEachUser[i] = random.nextInt(MAXIMUM_PANCAKES_BY_USER + 1);
        }
        return pancakeForEachUser;
    }

    /**
     * Converts an array of pancake orders into a formatted string for viewing.
     *
     * @param pancakeForAllUsers An array containing the number of pancakes ordered by each user.
     * @return A formatted string representing the pancake orders for all users.
     */
    private static String viewPancakesOrderedByEachUser(int[] pancakeForAllUsers) {
        StringBuilder view = new StringBuilder();
        for (int pancakeForEachUser : pancakeForAllUsers) {
            view.append(String.valueOf(pancakeForEachUser)).append(", ");
        }
        return view.toString().trim();
    }

    /**
     * Calculates the total number of pancakes eaten based on user orders.
     *
     * @param pancakeForAllUsers An array containing the number of pancakes ordered by each user.
     * @return The total number of pancakes eaten by all users.
     */
    private static int totalPancakesEaten(int[] pancakeForAllUsers) {
        int totalPancakesEaten = 0;
        for (int pancakeForEachUser : pancakeForAllUsers) {
            totalPancakesEaten += pancakeForEachUser;
        }
        return totalPancakesEaten;
    }

    /**
     * The main method to run and display the results of multiple rounds of pancake order simulations concurrently.
     *
     * @param args The command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            try {
                CompletableFuture<String> roundResult = simulateRoundConcurrently(i);
                System.out.println(roundResult.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


}
