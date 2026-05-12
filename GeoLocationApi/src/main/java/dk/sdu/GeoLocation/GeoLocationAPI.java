package dk.sdu.GeoLocation;

import dk.sdu.scs.common.services.IGeoLocation;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeoLocationAPI implements IGeoLocation {
    private static final String API_KEY = Dotenv.load().get("GEOCODING_KEY");
    private static final String BASE_URL = "https://geocode.maps.co/search";

    private double latitude;
    private double longitude;

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public String getAll(String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?q=" + encodedAddress + "&api_key=" + API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("API SVAR: " + json);
            parseCoordinates(json);
            return json;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseCoordinates(String json) {
        JSONArray results = new JSONArray(json);

        if (!results.isEmpty()) {
            JSONObject location = results.getJSONObject(0);

            this.latitude = location.getDouble("lat");
            this.longitude = location.getDouble("lon");
        }
    }
}