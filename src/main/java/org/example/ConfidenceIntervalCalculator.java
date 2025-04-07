package org.example;
import java.io.*;
import java.util.*;

/**
 * The ConfidenceIntervalCalculator class reads timing data from a CSV file,
 * calculates the mean time it takes to process the events across multiple runs,
 * and computes the 95% confidence interval for these timings.
 */

public class ConfidenceIntervalCalculator {
    public static void main(String[] args) throws IOException {
        // Create a list to store the times read from the CSV file
        List<Double> times = new ArrayList<>();
        // Read the timings from the "timings.csv" file
        try (BufferedReader reader = new BufferedReader(new FileReader("timings.csv"))) {
            String line;
            // Read each line and add the parsed time to the list
            while ((line = reader.readLine()) != null) {
                times.add(Double.parseDouble(line));
            }
        }

        // Get the number of timings n
        int n = times.size();
        // Calculate the mean (average) time
        double mean = times.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        // Calculate the sum of squared differences from the mean for standard deviation
        double sumSq = times.stream().mapToDouble(t -> Math.pow(t - mean, 2)).sum();
        // Calculate the standard deviation using sample standard deviation formula
        double stdDev = Math.sqrt(sumSq / (n - 1));
        // Calculate the 95% confidence interval margin
        double confidence95 = 1.96 * (stdDev / Math.sqrt(n));

        System.out.printf("Mean time: %.2f ms\n", mean);
        System.out.printf("95%% Confidence Interval: [%.2f ms, %.2f ms]\n", mean - confidence95, mean + confidence95);
    }
}
