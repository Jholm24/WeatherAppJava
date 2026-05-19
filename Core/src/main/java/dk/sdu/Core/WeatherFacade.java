package dk.sdu.Core;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dk.sdu.Core.db.WeatherRepository;
import dk.sdu.Core.observer.WeatherEventPublisher;
import dk.sdu.Core.observer.WindAlertObserver;
import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WeatherFacade {

    private final List<IWeather> weatherServices;
    private final IGeoLocation geoService;
    private final WeatherRepository repository = new WeatherRepository();
    private final List<OutputStream> sseClients = new CopyOnWriteArrayList<>();
    private final WeatherEventPublisher eventPublisher = new WeatherEventPublisher();
    private final Gson gson = new Gson();

    public WeatherFacade(List<IWeather> weatherServices, List<IGeoLocation> geoServices) {
        this.weatherServices = weatherServices;
        this.geoService = geoServices.get(0);
        this.eventPublisher.subscribe(new WindAlertObserver(sseClients));
    }

    public void registerRoutes(HttpServer server) {
        server.createContext("/", this::handleStaticFile);
        server.createContext("/api/weather", this::handleWeatherRequest);
        server.createContext("/api/alerts", this::handleAlertsRequest);
        server.createContext("/api/history", this::handleHistoryRequest);
    }

    private void handleStaticFile(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/") || path.equals("/index.html")) path = "/ui/index.html";
        try (InputStream resource = WeatherFacade.class.getResourceAsStream(path)) {
            if (resource == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            byte[] bytes = resource.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", resolveContentType(path));
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        }
    }

    private void handleWeatherRequest(HttpExchange exchange) throws IOException {
        String address = parseAddress(exchange.getRequestURI().getQuery(), "Copenhagen");
        try {
            writeJson(exchange, getWeather(address));
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private void handleHistoryRequest(HttpExchange exchange) throws IOException {
        String address = parseAddress(exchange.getRequestURI().getQuery(), null);
        if (address == null || address.isBlank()) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        try {
            writeJson(exchange, repository.getHistory(address));
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private void handleAlertsRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(200, 0);
        OutputStream client = exchange.getResponseBody();
        sseClients.add(client);
        try {
            while (true) {
                Thread.sleep(15000);
                client.write(":\n\n".getBytes());
                client.flush();
            }
        } catch (IOException | InterruptedException e) {
            sseClients.remove(client);
        }
    }

    private String getWeather(String address) throws Exception {
        geoService.getAll(address);
        int addressId = repository.saveGeoAddress(address, geoService.getLatitude(), geoService.getLongitude());

        List<Map<String, Object>> entries = new ArrayList<>();
        for (IWeather service : weatherServices) {
            service.getAll(address);
            eventPublisher.publish(address, service.getWindSpeed());
            repository.saveWeatherReading(addressId, service);
            entries.add(Map.of(
                    "provider", service.getName(),
                    "temperature", service.getTemperature(),
                    "feelsLike", service.getFeelsLikeTemperature(),
                    "humidity", service.getHumidity(),
                    "windSpeed", service.getWindSpeed(),
                    "windDirection", service.getWindDirection(),
                    "cloudCover", service.getCloudCover()));
        }
        return gson.toJson(entries);
    }

    private void writeJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, Exception e) throws IOException {
        e.printStackTrace();
        exchange.sendResponseHeaders(500, -1);
        exchange.close();
    }

    private String parseAddress(String query, String defaultValue) {
        if (query == null) return defaultValue;
        for (String param : query.split("&")) {
            if (param.startsWith("address=")) return param.substring("address=".length());
        }
        return defaultValue;
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        return "application/octet-stream";
    }
}
