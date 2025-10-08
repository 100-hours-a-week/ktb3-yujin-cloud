package com.ktb3.community.member.entity;

import com.ktb3.community.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void create_member_test(){
        // CREATE
        Member member = Member.builder()
                .email("crud@test.com")
                .nickname("크루드")
                .build();
        Member saved = memberRepository.save(member);

        System.out.println("✅ CREATE 성공 - ID: " + saved.getId());

        // READ
        Member found = memberRepository.findById(saved.getId())
                .orElseThrow();
        System.out.println("✅ READ 성공 - 닉네임: " + found.getNickname());

        // UPDATE
        found.updateNickname("변경됨");
        memberRepository.flush();
        System.out.println("✅ UPDATE 성공 - 새 닉네임: " + found.getNickname());

        // DELETE (소프트 삭제)
        found.delete();
        memberRepository.flush();
        System.out.println("✅ DELETE 성공 - 삭제일: " + found.getDeletedAt());

        System.out.println("\n🎉 모든 CRUD 작업 정상!");

    }

}