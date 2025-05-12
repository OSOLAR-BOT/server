package capstone.backend.common.apiPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message"})
public abstract class BaseApiResponse {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
}
