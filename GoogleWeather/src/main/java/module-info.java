import dk.sdu.GoogleWeather.GoogleWeatherAPI;
import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;

module GoogleWeather {
    requires dk.sdu.scs.common;
    requires org.json;
    requires java.net.http;
    requires io.github.cdimascio.dotenv.java;
    uses IGeoLocation;
    provides IWeather with GoogleWeatherAPI;
}