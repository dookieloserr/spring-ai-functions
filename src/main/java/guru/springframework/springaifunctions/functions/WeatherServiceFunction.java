package guru.springframework.springaifunctions.functions;

import com.fasterxml.classmate.GenericType;
import guru.springframework.springaifunctions.model.CityRequest;
import guru.springframework.springaifunctions.model.CityResponse;
import guru.springframework.springaifunctions.model.WeatherRequest;
import guru.springframework.springaifunctions.model.WeatherResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Function;

public class WeatherServiceFunction implements Function<WeatherRequest,WeatherResponse> {

    public static final String WEATHER_URL = "https://api.api-ninjas.com/v1/weather";

    public static final String CITY_URL = "https://api.api-ninjas.com/v1/zipcode";

    private final String apiNinjasKey;

    public WeatherServiceFunction(String apiNinjasKey) {
        this.apiNinjasKey = apiNinjasKey;
    }

    @Override
    public WeatherResponse apply(WeatherRequest weatherRequest) {
        CityRequest cityRequest = new CityRequest(weatherRequest.location(), weatherRequest.state());
        CityResponse cityResponse = getCityLatitudeAndLongitude(cityRequest).get(0);
        System.out.println("City Response: " +cityResponse);
        RestClient restClient = RestClient.builder()
                .baseUrl(WEATHER_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("X-Api-Key", apiNinjasKey);
                    httpHeaders.set("Accept","application/json");
                    httpHeaders.set("Content-Type","application/json");
                }).build();
        return restClient.get().uri(uriBuilder -> {
            System.out.println("Building URI for weather request: " + cityResponse);
               uriBuilder.queryParam("lat",cityResponse.lat());
               uriBuilder.queryParam("lon",cityResponse.lon());
            return uriBuilder.build();
        }).retrieve().body(WeatherResponse.class);
    }

    private List<CityResponse> getCityLatitudeAndLongitude(CityRequest cityRequest){

        RestClient restClient = RestClient.builder()
                .baseUrl(CITY_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("X-Api-Key", apiNinjasKey);
                    httpHeaders.set("Accept","application/json");
                    httpHeaders.set("Content-Type","application/json");
                }).build();

        return restClient.get().uri(uriBuilder -> {
            System.out.println("Building URI for city request: " + cityRequest);
            uriBuilder.queryParam("city",cityRequest.city());

            if (cityRequest.state() != null && !cityRequest.state().isBlank()){
                uriBuilder.queryParam("state",cityRequest.state());
            }
            return uriBuilder.build();
        }).retrieve().body(new ParameterizedTypeReference<List<CityResponse>>(){});

    }
}
