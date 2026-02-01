package com.example.pdp.dspAuthPDP.Service;


import com.example.pdp.dspAuthPDP.Utility.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class KeepAliveService
{

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveService.class);

    public Map<String, Object> keepAlive(String tokenId, String channel)
    {
        RestTemplate restTemplate = new RestTemplate();
        String url = ConfigReader.getHost("SelfBuild_host", "DomainHosts") +
                ConfigReader.getURL("getSessionInfo", "SelfBuild");
        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("_action", "getSessionInfo")
                .build()
                .encode()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-channel", channel);
        headers.add("x-group-member", "INDBANK");
        headers.add("dspSession", tokenId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("tokenId", tokenId);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> outcome = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );
        if (outcome.getStatusCode().is2xxSuccessful() && outcome.getBody() != null)
        {
            Map<String, Object> responsebody = outcome.getBody();
            if (Boolean.FALSE.equals(responsebody.get("valid"))) {
                logger.error("Keep Alive Response: Session is not valid.");
                return Map.of("isSessionValid", false);
            }
            else
            {
                Boolean isSessionValid = Boolean.valueOf(responsebody.get("isSessionValid").toString());
                String sessionCorrelation = responsebody.get("sessionCorrelationId").toString();
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("isSessionValid", isSessionValid);
                response.put("sessionCorrelationId", sessionCorrelation);
                logger.info("Keep Alive Response: " + response.toString());
                return response;
            }
        }
        else
        {
            logger.error("Keep Alive Response: Failed to retrieve session information from self build service");
            return Map.of("isSessionValid", false);
        }
    }
}
