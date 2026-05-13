package dk.sdu.Core.config;
import java.util.List;
import java.util.ServiceLoader;
import static java.util.stream.Collectors.toList;
import dk.sdu.scs.common.services.IGeoLocation;
import dk.sdu.scs.common.services.IWeather;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModuleConfig {

    public ModuleConfig() {
    }

    @Bean
    public List<IGeoLocation> IGeoLocationServiceList(){
        return ServiceLoader.load(IGeoLocation.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean
    public List<IWeather> IWeatherServiceList(){
        return ServiceLoader.load(IWeather.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }


}
