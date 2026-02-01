package com.example.pdp.dspAuthPDP.POJO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionPOJO
{
    @JsonProperty(value = "tokenId")
    @NonNull
    private String tokenId;

    public @NonNull String getTokenId() {
        return tokenId;
    }

    public void setTokenId(@NonNull String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "Incoming Request {" +
                "tokenId='" + tokenId + '\'' +
                '}';
    }
}
