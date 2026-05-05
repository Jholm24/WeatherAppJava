package dk.sdu.OpenWeather;

import dk.sdu.scs.common.services.IWeather;

public class OpenWeatherAPI implements IWeather {
    @Override
    public void getAll(double lat, double lon) {

    }

    @Override
    public void requestAPI() {

    }

    @Override
    public double getTemperature() {
        return 0;
    }

    @Override
    public double getFeelsLikeTemperature() {
        return 0;
    }

    @Override
    public double getHumidity() {
        return 0;
    }

    @Override
    public double getWindSpeed() {
        return 0;
    }

    @Override
    public int getWindDirection() {
        return 0;
    }

    @Override
    public String getCloudCover() {
        return "";
    }
}
