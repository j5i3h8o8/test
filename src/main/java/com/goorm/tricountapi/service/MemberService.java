package com.goorm.tricountapi.service;

import com.goorm.tricountapi.model.Member;
import com.goorm.tricountapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    
    // 회원가입

    @Transactional
    public Member signup(Member member) {
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent((member1) -> {
                    throw new RuntimeException("login id duplicated");
                });
        return memberRepository.save(member);
    }

    // 로그인
    public Member login(String loginId, String password) {
        // id, password가 맞으면 통과, 맞지 않으면 로그인 처리를 안해줘야 함
        Member loginMember = memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Member info is not found!"));
        return loginMember;
    }

    
    // 조회 - MemberContext에서 사용하기 위해서
    public Member findMemberById(Long memberId) {
        Optional<Member> loginMember = memberRepository.findById(memberId);
        if(!loginMember.isPresent()) {
            throw new RuntimeException("Member info is not found!");
        }

        return loginMember.get();
    }

}
