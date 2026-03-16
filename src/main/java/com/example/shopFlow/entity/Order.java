package com.example.shopFlow.entity;

import com.example.shopFlow.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Users customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus statut;

    @Column(unique = true, nullable = false)
    private String numeroCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address adresseLivraison;

    private Double sousTotal;
    private Double fraisLivraison;
    private Double totalTTC;

    private LocalDateTime dateCommande;
    @Column(nullable = false)
    private boolean isNew = true;  // flag notification changement statut

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> lignes;

    @PrePersist
    public void prePersist() {
        dateCommande = LocalDateTime.now();
        statut = OrderStatus.PENDING;
    }
}