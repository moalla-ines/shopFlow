package com.example.shopFlow.entity;

import com.example.shopFlow.entity.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false)
    private Double valeur;

    private LocalDateTime dateExpiration;

    private Integer usagesMax;

    @Column(nullable = false)
    private Integer usagesActuels = 0;

    @Column(nullable = false)
    private boolean actif = true;
}