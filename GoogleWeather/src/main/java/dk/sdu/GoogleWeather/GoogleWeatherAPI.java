package dk.sdu.GoogleWeather;

import dk.sdu.scs.common.services.IWeather;

public class GoogleWeatherAPI implements IWeather {

    private static final String API_KEY = System.getenv("GOOGLE_WEATHER_KEY");
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    double celcius;
    double feelsLikeCelcius;
    int humidity;
    double windSpeed;
    int windDirection;
    String cloudCover;


    @Override
    public void getAll(double lat, double lon) {

    }

    @Override
    public void requestAPI() {

    }

    @Override
    public double getTemperature() {
        return celcius;
    }

    @Override
    public double getFeelsLikeTemperature() {
        return feelsLikeCelcius;
    }

    @Override
    public double getHumidity() {
        return humidity;
    }

    @Override
    public double getWindSpeed() {
        return windSpeed;
    }

    @Override
    public int getWindDirection() {
        return windDirection;
    }

    @Override
    public String getCloudCover() {
        return cloudCover;
    }
}
