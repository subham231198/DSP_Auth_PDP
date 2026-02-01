package com.example.pdp.dspAuthPDP.Service;


import com.example.pdp.dspAuthPDP.Utility.ConfigReader;
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
public class SessionAttributeService
{

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SessionAttributeService.class);

    public Map<String, Object> getSessionAttributes(String tokenId, String channel)
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
                logger.error("Session Attributes Response: Session is not valid.");
                return Map.of("valid", false);
            }
            else
            {
                Boolean isSessionValid = Boolean.valueOf(responsebody.get("isSessionValid").toString());
                String sessionCorrelation = responsebody.get("sessionCorrelationId").toString();
                if(isSessionValid && sessionCorrelation!=null && !sessionCorrelation.isEmpty())
                {
                    logger.info("Session Attributes Response: Session is valid.");
                    return responsebody;
                }
                else
                {
                    logger.error("Session Attributes Response: Session is not valid based on isSessionValid flag.");
                    return Map.of("valid", false);
                }
            }
        }
        else
        {
            logger.error("Keep Alive Response: Failed to retrieve session information from self build service");
            return Map.of("valid", false);
        }
    }


    public Map<String , Object> getSessionInfo(String tokenId, String channel)
    {
        Map<String, Object> sessionAttributes = getSessionAttributes(tokenId, channel);
        if(sessionAttributes.containsKey("valid") && Boolean.FALSE.equals(sessionAttributes.get("valid"))) {
            return Map.of("valid", false);
        }

        String customerId = sessionAttributes.get("customerId").toString();
        String sessionCorrelation = sessionAttributes.get("sessionCorrelationId").toString();
        Boolean isSessionValid = Boolean.valueOf(sessionAttributes.get("isSessionValid").toString());
        String issuedAt = sessionAttributes.get("issuedAt").toString();
        String expiry = sessionAttributes.get("expiry").toString();
        String authLevel = sessionAttributes.get("authLevel").toString();

        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("channel", channel);
        properties.put("authLevel", authLevel);
        properties.put("issuedAt", issuedAt);
        properties.put("expiry", expiry);

        response.put("isSessionValid", isSessionValid);
        response.put("sessionCorrelationId", sessionCorrelation);
        response.put("customerId", customerId);
        response.put("properties", properties);


        return response;
    }

    public Map<String, Object> getAllSessionInfo(String tokenId, String channel)
    {
        Map<String, Object> sessionAttributes = getSessionAttributes(tokenId, channel);
        if(sessionAttributes.containsKey("valid") && Boolean.FALSE.equals(sessionAttributes.get("valid"))) {
            return Map.of("valid", false);
        }

        String customerId = sessionAttributes.get("customerId").toString();
        String sessionCorrelation = sessionAttributes.get("sessionCorrelationId").toString();
        Boolean isSessionValid = Boolean.valueOf(sessionAttributes.get("isSessionValid").toString());
        String issuedAt = sessionAttributes.get("issuedAt").toString();
        String accountType = sessionAttributes.get("accountType").toString();
        String expiry = sessionAttributes.get("expiry").toString();
        String authLevel = sessionAttributes.get("authLevel").toString();
        String alg = sessionAttributes.get("alg").toString();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("isSessionValid", isSessionValid);
        response.put("sessionCorrelationId", sessionCorrelation);
        response.put("customerId", customerId);
        response.put("accountType", accountType);
        response.put("authLevel", authLevel);
        response.put("issuedAt", issuedAt);
        response.put("expiry", expiry);
        response.put("alg", alg);

        return response;
    }
}
