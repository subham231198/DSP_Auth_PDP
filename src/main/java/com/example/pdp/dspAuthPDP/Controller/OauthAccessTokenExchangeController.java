package com.example.pdp.dspAuthPDP.Controller;

import com.example.pdp.dspAuthPDP.POJO.PolicyEvaluatePOJO;
import com.example.pdp.dspAuthPDP.Service.OauthAccessTokenExchangeService;
import com.example.pdp.dspAuthPDP.Utility.ConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/oauth/dsp")
public class OauthAccessTokenExchangeController
{

    @Autowired
    private OauthAccessTokenExchangeService oauthAccessTokenExchangeService;

    @PostMapping(value = "/rest-sts/v1/token/exchange/accesstoken_to_customerjwt", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> exchangeToken_JWT(
            @RequestHeader(value = "x-channel") String channel,
            @RequestBody PolicyEvaluatePOJO policyEvaluatePOJO
    )
    {
        if(!policyEvaluatePOJO.getInputTokenState().getToken_type().equals("ACCESSTOKEN"))
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "invalid input_token_type"
            );
        }
        if(!policyEvaluatePOJO.getOutputTokenState().getToken_type().equals("JWT"))
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "invalid output_token_type"
            );
        }
        String accessToken = policyEvaluatePOJO.getInputTokenState().getTokenId();
        String clientId = ConfigReader.getAuthCredentials("clientId_oauth");
        String clientSecret = ConfigReader.getAuthCredentials("clientSecret_oauth");
        String authorization = "Basic " +
                oauthAccessTokenExchangeService.encodeCredentials(
                        clientId,
                        clientSecret
                );
        Map<String, Object> response = oauthAccessTokenExchangeService.exchangeToken(
                accessToken,
                authorization,
                policyEvaluatePOJO.getOutputTokenState().getToken_type(),
                channel
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/rest-sts/v1/token/exchange/accesstoken_to_saml", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> exchangeToken_SAML(
            @RequestHeader(value = "x-channel") String channel,
            @RequestBody PolicyEvaluatePOJO policyEvaluatePOJO
    )
    {
        if(!policyEvaluatePOJO.getInputTokenState().getToken_type().equals("ACCESSTOKEN"))
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "invalid input_token_type"
            );
        }
        if(!policyEvaluatePOJO.getOutputTokenState().getToken_type().equals("SAML"))
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "invalid output_token_type"
            );
        }
        String accessToken = policyEvaluatePOJO.getInputTokenState().getTokenId();
        String clientId = ConfigReader.getAuthCredentials("clientId_oauth");
        String clientSecret = ConfigReader.getAuthCredentials("clientSecret_oauth");
        String authorization = "Basic " +
                oauthAccessTokenExchangeService.encodeCredentials(
                        clientId,
                        clientSecret
                );
        Map<String, Object> response = oauthAccessTokenExchangeService.exchangeToken(
                accessToken,
                authorization,
                policyEvaluatePOJO.getOutputTokenState().getToken_type(),
                channel
        );
        return ResponseEntity.ok(response);
    }
}
