package capstone.backend.common.apiPayload.failure;

import capstone.backend.common.apiPayload.BaseApiResponse;
import lombok.Getter;

@Getter
public class ExceptionApiResponse extends BaseApiResponse {

    public ExceptionApiResponse(Boolean isSuccess, String code, String message) {
        super(isSuccess, code, message);
    }

}