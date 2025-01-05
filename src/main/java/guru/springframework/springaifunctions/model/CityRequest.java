package guru.springframework.springaifunctions.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonClassDescription("City API Request")
public record CityRequest(@JsonProperty (required = true, value = "city")
                              @JsonPropertyDescription ("The city name.g. San Francisco, CA") String city,
                          @JsonProperty (required = false)
                              @JsonPropertyDescription ("Optional two letter State for US Cities Only") String state
                              ){

}
