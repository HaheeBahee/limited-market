package com.bookstore.api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(

        @NotBlank
        @Email(message = "이메일 형식이 올바르지 않습니다")
        String email,

        // 8자 이상 + 영문 + 숫자 + 특수문자 필수
        @NotBlank(message = "비밀번호는 필수입니다")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[a-zA-Z\\d!@#$%^&*(),.?\":{}|<>]{8,}$",
                message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다"
        )
        String password,

        @NotBlank(message = "이름은 필수입니다")
        String name
) {
}
