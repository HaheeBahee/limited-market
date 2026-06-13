package com.limitedmarket.api.test;

import com.limitedmarket.api.global.exception.CustomException;
import com.limitedmarket.api.global.exception.ErrorCode;
import com.limitedmarket.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class ExceptionTestController {

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<Void>> testError() {
        throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> testSuccess() {
        return ResponseEntity.ok(ApiResponse.success("테스트 데이터", "성공했습니다"));
    }

}
