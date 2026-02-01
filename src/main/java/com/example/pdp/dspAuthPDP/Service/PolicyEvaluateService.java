package com.example.pdp.dspAuthPDP.Service;


import com.example.pdp.dspAuthPDP.Utility.JwtUtility;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@Service
public class PolicyEvaluateService
{
    @Autowired
    private SessionAttributeService sessionAttributeService;

//    @Autowired
//    private SamlTokenService samlTokenService;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PolicyEvaluateService.class);

    public Map<String, Object> evaluatePolicy(String tokenId, String outputToken, String channel)
    {
       if(!outputToken.equalsIgnoreCase("SECPJWT"))
       {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value output_token_type");
       }
       Map<String, Object> sessionAttributes = sessionAttributeService.getSessionAttributes(tokenId, channel);
       if(Boolean.FALSE.equals(sessionAttributes.get("valid")))
       {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
       }
        String customerId = sessionAttributes.get("customerId").toString();
        String sessionCorrelation = sessionAttributes.get("sessionCorrelationId").toString();
        Boolean isSessionValid = Boolean.valueOf(sessionAttributes.get("isSessionValid").toString());
        String authLevel = sessionAttributes.get("authLevel").toString();
        String accountType = sessionAttributes.get("accountType").toString();
        Assert.isTrue(isSessionValid, "Session is not valid");
        if(outputToken.equals("SECPJWT"))
        {
            String jwtToken = JwtUtility.generateCustomerToken(tokenId, authLevel, Instant.now().toString(), Instant.now().plusSeconds(300).toString(), customerId, sessionCorrelation, accountType, channel);
            logger.info("issued_JWT = "+jwtToken);
            return Map.of("issued_JWT", jwtToken);
        }
        if(outputToken.equals("SECPSAML"))
        {
//            Assertion assertion = samlTokenService.createSamlAssertion(tokenId, authLevel, customerId, sessionCorrelation, accountType, channel);
            return Map.of("issued_SAML", "assertion");
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value output_token_type");
        }
    }
}
