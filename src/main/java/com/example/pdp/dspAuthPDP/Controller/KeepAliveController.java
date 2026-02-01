package com.example.pdp.dspAuthPDP.Controller;


import com.example.pdp.dspAuthPDP.POJO.SessionPOJO;
import com.example.pdp.dspAuthPDP.Service.KeepAliveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/Identity")
public class KeepAliveController
{

    @Autowired
    private KeepAliveService keepAliveService;

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveController.class);

    Map<String, Object> response_401 = new LinkedHashMap<>(Map.of("code", 401,"reason","Unauthorized","message", "Access Denied!"));

    @PostMapping(value = "/indBank/dsp/v1/sessions")
    public ResponseEntity<Map<String, Object>> keepAlive_nonInternet(@RequestParam(value = "_action") String action, @RequestHeader(name = "x-channel") String channel, @RequestHeader(name = "x-group-member") String groupMember, @RequestHeader(value = "dspSession") String tokenId, @RequestBody SessionPOJO keepAlive)
    {
        if(action==null || action.isEmpty() || !action.equalsIgnoreCase("keepAlive"))
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid _action parameter value provided!"));
        }
        if(channel==null || channel.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing x-channel header!"));
        }
        if(groupMember==null || groupMember.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing x-group-member header!"));
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing dspSession header!"));
        }
        if(!tokenId.equals(keepAlive.getTokenId()))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response_401);
        }
        if(!channel.equals("WEB") && !channel.equals("MOBILE"))
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid x-channel header value!"));
        }
        if(!groupMember.equals("INDBANK"))
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid x-group-member header value!"));
        }
        logger.info(keepAlive.toString());
        return ResponseEntity.ok(keepAliveService.keepAlive(tokenId, channel));
    }

    @PostMapping(value = "/indBank/dsp/v1/sessions/keepAlive")
    public ResponseEntity<Map<String, Object>> keepAlive_Internet(@RequestHeader(name = "x-channel") String channel, @RequestHeader(name = "x-group-member") String groupMember,@RequestHeader(value = "dspSession") String tokenId, @RequestBody SessionPOJO keepAlive)
    {
        if(channel==null || channel.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing x-channel header!"));
        }
        if(groupMember==null || groupMember.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing x-group-member header!"));
        }
        if(tokenId==null || tokenId.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing dspSession header!"));
        }
        if(!tokenId.equals(keepAlive.getTokenId()))
        {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response_401);
        }
        if(!channel.equals("WEB") && !channel.equals("MOBILE"))
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid x-channel header value!"));
        }
        if(!groupMember.equals("INDBANK"))
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid x-group-member header value!"));
        }
        logger.info(keepAlive.toString());
        return ResponseEntity.ok(keepAliveService.keepAlive(tokenId, channel));
    }
}
