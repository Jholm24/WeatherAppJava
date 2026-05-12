package dk.sdu.scs.common.services;

public interface IWeather {

    String getName();

    String getAll(String address);
    double getTemperature();

    double getFeelsLikeTemperature();

    double getHumidity();

    double getWindSpeed();

    String getWindDirection();

    String getCloudCover();
}
