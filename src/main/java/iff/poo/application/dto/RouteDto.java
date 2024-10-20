package iff.poo.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteDto {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("cidade_de_origem")
    public CityDto originCity;

    @JsonProperty("cidade_de_destino")
    public CityDto destinyCity;

    @JsonProperty("distancia")
    public Double distance;

    @JsonProperty("preco_base")
    public Double basePrice;

    @JsonProperty("paradas_intermediarias")
    public List<RouteStop> routeStops;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RouteStop {
        @JsonProperty("id")
        public Long id;

        @JsonProperty("cidade")
        public CityDto city;

        @JsonProperty("ordem")
        public int stopOrder;

        @JsonProperty("distance_da_origem")
        public Double distanceFromOrigin;
    }
}
