package dk.sdu.Core.observer;

import java.util.ArrayList;
import java.util.List;

public class WeatherEventPublisher {
    private final List<IWeatherObserver> observers = new ArrayList<>();

    public void subscribe(IWeatherObserver observer) {
        observers.add(observer);
    }

    public void publish(String address, double windSpeed) {
        for (IWeatherObserver observer : observers) {
            observer.onWeatherUpdate(address, windSpeed);
        }
    }
}
