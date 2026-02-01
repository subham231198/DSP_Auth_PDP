package com.example.pdp.dspAuthPDP.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyEvaluatePOJO
{
    @JsonProperty(value = "input_token_state")
    @NonNull
    private Input_Token_State inputTokenState;

    @JsonProperty(value = "output_token_state")
    @NonNull
    private Output_token_state outputTokenState;

    public Input_Token_State getInputTokenState() {
        return inputTokenState;
    }

    public void setInputTokenState(Input_Token_State inputTokenState) {
        this.inputTokenState = inputTokenState;
    }

    public Output_token_state getOutputTokenState() {
        return outputTokenState;
    }

    public void setOutputTokenState(Output_token_state outputTokenState) {
        this.outputTokenState = outputTokenState;
    }

    @Override
    public String toString() {
        return "Incoming PE Request {" +
                "inputTokenState=" + inputTokenState +
                ", outputTokenState=" + outputTokenState +
                '}';
    }
}
