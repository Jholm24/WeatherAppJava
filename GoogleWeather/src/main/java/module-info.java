import dk.sdu.GoogleWeather.GoogleWeatherAPI;
import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;

module GoogleWeather {
    requires dk.sdu.scs.common;
    requires org.json;
    requires java.net.http;
    uses IGeoLocation;
    provides IWeather with GoogleWeatherAPI;
}