package com.bookstore.api.domain.address;

import com.bookstore.api.domain.member.Member;
import com.bookstore.api.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String addressName;
    private String city;
    private String street;
    private String zipcode;
    private boolean is_default;

}
