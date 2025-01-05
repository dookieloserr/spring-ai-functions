package guru.springframework.springaifunctions.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonClassDescription("City API Response")
public record CityResponse(
                            @JsonPropertyDescription ("The city name.g. San Francisco, CA") String city,
                            @JsonPropertyDescription ("The city state name.g. CA") String state,
                            @JsonPropertyDescription ("Latitude of the city") String lat,
                            @JsonPropertyDescription ("Longitude of the city") String lon
){

}
