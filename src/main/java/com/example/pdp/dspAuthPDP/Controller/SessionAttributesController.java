package com.example.pdp.dspAuthPDP.Controller;

import com.example.pdp.dspAuthPDP.POJO.SessionPOJO;
import com.example.pdp.dspAuthPDP.Service.SessionAttributeService;
import com.example.pdp.dspAuthPDP.Utility.ConfigReader;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping(value = "/Identity")
public class SessionAttributesController
{
    @Autowired
    private SessionAttributeService sessionAttributeService;

    @PostMapping(value = "/indBank/v1/dsp/sessions", consumes = "application/json", produces = "application/json")
    public Map<String, Object> getSessionInfo(
            @RequestParam(value = "_action") @NonNull String action,
            @RequestHeader(value = "x-channel") @NonNull String channel,
            @RequestHeader(value = "Authorization") @NonNull String authHeader,
            @RequestHeader(value = "x-group-member") @NonNull String groupMember,
            @RequestHeader(value = "dspSession") @NonNull String dspSession,
            @RequestBody @NonNull SessionPOJO sessionInfo)
    {
        if(action==null || action.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Query parameter _action cannot be null or empty!");
        }
        if (!action.equalsIgnoreCase("getSessionInfo") && !action.equalsIgnoreCase("getAllSessionInfo"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid query parameter _action value provided!");
        }
        if(authHeader==null || authHeader.isEmpty() || !authHeader.startsWith("Basic "))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or missing Authorization header");
        }
        if(channel==null || channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Header x-channel cannot be null or empty!");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-channel!");
        }
        if(groupMember==null || groupMember.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Header x-group-member cannot be null or empty!");
        }
        if(!groupMember.equalsIgnoreCase("INDBANK"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-group-member!");
        }
        if(!dspSession.equals(sessionInfo.getTokenId()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        if(sessionInfo.getTokenId()==null || sessionInfo.getTokenId().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }
        if(action.equals("getSessionInfo"))
        {
            try {
                String base64Credentials = authHeader.substring("Basic ".length()).trim();
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(decodedBytes);
                String[] values = credentials.split(":", 2);
                String username = values[0];
                String password = values[1];
                if(!username.equals(ConfigReader.getAuthCredentials("clientId_getSessionInfo")) || !password.equals(ConfigReader.getAuthCredentials("clientSecret_getSessionInfo")))
                {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
                }
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access Denied!");
            }
            return sessionAttributeService.getSessionInfo(sessionInfo.getTokenId(), channel);
        }
        if(action.equals("getAllSessionInfo"))
        {
            try {
                String base64Credentials = authHeader.substring("Basic ".length()).trim();
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(decodedBytes);
                String[] values = credentials.split(":", 2);
                String username = values[0];
                String password = values[1];
                if(!username.equals(ConfigReader.getAuthCredentials("clientId_getAllSessionInfo")) || !password.equals(ConfigReader.getAuthCredentials("clientSecret_getAllSessionInfo")))
                {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
                }
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access Denied!");
            }
            return sessionAttributeService.getAllSessionInfo(sessionInfo.getTokenId(), channel);
        }
        return null;
    }
}
