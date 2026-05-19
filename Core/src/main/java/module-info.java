import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;

module Core {
    requires dk.sdu.scs.common;
    requires java.desktop;
    requires jdk.httpserver;
    requires spring.context;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires com.google.gson;
    uses IWeather;
    uses IGeoLocation;
    opens dk.sdu.Core to spring.core, spring.beans, spring.context;
    opens dk.sdu.Core.config to spring.core, spring.beans, spring.context;
}