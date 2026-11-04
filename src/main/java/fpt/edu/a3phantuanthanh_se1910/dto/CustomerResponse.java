// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import fpt.edu.a3phantuanthanh_se1910.entity.Customer;
import java.time.LocalDate;

public record CustomerResponse(Integer customerId, String customerFullName, String telephone,
                               String emailAddress, LocalDate customerBirthday, Short customerStatus) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getCustomerId(), customer.getCustomerFullName(),
                customer.getTelephone(), customer.getEmailAddress(), customer.getCustomerBirthday(),
                customer.getCustomerStatus());
    }
}
