import dk.sdu.scs.common.services.IWeather;
import dk.sdu.OpenWeather.OpenWeatherAPI;
import dk.sdu.scs.common.services.IGeoLocation;
module OpenWeatherApi {
    uses IGeoLocation;
    requires dk.sdu.scs.common;
    requires java.net.http;
    requires org.json;
    provides IWeather with OpenWeatherAPI;
}