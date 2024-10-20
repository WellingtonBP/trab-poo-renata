package iff.poo.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("nome")
    public String name;

    @JsonProperty("email")
    public String email;

    @JsonProperty("senha")
    public String password;
}
