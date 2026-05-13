package dk.sdu.Core.observer;

public interface IWeatherObserver {
    void onWeatherUpdate(String address, double windSpeed);
}
