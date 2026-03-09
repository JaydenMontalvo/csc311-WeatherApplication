module farmingdale.com.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens farmingdale.com.weatherapp to javafx.fxml;
    exports farmingdale.com.weatherapp;
}