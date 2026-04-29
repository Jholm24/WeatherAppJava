package dk.sdu.scs.common.services;

public interface IWeather {

    void getAll(double lat, double lon);

    void requestAPI();

    double getTemperature();

    double getFeelsLikeTemperature();

    double getHumidity();

    double getWindSpeed();

    int getWindDirection();

    String getCloudCover();
}
