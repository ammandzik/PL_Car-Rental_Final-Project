package pl.coderslab.carrental.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String invoiceNumber;
    private LocalDate issueDate;
    private Double totalAmount;
    @OneToOne
    private Reservation reservation;
    @ManyToOne
    private User user;
}
