package org.noah;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PancakeOperator {

    private static final int NUMBER_OF_USERS = 3;
    private static final int MAXIMUM_PANCAKES_BY_USER = 5;
    private static final int MAXIMUM_PANCAKES_BY_SHOPKEEPER = 12;

    private static int numberOfPancakesMade() {
        Random random = new Random();
        return random.nextInt(MAXIMUM_PANCAKES_BY_SHOPKEEPER + 1);
    }

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

    public static String simulateRoundConcurrently(int numberOfRounds) throws ExecutionException, InterruptedException {
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(30, ChronoUnit.SECONDS);

        CompletableFuture<Integer> totalNumberOfPancakesMadeFuture = CompletableFuture.supplyAsync(PancakeOperator::numberOfPancakesMade);
        CompletableFuture<int[]> pancakesOrderForEachUserFuture = CompletableFuture.supplyAsync(PancakeOperator::pancakesOrderedByEachUser);

        int totalNumberOfPancakesMade = totalNumberOfPancakesMadeFuture.get();
        int[] pancakesOrderForEachUser = pancakesOrderForEachUserFuture.get();

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


    public static int[] pancakesOrderedByEachUser() {
        Random random = new Random();
        int[] pancakeForEachUser = new int[NUMBER_OF_USERS];
        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            pancakeForEachUser[i] = random.nextInt(MAXIMUM_PANCAKES_BY_USER + 1);
        }
        return pancakeForEachUser;
    }

    public static String viewPancakesOrderedByEachUser(int[] pancakeForAllUsers) {
        StringBuilder view = new StringBuilder();
        for (int pancakeForEachUser : pancakeForAllUsers) {
            view.append(String.valueOf(pancakeForEachUser)).append(", ");
        }
        return view.toString().trim();
    }

    public static int totalPancakesEaten(int[] pancakeForAllUsers) {
        int totalPancakesEaten = 0;
        for (int pancakeForEachUser : pancakeForAllUsers) {
            totalPancakesEaten += pancakeForEachUser;
        }
        return totalPancakesEaten;
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            System.out.println(simulateRound(i));
        }
    }

    public static void mainConcurrently(String[] args) throws ExecutionException, InterruptedException {
        for (int i = 1; i <= 5; i++) {
            System.out.println(simulateRoundConcurrently(i));
        }
    }


}
