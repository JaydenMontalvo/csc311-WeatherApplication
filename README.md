# Weather Data Analyzer

A Java app that loads weather data from a CSV file and does analysis on it. Calculates temperatures, counts rainy days, and categorizes weather conditions.

## What It Does

- Loads weather data from CSV file
- Get average temperature by month in degrees Celsius
- Finds days that are hot
- Counts rainy days
- Weather categorization (Hot/Warm/Cool/Cold)
- Generate a report
- Filter data with custom conditions

## How to Use

```java
WeatherDataAnalyzer analyzer = new WeatherDataAnalyzer();

// average temp for August
double avg = analyzer.getAverageTemperatureByMonth(YearMonth.of(2023, 8));

// all days hotter than 30
List<String> hotDays = analyzer.getDaysAboveThreshold(30.0);

// how many rainy days
long rainy = analyzer.getRainyDayCount();

// print everything
analyzer.printAnalysis();
```

## Project Structure

```
weather-data-analyzer/
├── src/main/java/farmingdale/com/weatherapp/
│   ├── WeatherData.java
│   ├── WeatherSummary.java
│   ├── WeatherDataAnalyzer.java
│   └── WeatherDataAnalyzerGUI.java
├── src/main/resources/farmingdale/com/weatherapp/
│   └── weatherdata.csv
├── pom.xml
└── README.md
```

## Main Classes

### WeatherDataAnalyzer

The main class. Loads data and does all the analysis.

```java
WeatherDataAnalyzer()                                    // loads CSV from resources
double getAverageTemperatureByMonth(YearMonth ym)       // avg temp for month
List<String> getDaysAboveThreshold(double temp)         // days hotter than X
long getRainyDayCount()                                 // total rainy days
long getRainyDaysInMonth(YearMonth ym)                  // rainy days in month
static String getWeatherCategory(double temp)           // returns Hot/Warm/Cool/Cold
String generateAnalysisReport()                         // returns formatted report
void printAnalysis()                                    // prints to console
List<WeatherData> getAllWeatherData()                   // all the data
WeatherSummary getSummaryStatistics()                   // gets stats
double getAverageHumidityByMonth(YearMonth ym)          // avg humidity
double getTotalPrecipitationByMonth(YearMonth ym)       // total rain
List<WeatherData> filterData(Predicate<WeatherData> p)  // custom filter
```

### WeatherData

A record that holds one day of weather data.

```java
public record WeatherData(
    LocalDate date,
    double temperature,
    double humidity,
    double precipitation
)
```

Has methods like `isRainy()`, `isHot()`, `isCold()`.

### WeatherSummary

A record that holds summary stats.

```java
public record WeatherSummary(
    int totalDays,
    double averageTemperature,
    double maxTemperature,
    double minTemperature,
    long rainyDays
)
```

## CSV Format

Your CSV file needs to look like this:

```
Date,Temperature,Humidity,Precipitation
2023-08-01,32.5,65,0.0
2023-08-02,35.0,60,0.2
2023-08-03,28.3,72,1.5
```

Temperature can be -50 to 60°C, humidity 0-100%, precipitation is mm.

## Example

```java
public class Main {
    public static void main(String[] args) {
        try {
            var analyzer = new WeatherDataAnalyzer();
            
            // print full report
            analyzer.printAnalysis();
            
            // get august stats
            var aug = YearMonth.of(2023, 8);
            System.out.println("Avg: " + analyzer.getAverageTemperatureByMonth(aug));
            System.out.println("Rainy: " + analyzer.getRainyDaysInMonth(aug));
            
            // find hot days
            analyzer.getDaysAboveThreshold(30).forEach(System.out::println);
            
            // get all stats
            var summary = analyzer.getSummaryStatistics();
            System.out.println(summary);
            
        } catch (IOException e) {
            System.err.println("Oops: " + e.getMessage());
        }
    }
}
```

## Common Commands

```bash
mvn clean compile          # compile it
mvn test                   # run tests
mvn package                # build jar
mvn exec:java -Dexec.mainClass="..."  # run main
```

## Issues

**CSV/css not found?**
Make sure your files are in `src/main/resources/farmingdale/com/weatherapp/` 

**Data validation error?**
Check your CSV values are in valid ranges.

##Java Stuff Used

- Records - the data classes
- Switch - the getWeatherCategory() method
- Text Blocks - the report formatting
- Streams - filtering and processing data
