package com.fastcampus.batchcampus.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ServicePolicy {
    A(1L, "/fastcampus/services/a", 10),
    B(2L, "/fastcampus/services/b", 10),
    C(3L, "/fastcampus/services/c", 10),
    D(4L, "/fastcampus/services/d", 10),
    E(5L, "/fastcampus/services/e", 15),
    F(6L, "/fastcampus/services/f", 15),
    G(7L, "/fastcampus/services/g", 10),
    H(8L, "/fastcampus/services/h", 10),
    I(9L, "/fastcampus/services/i", 10),
    J(10L, "/fastcampus/services/j", 10),
    K(11L, "/fastcampus/services/k", 10),
    L(12L, "/fastcampus/services/l", 12),
    M(13L, "/fastcampus/services/m", 12),
    N(14L, "/fastcampus/services/n", 12),
    O(15L, "/fastcampus/services/o", 10),
    P(16L, "/fastcampus/services/p", 10),
    Q(17L, "/fastcampus/services/q", 10),
    R(18L, "/fastcampus/services/r", 10),
    S(19L, "/fastcampus/services/s", 10),
    T(20L, "/fastcampus/services/t", 10),
    U(11L, "/fastcampus/services/u", 10),
    V(12L, "/fastcampus/services/v", 10),
    W(13L, "/fastcampus/services/w", 19),
    X(14L, "/fastcampus/services/x", 19),
    Y(15L, "/fastcampus/services/y", 19),
    Z(16L, "/fastcampus/services/z", 19);

    private final Long id;
    private final String url;
    private final Integer fee;

    ServicePolicy(Long id, String url, Integer fee) {
        this.id = id;
        this.url = url;
        this.fee = fee;
    }

    public static ServicePolicy findByUrl(String url) {
        return Arrays.stream(values())
                .filter(it -> it.url.equals(url))
                .findFirst()
                .orElseThrow();
    }

    public static ServicePolicy findById(Long id) {
        return Arrays.stream(values())
                .filter(it -> it.id.equals(id))
                .findFirst()
                .orElseThrow();
    }
}
