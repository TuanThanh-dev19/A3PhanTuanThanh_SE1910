// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.repository;

import fpt.edu.a3phantuanthanh_se1910.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmailAddressIgnoreCase(String emailAddress);
    boolean existsByEmailAddressIgnoreCase(String emailAddress);
}
