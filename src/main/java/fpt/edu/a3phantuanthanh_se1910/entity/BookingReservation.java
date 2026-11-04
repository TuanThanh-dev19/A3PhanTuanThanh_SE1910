// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "BookingReservation", schema = "dbo")
public class BookingReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_sequence")
    @SequenceGenerator(name = "booking_sequence", sequenceName = "BookingReservationSequence", allocationSize = 1)
    @Column(name = "BookingReservationID")
    private Integer bookingReservationId;

    @Column(name = "BookingDate")
    private LocalDate bookingDate;

    @Column(name = "TotalPrice", columnDefinition = "money")
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", foreignKey = @ForeignKey(name = "FK_BookingReservation_Customer"))
    private Customer customer;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "BookingStatus")
    private Short bookingStatus;

    public Integer getBookingReservationId() {
        return bookingReservationId;
    }

    public void setBookingReservationId(Integer bookingReservationId) {
        this.bookingReservationId = bookingReservationId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Short getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(Short bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
