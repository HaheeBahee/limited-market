package com.limitedmarket.api.test;

import com.limitedmarket.api.global.exception.member.DuplicateEmailException;
import com.limitedmarket.api.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class ExceptionTestController {

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<Void>> testError() {
        throw new DuplicateEmailException();
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> testSuccess() {
        return ResponseEntity.ok(ApiResponse.success("테스트 데이터", "성공했습니다"));
    }

}
