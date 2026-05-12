package dk.sdu.OpenWeather;

import dk.sdu.scs.common.services.IWeather;
import dk.sdu.scs.common.services.IGeoLocation;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ServiceLoader;

public class OpenWeatherAPI implements IWeather {

    private final IGeoLocation geoLocation;
    private double latitude;
    private double longitude;
    private static final String excludeData = "exclude=minutely,hourly,daily,alerts";
    // data fields
    private double temp;
    private double feelsLike;
    private int humidity;
    private int windDeg;
    private int cloudCover;
    private double windSpeed;

    private static final String API_Key = Dotenv.load().get("OPENWEATHER_KEY");
    private static final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";

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
    public String getName() {
        return "OpenWeather";
    }

    @Override
    public String getAll(String address) {
        geoLocation.getAll(address);
        this.latitude = geoLocation.getLatitude();
        this.longitude = geoLocation.getLongitude();
        String requestURL = baseUrl +"?lat=" + latitude + "&lon=" + longitude + "&"+ "units=metric&" + excludeData + "&appid=" + API_Key;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestURL))
                    .GET()
                    .build();
            String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("API SVAR: " + json);
            JSONObject root = new JSONObject(json);
            this.temp = root.getJSONObject("main").getDouble("temp");
            this.feelsLike = root.getJSONObject("main").getDouble("feels_like");
            this.humidity = root.getJSONObject("main").getInt("humidity");
            this.windDeg = root.getJSONObject("wind").getInt("deg");
            this.windSpeed = root.getJSONObject("wind").getDouble("speed");
            this.cloudCover = root.getJSONObject("clouds").getInt("all");
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
        return temp;
    }

    @Override
    public double getFeelsLikeTemperature() {
        return  feelsLike;
    }

    @Override
    public double getHumidity() {
        return  humidity;
    }

    @Override
    public double getWindSpeed() {
        return  windSpeed;
    }

    @Override
    public String getWindDirection() {
        int d = ((this.windDeg % 360) + 360) % 360;
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
        int cloud = this.cloudCover;
        if(cloud <=10)
        {
            return "Sky frit";
        }
        else if(cloud <=25)
        {
            return "Skyer";
        } else if (cloud <=50) {
            return "Let Overskyet";
        }
        else {
            return "Overskyet";
        }
    }
}
