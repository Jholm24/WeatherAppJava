package dk.sdu.OpenWeather;

import dk.sdu.scs.common.services.IWeather;
import dk.sdu.scs.common.services.IGeoLocation;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ServiceLoader;

public class OpenWeatherAPI implements IWeather {

    private final IGeoLocation geoLocation;
    private static final String excludeData = "exclude=minutely,hourly,daily,alerts";
    private double latitude;
    private double longitude;

    // data fields
    private double temp;
    private double feelsLike;
    private int humidity;
    private int pressure;
    private int windDeg; // methods need to take degrees and converts to wind direction.
    private int cloudCover;

    private double windSpeed;

    private static final String API_Key = System.getenv("OPENWEATHER_KEY");
    private static final String  baseUrl = "https://api.openweathermap.org/data/3.0/onecall";

    // Constructor for serviceLoader
    public OpenWeatherAPI()
    {
        this.geoLocation = ServiceLoader.load(IGeoLocation.class).findFirst().orElseThrow(()->new IllegalStateException("No geolocation found"));
    }
    // Constructor for mock testing.
    public OpenWeatherAPI(IGeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }


    @Override
    public String getAll(String address) {
        geoLocation.getAll(address);
        this.latitude = geoLocation.getLatitude();
        this.longitude = geoLocation.getLongitude();
        String requestURL = baseUrl +"?lat=" + latitude + "&lon=" + longitude + "&" + excludeData + "&appid=" + API_Key;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestURL))
                    .GET()
                    .build();
            String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("API SVAR: " + json);
            return json;
        }
        catch (Exception e)
            {
            e.printStackTrace();
            return null;
            }
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
