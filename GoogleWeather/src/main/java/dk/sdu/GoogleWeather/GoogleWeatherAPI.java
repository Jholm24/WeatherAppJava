package dk.sdu.GoogleWeather;

import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ServiceLoader;

public class GoogleWeatherAPI implements IWeather {

    private final IGeoLocation geoLocation;


    private double celcius;
    private double feelsLikeCelcius;
    private int humidity;
    private double windSpeed;
    private double windDirection;
    private int cloudCover;


    private static final String API_Key = System.getenv("GOOGLE_KEY");
    private static final String BASE_URL = "https://weather.googleapis.com/v1/currentConditions:lookup";

    public GoogleWeatherAPI(){
        this.geoLocation = ServiceLoader.load(IGeoLocation.class).findFirst().orElseThrow(()->new IllegalStateException("No geolocation found"));

    }

    public GoogleWeatherAPI(IGeoLocation geoLocation){
        this.geoLocation = geoLocation;
    }


    @Override
    public String getName() {
        return "GoogleWeather";
    }

    @Override
    public String getAll(String address) {
        geoLocation.getAll(address);
        double latitude = geoLocation.getLatitude();
        double longitude = geoLocation.getLongitude();

        String requestBody = "{\"location\":{\"latitude\":" + latitude + ",\"longitude\":" + longitude + "},\"unitsSystem\":\"METRIC\"}";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "?key=" + API_Key))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("API SVAR: " + json);

            JSONObject current = new JSONObject(json).getJSONObject("currentConditions");
            this.celcius = current.getJSONObject("temperature").getDouble("degrees");
            this.feelsLikeCelcius = current.getJSONObject("feelsLikeTemperature").getDouble("degrees");
            this.humidity = current.getInt("relativeHumidity");
            this.windSpeed = current.getJSONObject("wind").getJSONObject("speed").getDouble("value");
            this.windDirection = current.getJSONObject("wind").getJSONObject("direction").getDouble("degrees");
            this.cloudCover = current.getInt("cloudCover");
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    public String getWindDirection() {
        double d = ((this.windDirection % 360) + 360) % 360;
        if (d < 22.5) {
            return "N";
        } else if (d < 67.5) {
            return "NØ";
        } else if (d < 112.5) {
            return "Ø";
        } else if (d < 157.5) {
            return "SØ";
        } else if (d < 202.5) {
            return "S";
        } else if (d < 247.5) {
            return "SV";
        } else if (d < 292.5) {
            return "V";
        } else if (d < 337.5) {
            return "NV";
        } else {
            return "N";
        }
    }

    @Override
    public String getCloudCover() {
        if (this.cloudCover <= 10) {
            return "Sky frit";
        } else if (this.cloudCover <= 25) {
            return "Skyer";
        } else if (this.cloudCover <= 50) {
            return "Let Overskyet";
        } else {
            return "Overskyet";
        }
    }
}
