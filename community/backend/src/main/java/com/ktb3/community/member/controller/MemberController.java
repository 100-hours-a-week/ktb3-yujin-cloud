package com.ktb3.community.member.controller;

import com.ktb3.community.auth.annotation.AuthMemberId;
import com.ktb3.community.auth.controller.AuthController;
import com.ktb3.community.common.exception.BusinessException;
import com.ktb3.community.member.dto.MemberDto;
import com.ktb3.community.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@Valid @RequestBody MemberDto.CheckEmailRequest request){
        boolean isDuplicate = memberService.isEmailDuplicate(request.getEmail());
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    @PostMapping("/nickname")
    public ResponseEntity<Map<String,Boolean>> checkNickname(@Valid @RequestBody MemberDto.CheckNicknameRequest request) {
        boolean isDuplicate = memberService.isNicknameDuplicate(request.getNickname());
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody MemberDto.SignUpRequest request){
        memberService.signUp(request);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
    }

    // 회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<MemberDto.DetailResponse> getMember(@AuthMemberId Long memberId) {

        MemberDto.DetailResponse response = memberService.getMemberDetail(memberId);
        return ResponseEntity.ok(response);
    }

    // 회원 정보 수정
    // 텍스트 + 파일 전송이라 multipart/form-data형식어야함
    @PatchMapping(value = "/{me}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberDto.DetailResponse> updateMember(
            @RequestParam(value = "nickname", required = false)
            @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하여야 합니다")
            @Pattern(regexp = "^\\S+$", message = "닉네임에 공백을 사용할 수 없습니다")
            String nickname,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "deleteProfileImage", required = false, defaultValue = "false") Boolean deleteProfileImage,
            @AuthMemberId Long memberId) {

        // 최소한 하나는 수정되어야 함
        if (nickname == null && profileImage == null && !deleteProfileImage) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "수정할 내용이 없습니다");
        }

        // 이미지 파일 검증
        if (profileImage != null && !profileImage.isEmpty()) {
            // 컨텐트 타입 검증
            String contentType = profileImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다");
            }
            // 파일 확장자 검증 필요
            // 이미지 사이즈 제한 필요한가?
        }

        MemberDto.DetailResponse response = memberService.updateMember(memberId, nickname,profileImage,deleteProfileImage);

        return ResponseEntity.ok(response);
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<Map<String, String>> deleteMember (@AuthMemberId Long memberId, HttpServletRequest request) {

        // 1. 탈퇴
        memberService.deleteMember(memberId);

        // 2. 로그아웃(토큰 쿠키 삭제)
        // TODO. authController인지 authSErvice인지 로그아웃 연결

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다"));
    }

}
