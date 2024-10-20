package iff.poo.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleDto {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("placa")
    public String licensePlate;

    @JsonProperty("modelo")
    public String model;

    @JsonProperty("capacidade")
    public int capacity;

    @JsonProperty("ultima_manutencao")
    public LocalDate lastMaintenance;
}
