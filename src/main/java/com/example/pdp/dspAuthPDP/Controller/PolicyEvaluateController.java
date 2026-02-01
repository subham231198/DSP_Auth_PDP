package com.example.pdp.dspAuthPDP.Controller;



import com.example.pdp.dspAuthPDP.POJO.PolicyEvaluatePOJO;
import com.example.pdp.dspAuthPDP.Service.PolicyEvaluateService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/Identity")
public class PolicyEvaluateController
{
    @Autowired
    private PolicyEvaluateService policyEvaluateService;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PolicyEvaluateController.class);

    @PostMapping(value = {"/dsp/rest-sts/policies", "/dsp/rest-sts/v1/root/policies"}, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> evaluatePolicy(
            @RequestParam(value = "_action") String action,
            @RequestHeader(value = "dspSession") String tokenId,
            @RequestHeader(value = "x-channel") String channel,
            @RequestBody PolicyEvaluatePOJO policy)
    {
        if(action == null || action.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing query parameter_action");
        }
        if(!action.equalsIgnoreCase("evaluate"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value for query parameter _action");
        }
        if(tokenId == null || tokenId.isEmpty() || !tokenId.equals(policy.getInputTokenState().getTokenId()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        if(policy.getInputTokenState()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "input_token_state cannot be null!");
        }
        if(policy.getOutputTokenState()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "output_token_state cannot be null!");
        }
        if(policy.getInputTokenState().getTokenId() == null || policy.getInputTokenState().getTokenId().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        if(policy.getOutputTokenState().getToken_type() == null || policy.getOutputTokenState().getToken_type().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-channel!");
        }

        logger.info(policy.toString());
        return new ResponseEntity<>(policyEvaluateService.evaluatePolicy(policy.getInputTokenState().getTokenId(), policy.getOutputTokenState().getToken_type(), channel), HttpStatus.OK);
    }
}
