package com.ktb3.community.member.dto;

import com.ktb3.community.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    public static class CheckEmailRequest {
        @NotBlank(message = "이메일은 필수입력입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class CheckNicknameRequest{
        @NotBlank(message = "닉네임은 필수입력입니다.")
        @Size(min = 1, max = 10, message = "닉네임은 최대 10자까지만 가능합니다.")
        @Pattern(regexp = "^\\S+$", message = "닉네임에 공백은 사용할 수 없습니다")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class SignUpRequest{

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
        )
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        private String passwordConfirm;

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 1, max = 10, message = "닉네임은 최대 10자까지만 가능합니다.")
        @Pattern(regexp = "^\\S+$", message = "닉네임에 공백은 사용할 수 없습니다.")
        private String nickname;

        // 비번,비번확인 일치여부
        public boolean isPasswordMatching(){
            return password != null && password.equals(passwordConfirm);
        }

    }

    @Getter
    @Builder
    public static class SignUpResponse{
        private Long id;
        private String email;
        private String nickname;

        public static SignUpResponse from(Member member) {
            return SignUpResponse.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .build();
        }
    }

    // 회원정보 수정
    public static class UpdateRequest {
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 1, max = 10, message = "닉네임은 최대 10자까지만 가능합니다.")
        @Pattern(regexp = "^\\S+$", message = "닉네임에 공백은 사용할 수 없습니다.")
        private String nickname;
    }

    // 회원정보 응답
    @Getter
    @Builder
    public static class DetailResponse {
        private Long id;
        private String email;
        private String nickname;
        private String profileUrl;

        public static DetailResponse from(Member member,String profileUrl) {
            return DetailResponse.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .profileUrl(profileUrl)
                    .build();
        }
    }

    // 회원
}
