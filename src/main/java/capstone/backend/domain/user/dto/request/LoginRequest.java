package capstone.backend.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "사용자 아이디는 비어있을 수 없습니다.")
    @Size(max = 20, message = "사용자 아이디는 최대 20자를 초과할 수 없습니다.")
    private String username;

    @NotBlank(message = "사용자 비밀번호는 비어있을 수 없습니다.")
    @Size(max = 20, message = "사용자 비밀번호는 최대 20자를 초과할 수 없습니다.")
    private String password;
}

