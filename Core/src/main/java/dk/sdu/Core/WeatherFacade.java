package dk.sdu.Core;

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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WeatherFacade {

    private final List<IWeather> weatherServices;
    private final IGeoLocation geoService;
    private final WeatherRepository repository;
    private final WeatherEventPublisher eventPublisher;
    private final List<OutputStream> sseClients;

    public WeatherFacade(List<IWeather> weatherServices, List<IGeoLocation> geoServices) {
        this.weatherServices = weatherServices;
        this.geoService = geoServices.get(0);
        this.repository = new WeatherRepository();
        this.sseClients = new CopyOnWriteArrayList<>();
        this.eventPublisher = new WeatherEventPublisher();
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
        if (path.equals("/") || path.equals("/index.html")) {
            path = "/ui/index.html";
        }
        try (InputStream resourceStream = WeatherFacade.class.getResourceAsStream(path)) {
            if (resourceStream == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            byte[] responseBytes = resourceStream.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", resolveContentType(path));
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.getResponseBody().close();
        }
    }

    private void handleWeatherRequest(HttpExchange exchange) throws IOException {
        String address = parseAddress(exchange.getRequestURI().getQuery(), "Copenhagen");
        try {
            byte[] responseBytes = getWeather(address).getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.getResponseBody().close();
        }
    }

    private void handleAlertsRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(200, 0);
        OutputStream clientStream = exchange.getResponseBody();
        sseClients.add(clientStream);
        try {
            while (true) {
                Thread.sleep(15000);
                clientStream.write(":\n\n".getBytes());
                clientStream.flush();
            }
        } catch (Exception e) {
            sseClients.remove(clientStream);
            exchange.getResponseBody().close();
        }
    }

    private void handleHistoryRequest(HttpExchange exchange) throws IOException {
        String address = parseAddress(exchange.getRequestURI().getQuery(), null);
        if (address == null || address.isBlank()) {
            exchange.sendResponseHeaders(400, -1);
            exchange.getResponseBody().close();
            return;
        }
        try {
            byte[] responseBytes = getHistory(address).getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.getResponseBody().close();
        }
    }

    private String getWeather(String address) throws Exception {
        geoService.getAll(address);
        int addressId = repository.saveGeoAddress(address, geoService.getLatitude(), geoService.getLongitude());

        StringBuilder jsonArray = new StringBuilder("[");
        for (IWeather weatherService : weatherServices) {
            weatherService.getAll(address);
            eventPublisher.publish(address, weatherService.getWindSpeed());
            try {
                repository.saveWeatherReading(addressId, weatherService);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonArray.append("{")
                    .append("\"provider\":\"").append(weatherService.getName()).append("\",")
                    .append("\"temperature\":").append(weatherService.getTemperature()).append(",")
                    .append("\"feelsLike\":").append(weatherService.getFeelsLikeTemperature()).append(",")
                    .append("\"humidity\":").append(weatherService.getHumidity()).append(",")
                    .append("\"windSpeed\":").append(weatherService.getWindSpeed()).append(",")
                    .append("\"windDirection\":\"").append(weatherService.getWindDirection()).append("\",")
                    .append("\"cloudCover\":\"").append(weatherService.getCloudCover()).append("\"")
                    .append("},");
        }
        if (jsonArray.charAt(jsonArray.length() - 1) == ',') {
            jsonArray.deleteCharAt(jsonArray.length() - 1);
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }

    private String getHistory(String address) throws Exception {
        return repository.getHistory(address);
    }

    private String parseAddress(String queryString, String defaultValue) {
        if (queryString == null) return defaultValue;
        for (String param : queryString.split("&")) {
            if (param.startsWith("address=")) {
                return param.substring(8);
            }
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
