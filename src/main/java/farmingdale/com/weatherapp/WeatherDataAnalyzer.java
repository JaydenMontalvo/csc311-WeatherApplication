package farmingdale.com.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WeatherDataAnalyzer
 * Loads weather data from a CSV located in the resources package
 * and performs various analyses using modern Java features.
 */
public class WeatherDataAnalyzer {

    private final List<WeatherData> weatherDataList;

    public static void main(String[] args) {
        try {
            var analyzer = new WeatherDataAnalyzer();
            analyzer.printAnalysis();
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    /**
     * Constructor that loads the CSV from resources.
     */
    public WeatherDataAnalyzer() throws IOException {
        this.weatherDataList = loadWeatherData();
    }

    /**
     * Loads CSV from resources folder.
     */
    private List<WeatherData> loadWeatherData() throws IOException {

        var inputStream = getClass().getResourceAsStream(
                "/farmingdale/com/weatherapp/weatherdata.csv"
        );

        if (inputStream == null) {
            throw new IOException("weatherdata.csv not found in resources.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            return reader.lines()
                    .skip(1)
                    .map(WeatherData::parse)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }

    public double getAverageTemperatureByMonth(YearMonth yearMonth) {
        return weatherDataList.stream()
                .filter(data -> YearMonth.from(data.date()).equals(yearMonth))
                .mapToDouble(WeatherData::temperature)
                .average()
                .orElse(0.0);
    }

    public List<String> getDaysAboveThreshold(double threshold) {
        return weatherDataList.stream()
                .filter(data -> data.temperature() > threshold)
                .map(data -> String.format("%s: %.1f°C", data.date(), data.temperature()))
                .collect(Collectors.toList());
    }

    public long getRainyDayCount() {
        return weatherDataList.stream()
                .filter(data -> data.precipitation() > 0)
                .count();
    }

    public long getRainyDaysInMonth(YearMonth yearMonth) {
        return weatherDataList.stream()
                .filter(data -> YearMonth.from(data.date()).equals(yearMonth))
                .filter(data -> data.precipitation() > 0)
                .count();
    }

    public static String getWeatherCategory(double temperature) {
        int t = (int) temperature;

        return switch (t) {
            case 31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50 -> "Hot";
            case 20,21,22,23,24,25,26,27,28,29,30 -> "Warm";
            case 10,11,12,13,14,15,16,17,18,19 -> "Cool";
            default -> "Cold";
        };
    }

    public String generateAnalysisReport() {

        Map<YearMonth, Double> monthlyAverages = weatherDataList.stream()
                .collect(Collectors.groupingBy(
                        data -> YearMonth.from(data.date()),
                        Collectors.averagingDouble(WeatherData::temperature)
                ));

        var report = new StringBuilder();

        report.append("""
                
                ========================================
                WEATHER DATA ANALYSIS REPORT
                ========================================
                """);

        report.append("\nMonthly Average Temperatures:\n");

        monthlyAverages.forEach((month, avgTemp) ->
                report.append(String.format(
                        "  %s: %.2f°C (%s)%n",
                        month,
                        avgTemp,
                        getWeatherCategory(avgTemp)
                ))
        );

        long totalRainyDays = getRainyDayCount();
        report.append(String.format("%nTotal Rainy Days: %d%n", totalRainyDays));

        long totalDays = weatherDataList.size();
        report.append(String.format("Total Days: %d%n", totalDays));

        double avgTemp = weatherDataList.stream()
                .mapToDouble(WeatherData::temperature)
                .average()
                .orElse(0.0);

        report.append(String.format("Overall Average Temperature: %.2f°C%n", avgTemp));

        return report.toString();
    }

    public void printAnalysis() {

        System.out.println(generateAnalysisReport());

        System.out.println("\nDays Above 25°C Threshold:");

        getDaysAboveThreshold(25.0)
                .forEach(day -> System.out.println("  " + day));

        System.out.println("\nAnalysis Complete!");
    }

    public List<WeatherData> getAllWeatherData() {
        return Collections.unmodifiableList(weatherDataList);
    }

    public WeatherSummary getSummaryStatistics() {

        if (weatherDataList.isEmpty()) {
            return new WeatherSummary(0,0,0,0,0);
        }

        double avgTemp = weatherDataList.stream()
                .mapToDouble(WeatherData::temperature)
                .average()
                .orElse(0.0);

        double maxTemp = weatherDataList.stream()
                .mapToDouble(WeatherData::temperature)
                .max()
                .orElse(0.0);

        double minTemp = weatherDataList.stream()
                .mapToDouble(WeatherData::temperature)
                .min()
                .orElse(0.0);

        long rainyDays = getRainyDayCount();

        return new WeatherSummary(
                weatherDataList.size(),
                avgTemp,
                maxTemp,
                minTemp,
                rainyDays
        );
    }

    public double getAverageHumidityByMonth(YearMonth yearMonth) {
        return weatherDataList.stream()
                .filter(data -> YearMonth.from(data.date()).equals(yearMonth))
                .mapToDouble(WeatherData::humidity)
                .average()
                .orElse(0.0);
    }

    public double getTotalPrecipitationByMonth(YearMonth yearMonth) {
        return weatherDataList.stream()
                .filter(data -> YearMonth.from(data.date()).equals(yearMonth))
                .mapToDouble(WeatherData::precipitation)
                .sum();
    }

    public List<WeatherData> filterData(java.util.function.Predicate<WeatherData> predicate) {
        return weatherDataList.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}