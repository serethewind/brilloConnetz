package org.noah;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * This class simulates a scenario where a shopkeeper produces pancakes and customers place orders for them.
 * It models the pancake-making process, records user orders, and provides a report on the process for multiple rounds.
 * This is the non-concurrent solution.
 */
public class PancakeOperator {

    private static final int NUMBER_OF_USERS = 3;
    private static final int MAXIMUM_PANCAKES_BY_USER = 5;
    private static final int MAXIMUM_PANCAKES_BY_SHOPKEEPER = 12;

    /**
     * Generates a random number of pancakes made by the shopkeeper.
     *
     * @return The number of pancakes made.
     */
    private static int numberOfPancakesMade() {
        Random random = new Random();
        return random.nextInt(MAXIMUM_PANCAKES_BY_SHOPKEEPER + 1);
    }

    /**
     * Simulates a round of pancake production and user orders for a specified number of rounds.
     *
     * @param numberOfRounds The number of rounds to simulate.
     * @return A report summarizing the details of each round, including start and end times, quantities of pancakes made and eaten, user orders, needs fulfillment, unmet orders, and wastage.
     */
    public static String simulateRound(int numberOfRounds) {
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(30, ChronoUnit.SECONDS);
        int totalNumberOfPancakesMade = numberOfPancakesMade();
        int[] pancakesOrderForEachUser = pancakesOrderedByEachUser();
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
    }

    /**
     * Generates a report based on the details of a round, including start and end times, quantities of pancakes made and eaten, user orders, needs fulfillment, unmet orders, and wastage.
     * This method is called by the SimulateRound method.
     *
     * @param startTime               The starting time of the round.
     * @param endTime                 The ending time of the round.
     * @param totalNumberOfPancakesMade The total number of pancakes made in the round.
     * @param viewPancakeForEachUser   The quantities of pancakes ordered by each user.
     * @param totalNumberOfPancakesEaten The total number of pancakes eaten by all users.
     * @param areNeedsMet              A boolean indicating if the needs of all users were met by the shopkeeper.
     * @param unmetOrders              The number of unmet pancake orders if needs were not met.
     * @param waste                    The number of wasted pancakes, if any.
     * @return A report summarizing the details of the round.
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
     * Generates random pancake orders for each user.
     *
     * @return An array of ordered quantities, one for each user.
     */
    public static int[] pancakesOrderedByEachUser() {
        Random random = new Random();
        int[] pancakeForEachUser = new int[NUMBER_OF_USERS];
        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            pancakeForEachUser[i] = random.nextInt(MAXIMUM_PANCAKES_BY_USER + 1);
        }
        return pancakeForEachUser;
    }

    /**
     * Creates a formatted string representing the pancake orders of each user.
     *
     * @param pancakeForAllUsers An array of ordered quantities for all users.
     * @return A formatted string displaying the pancake orders for each user.
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
     * @param pancakeForAllUsers An array of ordered quantities for all users.
     * @return The total number of pancakes eaten.
     */
    private static int totalPancakesEaten(int[] pancakeForAllUsers) {
        int totalPancakesEaten = 0;
        for (int pancakeForEachUser : pancakeForAllUsers) {
            totalPancakesEaten += pancakeForEachUser;
        }
        return totalPancakesEaten;
    }

    /**
     * The main method runs the pancake-making simulation for a specified number of rounds and prints reports to the console.
     *
     * @param args Command-line arguments (not used in this simulation).
     */
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            System.out.println(simulateRound(i));
        }
    }
}
