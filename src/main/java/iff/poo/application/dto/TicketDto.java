package iff.poo.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketDto {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("viagem_id")
    public Long travelId;

    @JsonProperty("parada_origem_id")
    public Long originRouteStopId;

    @JsonProperty("parada_destino_id")
    public Long destinyRouteStopId;

    @JsonProperty("usuario_id")
    public Long userId;

    @JsonProperty("numero_assento")
    public int seatNumber;

    @JsonProperty("pagamento")
    public Payment payment;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payment {
        @JsonProperty("id")
        public Long id;

        @JsonProperty("tipo")
        public String type;

        @JsonProperty("status")
        public String status;

        @JsonProperty("meta")
        public Map<String, Object> meta;
    }
}
