import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;

module Core {
    requires dk.sdu.scs.common;
    requires java.desktop;
    requires jdk.httpserver;
    requires spring.context;
    uses IWeather;
    uses IGeoLocation;
    opens dk.sdu.Core to spring.core, spring.beans, spring.context;
}