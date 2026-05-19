package dk.sdu.Core;

import com.sun.net.httpserver.HttpServer;
import dk.sdu.Core.config.ModuleConfig;
import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ModuleConfig.class);
        List<IWeather> weatherServices = ctx.getBean("IWeatherServiceList", List.class);
        List<IGeoLocation> geoServices = ctx.getBean("IGeoLocationServiceList", List.class);

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        new WeatherFacade(weatherServices, geoServices).registerRoutes(server);
        server.start();

        String serverUrl = "http://localhost:" + port;
        System.out.println("Server started on " + serverUrl);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            ctx.close();
            System.out.println("Server stopped.");
        }));

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI.create(serverUrl));
        }

        // Exit when stdin closes (terminal window closed on Windows).
        try { while (System.in.read() != -1); } catch (IOException ignored) {}
        System.exit(0);
    }
}
