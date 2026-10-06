/*
/*
 * Course: CIS171 - Java Programming
 * Project Name: Smart Budget & Expense Tracker
 * File: ExpenseCalculator.java
 * Date: October 5, 2026
 * Description: Contains pure business logic and mathematical calculations for tax computations,
 *              compound savings growth projections, randomized cashback calculations, and
 *              string input cleaning/formatting. Kept free of Scanner/UI logic for JUnit testing.
 */

import java.util.Random;

/**
 * ExpenseCalculator
 * Business logic and helper methods for calculations and string formatting.
 * Keep this class completely free of Scanner and System.out calls so JUnit tests work easily.
 */
public class ExpenseCalculator {

    // Calculates income tax based on annual gross income and tax rate percentage
    // double, double -> double
    public static double calculateTax(double grossIncome, double taxRatePercentage) {
        // Guard against weird inputs like negative income or negative tax rates
        if (grossIncome <= 0 || taxRatePercentage <= 0) {
            return 0.00;
        }
        return grossIncome * (taxRatePercentage / 100.0);
    }

    // Calculates compound growth of principal over time using Math.pow
    // Assumes monthly compounding (n = 12)
    // double, double, int -> double
    public static double projectSavingsGrowth(double principal, double annualRate, int years) {
        if (principal <= 0 || annualRate <= 0 || years <= 0) {
            return 0.00;
        }

        double rateAsDecimal = annualRate / 100.0;
        int compoundingPeriodsPerYear = 12; // Compounded monthly

        // Compound interest formula: A = P * (1 + r/n)^(n*t)
        double amount = principal * Math.pow(1 + (rateAsDecimal / compoundingPeriodsPerYear), 
                                             compoundingPeriodsPerYear * years);
        return amount;
    }

    // Generates a dynamic cash-back reward based on transaction amount
    // double -> double
    public static double calculateRandomCashback(double amountSpent) {
        if (amountSpent <= 0) {
            return 0.00;
        }

        // Random percentage between 1% and 5%
        Random rand = new Random();
        double rewardRatePercentage = 1.0 + (5.0 - 1.0) * rand.nextDouble();
        
        return amountSpent * (rewardRatePercentage / 100.0);
    }

    // Validates, cleans, and formats category name to Title Case using String member functions
    // String -> String
    public static String formatCategoryName(String rawInput) {
        if (rawInput == null) {
            return "INVALID_CATEGORY";
        }

        // Trim off extra leading and trailing whitespace
        String cleaned = rawInput.trim();

        if (cleaned.length() == 0) {
            return "INVALID_CATEGORY";
        }

        // Validate that every character is a letter or space (reject special characters like '#')
        for (int i = 0; i < cleaned.length(); i++) {
            char ch = cleaned.charAt(i);
            if (!Character.isLetter(ch) && !Character.isWhitespace(ch)) {
                return "INVALID_CATEGORY";
            }
        }

        // Convert the string to title case (capitalize first letter, lowercase the rest)
        String firstChar = cleaned.substring(0, 1).toUpperCase();
        String restOfString = cleaned.substring(1).toLowerCase();

        return firstChar + restOfString;
    }

}
