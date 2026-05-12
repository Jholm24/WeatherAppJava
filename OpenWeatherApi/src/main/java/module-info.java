import dk.sdu.scs.common.services.IWeather;
import dk.sdu.OpenWeather.OpenWeatherAPI;
module OpenWeatherApi {
    requires dk.sdu.scs.common;
    requires java.net.http;
    requires org.json;
    provides IWeather with OpenWeatherAPI;
}