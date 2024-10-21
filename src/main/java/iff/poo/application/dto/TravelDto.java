package iff.poo.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TravelDto {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("rota")
    public RouteDto route;

    @JsonProperty("data_hora_partida")
    public LocalDateTime startDate;

    @JsonProperty("data_hora_chegada")
    public LocalDateTime endDate;

    @JsonProperty("status")
    public String status;

    @JsonProperty("veiculo")
    public VehicleDto vehicle;

    @JsonProperty("disponibilidade")
    public AvailabilityDto availability;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AvailabilityDto {
        @JsonProperty("assentos_disponiveis")
        public List<Integer> availableSeats;

        @JsonProperty("id_cidade_origem_requisitada")
        public Long requestedOriginCityId;

        @JsonProperty("id_cidade_destino_requisitada")
        public Long requestedDestinyCityId;

        @JsonProperty("preco")
        public Double price;
    }
}
