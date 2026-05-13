package dk.sdu.Core.observer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WindAlertObserver implements IWeatherObserver {
    private static final double THRESHOLD = 10;
    private final List<OutputStream> clients;

    public WindAlertObserver(List<OutputStream> clients) {
        this.clients = clients;
    }

    @Override
    public void onWeatherUpdate(String address, double windSpeed) {
        if (windSpeed < THRESHOLD) return;
        byte[] msg = ("data: Ekstreme vindforhold, vær opmærksom\n\n").getBytes();
        clients.removeIf(out -> {
            try {
                out.write(msg);
                out.flush();
                return false;
            } catch (IOException e) {
                return true;
            }
        });
    }
}
