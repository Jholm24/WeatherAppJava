package dk.sdu.Core;

import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
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
        server.start();

        String url = "http://localhost:" + port;
        System.out.println("Server started on " + url);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            System.out.println("Server stopped.");
        }));

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI.create(url));
        }

        Thread.currentThread().join();
    }
}
