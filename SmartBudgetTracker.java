/*
 * Course: CIS171 - Java Programming
 * Project Name: Smart Budget & Expense Tracker
 * File: SmartBudgetTracker.java
 * Date: September 28, 2026 - October 12, 2026 (started on this file the Oct 6th)
 * Description: My CIS171 midterm project all in one file! Combines the console menu UI with all ,
 * the math and business logic for taxes, compound savings,and random cashback rewards.
 * I also cleaned up magic numbers by using constants and made my documentation more consistent.
 */

import java.time.LocalDate; //<- from my previous class
import java.time.format.DateTimeFormatter; //<- got the idea from my SQL project
import java.util.Random;
import java.util.Scanner;

public class SmartBudgetTracker {

    // Constants to eliminate magic numbers
    private static final int EXPENSE_HISTORY_LIMIT = 5;
    private static final int MENU_MIN = 1;
    private static final int MENU_MAX = 5;

    private static final double MIN_TAX_RATE = 0.0;
    private static final double MAX_TAX_RATE = 100.0;

    private static final double MIN_CASHBACK_RATE = 1.0;
    private static final double MAX_CASHBACK_RATE = 5.0;

    private static final int COMPOUNDING_PERIODS_PER_YEAR = 12;
    private static final int QUARTERS_PER_YEAR = 4;
    private static final double QUARTER_FRACTION = 0.25;

    private static final double MIN_VALID_AMOUNT = 0.01;
    private static final double ZERO_AMOUNT = 0.00;
    private static final int MAX_SAVINGS_YEARS = 50;

    // Global array stores up to 5 recent transaction amounts
    private static double[] recentExpenses = new double[EXPENSE_HISTORY_LIMIT];

    // Executes the command-line interface loop for the financial utility application
    // String[] -> void
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        // Auto-date header using modern Java Time
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

        while (running) {
            displayMenu(today.format(dateFormatter));
            int choice = getValidInt(input, "Enter choice (" + MENU_MIN + "-" + MENU_MAX + "): ", MENU_MIN, MENU_MAX);

            switch (choice) {
                case 1:
                    handleTaxCalculation(input);
                    break;
                case 2:
                    handleExpenseLogging(input);
                    break;
                case 3:
                    handleSavingsProjection(input);
                    break;
                case 4:
                    handleViewExpenses();
                    break;
                case 5:
                    System.out.println("\nThank you for using Smart Budget & Expense Tracker. Goodbye!");
                    running = false;
                    break;
            }
            System.out.println();
        }

        input.close();
    }

    // Prints out the main navigation menu matching the project sketch
    // String -> void
    public static void displayMenu(String dateString) {
        System.out.println("==================================================");
        System.out.println("WELCOME TO SMART BUDGET & EXPENSE TRACKER");
        System.out.println("Date: " + dateString);
        System.out.println("==================================================");
        System.out.println("--------------------------------------------------");
        System.out.println("1. Calculate Tax & Net Income");
        System.out.println("2. Log Expense (With Cashback Generator)");
        System.out.println("3. Calculate Savings Projection (Math Compound Interest)");
        System.out.println("4. View Expense History (Array Summary)");
        System.out.println("5. Exit");
        System.out.println("--------------------------------------------------");
    }

    // Handles user prompts and displays tax and net income calculations
    // Scanner -> void
    public static void handleTaxCalculation(Scanner input) {
        System.out.println("\n--- TAX CALCULATOR ---");
        double gross = getValidDouble(input, "Enter annual gross income: $", ZERO_AMOUNT);
        double rate = getValidDouble(input, "Enter tax rate percentage (%): ", MIN_TAX_RATE, MAX_TAX_RATE);

        double taxOwed = calculateTax(gross, rate);
        double netIncome = gross - taxOwed;

        System.out.printf("Gross Income : $%,10.2f%n", gross);
        System.out.printf("Tax Owed     : $%,10.2f%n", taxOwed);
        System.out.printf("Net Income   : $%,10.2f%n", netIncome);
    }

    // Handles expense entry, category formatting and random cashback generation
    // Scanner -> void
    public static void handleExpenseLogging(Scanner input) {
        System.out.println("\n--- LOG EXPENSE ---");

        System.out.print("Enter category name: ");
        String rawCategory = input.nextLine();
        String formattedCategory = formatCategoryName(rawCategory);

        if (formattedCategory.equals("INVALID_CATEGORY")) {
            System.out.println("Error: Invalid category name. Please use only letters and spaces.");
            return;
        }

        double amount = getValidDouble(input, "Enter expense amount: $", MIN_VALID_AMOUNT);

        // Adds to the array history & pushes out old entries if/when full
        shiftArrayAndAdd(amount);

        double reward = calculateRandomCashback(amount);

        System.out.printf("Logged expense of $%,.2f under '%s'%n", amount, formattedCategory);
        System.out.printf("🎉 Cashback Reward Earned: $%,.2f%n", reward);
    }

    // Handles compound interest savings calculation and quarter by quarter grid display
    // Scanner -> void
    public static void handleSavingsProjection(Scanner input) {
        System.out.println("\n--- SAVINGS PROJECTION CALCULATOR ---");
        double principal = getValidDouble(input, "Enter investment principal: $", MIN_VALID_AMOUNT);
        double rate = getValidDouble(input, "Enter expected annual rate (%): ", MIN_VALID_AMOUNT);
        int years = getValidInt(input, "Enter investment duration (years): ", 1, MAX_SAVINGS_YEARS);

        double totalFutureValue = projectSavingsGrowth(principal, rate, years);

        System.out.println("\n--- SAVINGS GROWTH GRID (BY YEAR & QUARTER) ---");
        for (int y = 1; y <= years; y++) {
            System.out.println("Year " + y + ":");
            for (int q = 1; q <= QUARTERS_PER_YEAR; q++) {
                double fractionOfYear = (y - 1) + (q * QUARTER_FRACTION);
                double rateAsDecimal = rate / 100.0;
                double quarterValue = principal * Math.pow(1 + (rateAsDecimal / COMPOUNDING_PERIODS_PER_YEAR),
                        COMPOUNDING_PERIODS_PER_YEAR * fractionOfYear);

                System.out.printf("   Q%d: $%,10.2f", q, quarterValue);
            }
            System.out.println();
        }

        System.out.printf("%nProjected Total Value after %d year(s): $%,10.2f%n", years, totalFutureValue);
    }

    // Displays recent transaction history stored in the expense array
    // void -> void
    public static void handleViewExpenses() {
        System.out.println("\n--- RECENT EXPENSE HISTORY (LAST " + EXPENSE_HISTORY_LIMIT + " TRANSACTIONS) ---");
        boolean hasData = false;

        for (int i = 0; i < recentExpenses.length; i++) {
            if (recentExpenses[i] > ZERO_AMOUNT) {
                System.out.printf("Transaction %d: $%,10.2f%n", (i + 1), recentExpenses[i]);
                hasData = true;
            }
        }

        if (!hasData) {
            System.out.println("No expenses logged yet.");
        }
    }

    // Shifts older transaction amounts left and adds the newest amount to the end
    // double -> void
    public static void shiftArrayAndAdd(double newAmount) {
        for (int i = 0; i < recentExpenses.length - 1; i++) {
            recentExpenses[i] = recentExpenses[i + 1];
        }
        recentExpenses[recentExpenses.length - 1] = newAmount;
    }

    // Validates integer inputs within a specified range using a scanner
    // scanner, String, int, int -> int
    public static int getValidInt(Scanner input, String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            if (input.hasNextInt()) {
                value = input.nextInt();
                input.nextLine(); // Clear newline
                if (value >= min && value <= max) {
                    return value;
                }
            } else {
                input.nextLine(); // Clear bad input
            }
            System.out.println("Invalid entry. Please enter a whole number between " + min + " and " + max + ".");
        }
    }

    // Validates double inputs with a minimum constraint using a Scanner
    // Scanner, String, double -> double
    public static double getValidDouble(Scanner input, String prompt, double min) {
        double value;
        while (true) {
            System.out.print(prompt);
            if (input.hasNextDouble()) {
                value = input.nextDouble();
                input.nextLine(); // Clear newline
                if (value >= min) {
                    return value;
                }
            } else {
                input.nextLine(); // Clear bad input
            }
            System.out.println("Invalid entry. Please enter a positive number.");
        }
    }

    // Validates double inputs within a specified minimum and maximum range using a Scanner
    // Scanner, String, double, double -> double
    public static double getValidDouble(Scanner input, String prompt, double min, double max) {
        double value;
        while (true) {
            System.out.print(prompt);
            if (input.hasNextDouble()) {
                value = input.nextDouble();
                input.nextLine(); // Clear newline
                if (value >= min && value <= max) {
                    return value;
                }
            } else {
                input.nextLine(); // Clear bad input
            }
            System.out.println("Invalid entry. Please enter a value between " + min + " and " + max + ".");
        }
    }

    // =========================================================================
    // BUSINESS LOGIC & MATH METHODS
    // =========================================================================

    // Calculates income tax based on annual gross income and tax rate percentage
    // double, double -> double
    public static double calculateTax(double grossIncome, double taxRatePercentage) {
        if (grossIncome <= ZERO_AMOUNT || taxRatePercentage <= ZERO_AMOUNT) {
            return ZERO_AMOUNT;
        }
        return grossIncome * (taxRatePercentage / 100.0);
    }

    // Calculates compound growth of principal over time using Math.pow
    // double, double, int -> double
    public static double projectSavingsGrowth(double principal, double annualRate, int years) {
        if (principal <= ZERO_AMOUNT || annualRate <= ZERO_AMOUNT || years <= 0) {
            return ZERO_AMOUNT;
        }

        double rateAsDecimal = annualRate / 100.0;

        double amount = principal * Math.pow(1 + (rateAsDecimal / COMPOUNDING_PERIODS_PER_YEAR),
                COMPOUNDING_PERIODS_PER_YEAR * years);
        return amount;
    }

    // Generates a dynamic cash-back reward based on transaction amount
    // double -> double
    public static double calculateRandomCashback(double amountSpent) {
        if (amountSpent <= ZERO_AMOUNT) {
            return ZERO_AMOUNT;
        }

        Random rand = new Random();
        double rewardRatePercentage = MIN_CASHBACK_RATE + (MAX_CASHBACK_RATE - MIN_CASHBACK_RATE) * rand.nextDouble();

        return amountSpent * (rewardRatePercentage / 100.0);
    }

    // Validates, cleans, and formats category name to Title Case using String member functions
    // String -> String
    public static String formatCategoryName(String rawInput) {
        if (rawInput == null) {
            return "INVALID_CATEGORY";
        }

        String cleaned = rawInput.trim();

        if (cleaned.length() == 0) {
            return "INVALID_CATEGORY";
        }

        for (int i = 0; i < cleaned.length(); i++) {
            char ch = cleaned.charAt(i);
            if (!Character.isLetter(ch) && !Character.isWhitespace(ch)) {
                return "INVALID_CATEGORY";
            }
        }

        String firstChar = cleaned.substring(0, 1).toUpperCase();
        String restOfString = cleaned.substring(1).toLowerCase();

        return firstChar + restOfString;
    }

    // Calculates remaining budget balance after subtracting array of recent expenses
    // double, double[] -> double
    public static double calculateRemainingBudget(double budget, double[] expenses) {
        double totalSpent = ZERO_AMOUNT;

        if (expenses != null) {
            for (double expense : expenses) {
                totalSpent += expense;
            }
        }

        return budget - totalSpent;
    }

    // Validates if a spending amount is non-negative and within budget limit
    // double, double -> boolean
    public static boolean isWithinBudget(double spent, double limit) {
        return spent >= ZERO_AMOUNT && spent <= limit;
    }
}
