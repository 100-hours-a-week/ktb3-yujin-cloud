package com.ktb3.community.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Getter
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Getter
    @Builder
    public static class LoginResponse{
        private Long id;
        private String email;
        private String nickname;
    }

    // 비밀번호 수정 요청
    @Getter
    @NoArgsConstructor
    public static class ChangePasswordRequest{

        @NotBlank(message = "현재 비밀번호는 필수입니다")
        private String currentPassword;

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
        )
        private String newPassword;

        @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
        private String newPasswordConfirm;

        // 새 비번,비번확인 일치여부
        public boolean isNewPasswordMatching(){
            return newPassword != null && newPassword.equals(newPasswordConfirm);
        }
    }

    @Getter @AllArgsConstructor
    public static class TokenResponse {
        private Long id;
        private String email;
        private String nickname;
        private String accessToken;
        private String refreshToken;
    }

}
