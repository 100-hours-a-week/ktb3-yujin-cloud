package com.ktb3.community.auth.service;

import com.ktb3.community.auth.dto.AuthDto;
import com.ktb3.community.member.entity.Member;
import com.ktb3.community.member.entity.MemberAuth;
import com.ktb3.community.member.repository.MemberAuthRepository;
import com.ktb3.community.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberAuthRepository memberAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request){

        // 1. 이메일로 회원조회
        Member member = memberRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 2. 해당 회원의 비밀번호 조회
        MemberAuth memberAuth = memberAuthRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("인증정보를 찾을 수 없습니다."));

        // 3. 2번의 값과 입력값 일치하는지 확인
        if (!passwordEncoder.matches(request.getPassword(), memberAuth.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return AuthDto.LoginResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();

    }

    @Transactional
    public void changePassword(Long memberId, String currentPassword, String newPassword) {

        // 1. 회원 조회
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. 인증정보 조회
        MemberAuth memberAuth = memberAuthRepository.findById(memberId)
                .orElseThrow(()-> new IllegalArgumentException("인증정보를 찾을 수 없습니다."));

        // 3. 현재 비밀번호 맞는지 확인
        if(!passwordEncoder.matches(currentPassword, memberAuth.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일지하지 않습니다.");
        }

        // 4. 새로운 비밀번호랑 현재 비밀번호가 다른지 확인
        if (passwordEncoder.matches(newPassword, memberAuth.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다");
        }

        // 5. 새로운 비밀번호 암호화하여 변경
        String encodedPassword = passwordEncoder.encode(newPassword);
        memberAuth.changePassword(encodedPassword);
    }

}
