package dk.sdu.scs.common.services;

public interface IWeather {

    String getAll(String address);
    double getTemperature();

    double getFeelsLikeTemperature();

    double getHumidity();

    double getWindSpeed();

    int getWindDirection();

    String getCloudCover();
}
