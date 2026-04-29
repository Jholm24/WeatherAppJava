import dk.sdu.GeoLocation.GeoLocationAPI;

module dk.sdu.scs.geolocationapi {
    requires dk.sdu.scs.common;
    requires org.json;
    requires java.net.http;
    requires io.github.cdimascio.dotenv.java;
    provides dk.sdu.scs.common.services.IGeoLocation
            with GeoLocationAPI;
}