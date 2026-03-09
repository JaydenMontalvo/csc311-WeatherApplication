package farmingdale.com.weatherapp;

import java.time.LocalDate;
import java.util.Optional;

/**
 * # WeatherData Record
 *
 * An immutable data class representing weather information for a single day.
 *
 * ## Introduction (Java 16+)
 * Records are a concise way to define immutable data carriers. They automatically
 * generate `equals()`, `hashCode()`, `toString()`, and a canonical constructor.
 *
 * ## Record Components
 * - `date`: The measurement date
 * - `temperature`: Temperature in Celsius
 * - `humidity`: Relative humidity as a percentage (0-100)
 * - `precipitation`: Rainfall in millimeters
 *
 * ## Example
 * ```java
 * WeatherData data = new WeatherData(
 *     LocalDate.of(2023, 8, 1),
 *     32.5,
 *     65.0,
 *     0.0
 * );
 * ```
 *
 * @param date The date of the weather observation
 * @param temperature The temperature in Celsius
 * @param humidity The humidity percentage
 * @param precipitation The precipitation in millimeters
 *
 * @author Weather Data Team
 * @version 2.0
 * @since Java 16
 */
public record WeatherData(
        LocalDate date,
        double temperature,
        double humidity,
        double precipitation
) {

    /**
     * Compact constructor for validation.
     *
     * Records in Java 16+ support compact constructors that validate
     * component values before instance creation.
     *
     * @throws IllegalArgumentException if values are out of valid ranges
     */
    public WeatherData {
        if (temperature < -50 || temperature > 60) {
            throw new IllegalArgumentException(
                    "Temperature must be between -50 and 60 Celsius"
            );
        }
        if (humidity < 0 || humidity > 100) {
            throw new IllegalArgumentException(
                    "Humidity must be between 0 and 100 percent"
            );
        }
        if (precipitation < 0) {
            throw new IllegalArgumentException(
                    "Precipitation cannot be negative"
            );
        }
    }

    /**
     * Parses a CSV line into a `WeatherData` record.
     *
     * Expected CSV format:
     * ```
     * Date,Temperature,Humidity,Precipitation
     * 2023-08-01,32.5,65,0.0
     * ```
     *
     * Uses Java 17+ pattern matching to validate and extract values.
     *
     * ## Example
     * ```java
     * Optional<WeatherData> data = WeatherData.parse(
     *     "2023-08-01,32.5,65,0.0"
     * );
     * ```
     *
     * @param csvLine the CSV line to parse
     * @return an `Optional` containing the parsed `WeatherData`, or empty if parsing fails
     */
    public static Optional<WeatherData> parse(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length != 4) {
                return Optional.empty();
            }

            LocalDate date = LocalDate.parse(parts[0].trim());
            double temperature = Double.parseDouble(parts[1].trim());
            double humidity = Double.parseDouble(parts[2].trim());
            double precipitation = Double.parseDouble(parts[3].trim());

            return Optional.of(new WeatherData(date, temperature, humidity, precipitation));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Determines if this day is rainy.
     *
     * A rainy day is defined as any day with precipitation > 0.
     *
     * @return true if it rained, false otherwise
     */
    public boolean isRainy() {
        return precipitation > 0;
    }

    /**
     * Determines if this day is hot.
     *
     * A hot day is defined as temperature > 30°C.
     *
     * @return true if the day is hot, false otherwise
     */
    public boolean isHot() {
        return temperature > 30;
    }

    /**
     * Determines if this day is cold.
     *
     * A cold day is defined as temperature < 10°C.
     *
     * @return true if the day is cold, false otherwise
     */
    public boolean isCold() {
        return temperature < 10;
    }

    /**
     * Provides a formatted string representation of the weather data.
     *
     * Uses pattern matching (Java 17+) to format the output based on weather conditions.
     *
     * @return a human-readable weather summary
     */
    @Override
    public String toString() {
        return String.format(
                "%s | Temp: %.1f°C | Humidity: %.0f%% | Precipitation: %.1f mm",
                date,
                temperature,
                humidity,
                precipitation
        );
    }
}
