package farmingdale.com.weatherapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modern JavaFX Weather Data Analyzer GUI
 */
public class WeatherDataAnalyzerGUI extends Application {

    private WeatherDataAnalyzer analyzer;
    private Label summaryLabel;
    private ListView<String> daysListView;
    private ComboBox<String> monthComboBox;
    private LineChart<String, Number> temperatureChart;
    private Spinner<Double> thresholdSpinner;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        analyzer = new WeatherDataAnalyzer(); // Load CSV inside

        BorderPane root = createMainLayout();
        Scene scene = new Scene(root, 800, 500);
        loadCSS(scene);

        primaryStage.setTitle("Weather Data Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        updateAnalysis();
    }

    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        root.setId("root");
        root.setTop(createHeader());
        root.setLeft(createSidebar());
        root.setCenter(createCenterContent());
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20));
        header.setSpacing(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Weather Data Analyzer");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        VBox titleBox = new VBox(5, title);
        header.getChildren().add(titleBox);

        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(20));
        sidebar.setSpacing(15);
        sidebar.setPrefWidth(300);

        Label controlsTitle = new Label("Controls");
        controlsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        controlsTitle.setTextFill(Color.WHITE);

        Label monthLabel = new Label("Select Month:");
        monthLabel.setFont(Font.font("Segoe UI", 12));
        monthLabel.setTextFill(Color.LIGHTGRAY);

        monthComboBox = new ComboBox<>();
        populateMonthCombo();
        monthComboBox.setOnAction(e -> updateAnalysis());

        Label thresholdLabel = new Label("Temperature Threshold (°C):");
        thresholdLabel.setFont(Font.font("Segoe UI", 12));
        thresholdLabel.setTextFill(Color.LIGHTGRAY);

        thresholdSpinner = new Spinner<>(0.0, 50.0, 25.0, 1.0);
        thresholdSpinner.setEditable(true);
        thresholdSpinner.setPrefWidth(Double.MAX_VALUE);

        Button analyzeButton = new Button("Analyze");
        analyzeButton.setPrefWidth(Double.MAX_VALUE);
        analyzeButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        analyzeButton.setId("analyze-button");
        analyzeButton.setOnAction(e -> updateAnalysis());

        Separator separator = new Separator();

        Label statsTitle = new Label("Statistics");
        statsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        statsTitle.setTextFill(Color.WHITE);

        summaryLabel = new Label();
        summaryLabel.setFont(Font.font("Monospace", 11));
        summaryLabel.setTextFill(Color.LIGHTGRAY);
        summaryLabel.setWrapText(true);
        summaryLabel.setPadding(new Insets(10));
        summaryLabel.setStyle("-fx-border-color: #444444; -fx-border-radius: 5; -fx-background-color: #1e1e1e;");

        sidebar.getChildren().addAll(
                controlsTitle,
                new Separator(),
                monthLabel,
                monthComboBox,
                thresholdLabel,
                thresholdSpinner,
                analyzeButton,
                separator,
                statsTitle,
                summaryLabel
        );
        VBox.setVgrow(summaryLabel, Priority.ALWAYS);

        return sidebar;
    }

    private void populateMonthCombo() {
        String[] monthNames = {
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        };
        monthComboBox.getItems().addAll(monthNames);
        monthComboBox.setValue("January");
    }

    private VBox createCenterContent() {
        VBox centerBox = new VBox();
        centerBox.setPadding(new Insets(15));

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab chartTab = new Tab("Temperature Trend", createChartTab());
        Tab daysTab = new Tab("Days Above Threshold", createDaysTab());
        Tab dataTab = new Tab("Raw Data", createDataTab());

        tabPane.getTabs().addAll(chartTab, daysTab, dataTab);

        centerBox.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        return centerBox;
    }

    private VBox createChartTab() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperature (°C)");

        temperatureChart = new LineChart<>(xAxis, yAxis);
        temperatureChart.setTitle("Temperature Trend");
        temperatureChart.setCreateSymbols(false);

        VBox chartBox = new VBox(temperatureChart);
        chartBox.setPadding(new Insets(10));
        VBox.setVgrow(temperatureChart, Priority.ALWAYS);

        return chartBox;
    }

    private VBox createDaysTab() {
        daysListView = new ListView<>();
        daysListView.setStyle("-fx-control-inner-background: #2a2a2a; -fx-text-fill: #e0e0e0;");
        VBox daysBox = new VBox(daysListView);
        daysBox.setPadding(new Insets(15));
        VBox.setVgrow(daysListView, Priority.ALWAYS);
        return daysBox;
    }

    private VBox createDataTab() {
        TableView<WeatherData> table = new TableView<>();
        table.setId("data-table");
        table.setStyle("-fx-control-inner-background: #2a2a2a; -fx-text-fill: #e0e0e0;");

        // Date column (formatted as YYYY-MM-DD)
        TableColumn<WeatherData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().date().toString())
        );

        // Temperature column
        TableColumn<WeatherData, Double> tempCol = new TableColumn<>("Temperature (°C)");
        tempCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleDoubleProperty(cd.getValue().temperature()).asObject()
        );

        // Humidity column
        TableColumn<WeatherData, Double> humidityCol = new TableColumn<>("Humidity (%)");
        humidityCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleDoubleProperty(cd.getValue().humidity()).asObject()
        );

        // Precipitation column
        TableColumn<WeatherData, Double> precipCol = new TableColumn<>("Precipitation (mm)");
        precipCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleDoubleProperty(cd.getValue().precipitation()).asObject()
        );

        table.getColumns().addAll(dateCol, tempCol, humidityCol, precipCol);
        table.setItems(javafx.collections.FXCollections.observableArrayList(analyzer.getAllWeatherData()));

        VBox dataBox = new VBox(table);
        dataBox.setPadding(new Insets(15));
        VBox.setVgrow(table, Priority.ALWAYS);
        return dataBox;
    }



    /** Updates chart, summary, and days list based on selected month and threshold */
    private void updateAnalysis() {
        String selectedMonthName = monthComboBox.getValue();
        int monthNumber = monthComboBox.getItems().indexOf(selectedMonthName) + 1;

        YearMonth ym = YearMonth.of(2023, monthNumber);
        double threshold = thresholdSpinner.getValue();

        // Update chart
        temperatureChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature");

        List<WeatherData> filtered = analyzer.getAllWeatherData().stream()
                .filter(data -> data.date().getMonthValue() == monthNumber)
                .collect(Collectors.toList());

        for (WeatherData data : filtered) {
            series.getData().add(new XYChart.Data<>(data.date().toString(), data.temperature()));
        }
        temperatureChart.getData().add(series);

        // Update days above threshold
        List<String> daysAboveThreshold = analyzer.getAllWeatherData().stream()
                .filter(data -> data.temperature() > threshold)
                .map(data -> String.format("%s: %.1f°C", data.date(), data.temperature()))
                .collect(Collectors.toList());
        daysListView.getItems().setAll(daysAboveThreshold);

        // Update summary
        double avgTemp = analyzer.getAverageTemperatureByMonth(ym);
        long rainyDays = analyzer.getRainyDaysInMonth(ym);
        String stats = String.format("""
                Month: %s
                Avg Temp: %.2f°C
                Rainy Days: %d
                Category: %s
                """,
                selectedMonthName,
                avgTemp,
                rainyDays,
                WeatherDataAnalyzer.getWeatherCategory(avgTemp)
        );
        summaryLabel.setText(stats);
    }

    private void loadCSS(Scene scene) {
        scene.getStylesheets().add(
                getClass().getResource("/farmingdale/com/weatherapp/style.css").toExternalForm()
        );
    }
}