/*
 * Course: CIS171 - Java Programming
 * Name: Rae Mcivor
 * Project Name: Smart Budget & Expense Tracker
 * File: BudgetApp.java
 * Date: October 5, 2026
 * Description: Command-line user interface controller that handles interactive menus,
 *              validated user input routines, dynamic date display, and routing inputs
 *              to ExpenseCalculator business methods.
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * BudgetApp
 * User interface controller handling menu options, inputs, and console display.
 */
public class BudgetApp {

    // Global array storing up to 5 recent transaction amounts
    private static double[] recentExpenses = new double[5];

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        // Auto-date header using modern Java Time API
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

        while (running) {
            displayMenu(today.format(dateFormatter));
            int choice = getValidInt(input, "Enter choice (1-5): ", 1, 5);

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

    // Print out the main navigation menu matching the project sketch
    private static void displayMenu(String dateString) {
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

    // Option 1: Income Tax Calculator
    private static void handleTaxCalculation(Scanner input) {
        System.out.println("\n--- TAX CALCULATOR ---");
        double gross = getValidDouble(input, "Enter annual gross income: $", 0.0);
        double rate = getValidDouble(input, "Enter tax rate percentage (%): ", 0.0, 100.0);

        double taxOwed = ExpenseCalculator.calculateTax(gross, rate);
        double netIncome = gross - taxOwed;

        System.out.printf("Gross Income : $%,10.2f%n", gross);
        System.out.printf("Tax Owed     : $%,10.2f%n", taxOwed);
        System.out.printf("Net Income   : $%,10.2f%n", netIncome);
    }

    // Option 2: Log Expense with dynamic cashback reward
    private static void handleExpenseLogging(Scanner input) {
        System.out.println("\n--- LOG EXPENSE ---");

        System.out.print("Enter category name: ");
        String rawCategory = input.nextLine();
        String formattedCategory = ExpenseCalculator.formatCategoryName(rawCategory);

        if (formattedCategory.equals("INVALID_CATEGORY")) {
            System.out.println("Error: Invalid category name. Please use only letters and spaces.");
            return;
        }

        double amount = getValidDouble(input, "Enter expense amount: $", 0.01);

        // Add to array history, pushing out old entries if full
        shiftArrayAndAdd(amount);

        double reward = ExpenseCalculator.calculateRandomCashback(amount);

        System.out.printf("Logged expense of $%,.2f under '%s'%n", amount, formattedCategory);
        System.out.printf("🎉 Cashback Reward Earned: $%,.2f%n", reward);
    }

    // Option 3: Compound interest calculator with grid display
    private static void handleSavingsProjection(Scanner input) {
        System.out.println("\n--- SAVINGS PROJECTION CALCULATOR ---");
        double principal = getValidDouble(input, "Enter investment principal: $", 0.01);
        double rate = getValidDouble(input, "Enter expected annual rate (%): ", 0.01);
        int years = getValidInt(input, "Enter investment duration (years): ", 1, 50);

        double totalFutureValue = ExpenseCalculator.projectSavingsGrowth(principal, rate, years);

        System.out.println("\n--- SAVINGS GROWTH GRID (BY YEAR & QUARTER) ---");
        // Nested loop breakdown of compound savings growth over time
        for (int y = 1; y <= years; y++) {
            System.out.println("Year " + y + ":");
            for (int q = 1; q <= 4; q++) {
                // Approximate fractional years (e.g., Q1 = year + 0.25)
                double fractionOfYear = (y - 1) + (q * 0.25);

                // Calculate actual exact value for that quarter fraction using monthly compounding
                double rateAsDecimal = rate / 100.0;
                double quarterValue = principal * Math.pow(1 + (rateAsDecimal / 12), 12 * fractionOfYear);

                System.out.printf("   Q%d: $%,10.2f", q, quarterValue);
            }
            System.out.println();
        }

        System.out.printf("%nProjected Total Value after %d year(s): $%,10.2f%n", years, totalFutureValue);
    }

    // Option 4: View Expense History (Display contents of fixed-size array)
    private static void handleViewExpenses() {
        System.out.println("\n--- RECENT EXPENSE HISTORY (LAST 5 TRANSACTIONS) ---");
        boolean hasData = false;

        for (int i = 0; i < recentExpenses.length; i++) {
            if (recentExpenses[i] > 0) {
                System.out.printf("Transaction %d: $%,10.2f%n", (i + 1), recentExpenses[i]);
                hasData = true;
            }
        }

        if (!hasData) {
            System.out.println("No expenses logged yet.");
        }
    }

    // Helper: Push older expenses left to make room for newest expense at the end
    private static void shiftArrayAndAdd(double newAmount) {
        for (int i = 0; i < recentExpenses.length - 1; i++) {
            recentExpenses[i] = recentExpenses[i + 1];
        }
        recentExpenses[recentExpenses.length - 1] = newAmount;
    }

    // Input Validation Helpers
    private static int getValidInt(Scanner input, String prompt, int min, int max) {
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

    private static double getValidDouble(Scanner input, String prompt, double min) {
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

    private static double getValidDouble(Scanner input, String prompt, double min, double max) {
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
}