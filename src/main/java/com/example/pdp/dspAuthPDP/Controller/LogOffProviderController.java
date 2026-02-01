package com.example.pdp.dspAuthPDP.Controller;

import com.example.pdp.dspAuthPDP.POJO.LogOffProvider;
import com.example.pdp.dspAuthPDP.Service.LogOffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class LogOffProviderController
{
    @Autowired
    private LogOffService logOffService;

    private static final Logger logger = LoggerFactory.getLogger(LogOffService.class);

    @PostMapping(value = "/dsp/v1/rest-sts/sessions-logout")
    public ResponseEntity<Map<String, Object>> logOffProvider(@RequestParam(value = "_action") String action, @RequestBody LogOffProvider logOffProvider)
    {
        if(!action.equals("translate"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid query passed against action!");
        }
        if(!logOffProvider.getInputTokenState().getToken_type().equals("SSOTOKEN"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token-type in request");
        }
        else if(logOffProvider.getInputTokenState().getTokenId().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or blank!");
        }
        else if(!logOffProvider.getOutputTokenState().getSubject_confirmation().equals("Bearer"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value passed against subject confirmation!");
        }
        else
        {
            logger.info(logOffProvider.toString());
            Map<String, Object> result = logOffService.logout(logOffProvider.getInputTokenState().getTokenId());
            if(result!=null)
            {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }
}
