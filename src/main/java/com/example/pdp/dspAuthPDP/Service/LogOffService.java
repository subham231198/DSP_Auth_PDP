package com.example.pdp.dspAuthPDP.Service;

import com.example.pdp.dspAuthPDP.POJO.Input_Token_State;
import com.example.pdp.dspAuthPDP.POJO.LogOffProvider;
import com.example.pdp.dspAuthPDP.POJO.Output_token_state;
import com.example.pdp.dspAuthPDP.Utility.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class LogOffService
{
    private static final Logger logger = LoggerFactory.getLogger(LogOffService.class);
    public Map<String, Object> logout(String tokenId)
    {
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            String url = ConfigReader.getHost("SelfBuild_host", "DomainHosts") +
                    ConfigReader.getURL("logOffProvider", "SelfBuild");
            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("_action", "translate")
                    .build()
                    .encode()
                    .toUri();

            LogOffProvider logOffProvider = new LogOffProvider();
            Input_Token_State inputTokenState = new Input_Token_State();
            Output_token_state outputTokenState = new Output_token_state();
            inputTokenState.setToken_type("SSOTOKEN");
            inputTokenState.setTokenId(tokenId);
            outputTokenState.setToken_type("");
            outputTokenState.setSubject_confirmation("Bearer");
            logOffProvider.setInputTokenState(inputTokenState);
            logOffProvider.setOutputTokenState(outputTokenState);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LogOffProvider> entity =
                    new HttpEntity<>(logOffProvider, headers);

            ResponseEntity<Map<String, Object>> outcome = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );
            if (outcome.getStatusCode().is2xxSuccessful() && outcome.getBody() != null)
            {
                logger.info("Log Off Response: Successfully logged out of all sessions.");
                return Map.of("message", "Successfully logged out of all sessions");
            }
            else if(outcome.getStatusCode()==HttpStatus.UNAUTHORIZED)
            {
                logger.error("Log Off Response: Unauthorized - Invalid token.");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error resolving user from JSON");
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error resolving user from JSON");
            }
        }
        catch (HttpClientErrorException.Unauthorized ex) {

            logger.warn("Log Off Response: Unauthorized from producer");
            String body = ex.getResponseBodyAsString();

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Error resolving user from JSON");
        }
        catch (HttpClientErrorException ex) {

            throw new ResponseStatusException(
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString());
        }
        catch (Exception ex) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Logout failed", ex);
        }
    }
}
