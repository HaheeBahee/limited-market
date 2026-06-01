package com.bookstore.api.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 로그인 시 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 회원가입 시 이메일 중복 체크
    boolean existsByEmail(String email);
}
