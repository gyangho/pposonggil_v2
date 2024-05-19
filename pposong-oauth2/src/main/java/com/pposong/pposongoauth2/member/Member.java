package com.pposong.pposongoauth2.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private String email;
    private String role;//유저 권한(유저, 권리자)
    private String provider; //공급자(구글, 페이스북..)
    private String providerId; //공급 아이디
}
