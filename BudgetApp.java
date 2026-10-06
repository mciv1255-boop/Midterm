/*
 * Course: CIS171 - Java Programming
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

    // [Remaining menu options and scanner validation helper methods omitted...]
}
