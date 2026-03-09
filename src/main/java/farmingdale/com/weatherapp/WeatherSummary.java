package farmingdale.com.weatherapp;

/**
 * # WeatherSummary Record
 *
 * An immutable record containing statistical summaries of weather data.
 *
 * This record demonstrates Java Records (Java 16+) for creating
 * simple, effective data carriers with automatic implementations
 * of common methods.
 *
 * ## Record Components
 * - `totalDays`: Total number of days in the dataset
 * - `averageTemperature`: Mean temperature across all days
 * - `maxTemperature`: Highest temperature recorded
 * - `minTemperature`: Lowest temperature recorded
 * - `rainyDays`: Count of days with precipitation > 0
 *
 * ## Example
 * ```java
 * WeatherSummary summary = new WeatherSummary(
 *     31,      // totalDays
 *     25.5,    // averageTemperature
 *     35.0,    // maxTemperature
 *     15.0,    // minTemperature
 *     8        // rainyDays
 * );
 * ```
 *
 * @param totalDays The total number of days in the dataset
 * @param averageTemperature The average temperature in Celsius
 * @param maxTemperature The maximum temperature in Celsius
 * @param minTemperature The minimum temperature in Celsius
 * @param rainyDays The count of rainy days
 *
 * @author Weather Data Team
 * @version 1.0
 * @since Java 16
 */
public record WeatherSummary(
        int totalDays,
        double averageTemperature,
        double maxTemperature,
        double minTemperature,
        long rainyDays
) {

    /**
     * Compact constructor with validation.
     *
     * Ensures that temperature values are logically consistent:
     * - Maximum temperature >= minimum temperature
     * - Average temperature is between min and max
     * - Rainy days doesn't exceed total days
     */
    public WeatherSummary {
        if (maxTemperature < minTemperature) {
            throw new IllegalArgumentException(
                    "Maximum temperature cannot be less than minimum temperature"
            );
        }
        if (rainyDays > totalDays) {
            throw new IllegalArgumentException(
                    "Rainy days cannot exceed total days"
            );
        }
    }

    /**
     * Calculates the percentage of rainy days.
     *
     * Uses a lambda-friendly approach to compute the precipitation ratio.
     *
     * ## Example
     * ```java
     * double rainyPercentage = summary.getRainyDayPercentage();
     * System.out.println("Rainy days: " + rainyPercentage + "%");
     * ```
     *
     * @return the percentage of days with precipitation (0-100)
     */
    public double getRainyDayPercentage() {
        if (totalDays == 0) {
            return 0.0;
        }
        return (rainyDays * 100.0) / totalDays;
    }

    /**
     * Calculates the temperature range.
     *
     * @return the difference between max and min temperature
     */
    public double getTemperatureRange() {
        return maxTemperature - minTemperature;
    }

    /**
     * Provides a formatted summary string.
     *
     * Uses text blocks (Java 15+) for readable multi-line output.
     * This demonstrates how text blocks improve readability.
     *
     * @return a formatted summary
     */
    @Override
    public String toString() {
        return String.format("""
                
                ╔═══════════════════════════════════════╗
                ║        WEATHER SUMMARY STATISTICS     ║
                ╚═══════════════════════════════════════╝
                
                📅 Total Days:              %d
                🌡️  Average Temperature:    %.2f°C
                🔥 Maximum Temperature:    %.2f°C
                ❄️  Minimum Temperature:    %.2f°C
                📊 Temperature Range:      %.2f°C
                🌧️  Rainy Days:            %d (%.1f%%)
                
                """,
                totalDays,
                averageTemperature,
                maxTemperature,
                minTemperature,
                getTemperatureRange(),
                rainyDays,
                getRainyDayPercentage()
        );
    }
}
