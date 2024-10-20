package iff.poo.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityDto {
    @JsonProperty("nome")
    public String name;

    @JsonProperty("uf")
    public String uf;
}
