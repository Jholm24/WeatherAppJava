package dk.sdu.Core;

import com.sun.net.httpserver.HttpServer;
import dk.sdu.scs.common.services.IWeather;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx =
                new
                        AnnotationConfigApplicationContext(ModuleConfig.class);
        List<IWeather> weatherServices =
                ctx.getBean("IWeatherServiceList", List.class);

        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.equals("/index.html")) {
                path = "/ui/index.html";
            }
            try (InputStream is = Main.class.getResourceAsStream(path)) {
                if (is == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                byte[] bytes = is.readAllBytes();
                String contentType = path.endsWith(".html") ? "text/html" :
                                     path.endsWith(".css")  ? "text/css"  :
                                     path.endsWith(".js")   ? "application/javascript" : "application/octet-stream";
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } catch (IOException e) {
                exchange.sendResponseHeaders(500, -1);
            } finally {
                exchange.getResponseBody().close();
            }
        });
        server.createContext("/api/weather", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String address = "Copenhagen";
            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("address=")) {
                        address = param.substring(8);
                    }
                }
            }

            StringBuilder combined = new StringBuilder("[");
            for (IWeather w : weatherServices) {
                w.getAll(address);
                combined.append("{")
                        .append("\"provider\":\"").append(w.getName()).append("\",")
                        .append("\"temperature\":").append(w.getTemperature()).append(",")
                        .append("\"feelsLike\":").append(w.getFeelsLikeTemperature()).append(",")
                        .append("\"humidity\":").append(w.getHumidity()).append(",")
                        .append("\"windSpeed\":").append(w.getWindSpeed()).append(",")
                        .append("\"windDirection\":\"").append(w.getWindDirection()).append("\",")
                        .append("\"cloudCover\":\"").append(w.getCloudCover()).append("\"")
                        .append("},");
            }
            if (combined.charAt(combined.length() - 1) == ',') {
                combined.deleteCharAt(combined.length() - 1);
            }
            combined.append("]");

            byte[] bytes = combined.toString().getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        });

        server.start();

        String url = "http://localhost:" + port;
        System.out.println("Server started on " + url);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            ctx.close();
            System.out.println("Server stopped.");
        }));

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI.create(url));
        }

        // Exit when stdin closes (terminal window closed)
        try {
            while (System.in.read() != -1) {}
        } catch (IOException ignored) {}
        System.exit(0);
    }
}
