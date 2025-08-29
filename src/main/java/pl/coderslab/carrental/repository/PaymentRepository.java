package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
