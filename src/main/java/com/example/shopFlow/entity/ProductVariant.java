package com.example.shopFlow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String attribut;   // ex: Taille, Couleur
    private String valeur;     // ex: M, Rouge

    @Column(nullable = false)
    private Integer stockSupplementaire = 0;

    private Double prixDelta;  // différence de prix par rapport au produit
}