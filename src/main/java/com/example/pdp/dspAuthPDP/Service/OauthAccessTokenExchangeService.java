package com.example.pdp.dspAuthPDP.Service;

import com.example.pdp.dspAuthPDP.Utility.ConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OauthAccessTokenExchangeService
{
    @Autowired
    PolicyEvaluateService policyEvaluateService;

    public Map<String, Object> exchangeToken(
            String accessToken,
            String authorization,
            String outputToken,
            String channel
    )
    {
        String tokenId = null;
        ResponseEntity<Map<String, Object>> outcome;
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            String url = ConfigReader.getHost("SelfBuild_host", "DomainHosts")+ConfigReader.getURL("oauthSessionInfo", "SelfBuild");
            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("access_token", accessToken)
                    .build()
                    .encode()
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", authorization);
            headers.add("Content-Type", "application/x-www-form-urlencoded");


            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(headers);

            outcome = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );
            if(!outcome.getStatusCode().is2xxSuccessful() || outcome.getBody() == null)
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
            }
            if(outcome.getStatusCode().is4xxClientError() && outcome.getBody() == null)
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
            }

        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Map<String, Object> responsebody = outcome.getBody();
        tokenId = responsebody.get("tokenId").toString();

        return policyEvaluateService.evaluatePolicy(
                tokenId,
                outputToken,
                channel
        );
    }

    public String encodeCredentials(String clientId, String clientSecret)
    {
        String credentials = clientId + ":" + clientSecret;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
