// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "BookingDetail", schema = "dbo")
public class BookingDetail {

    @EmbeddedId
    private BookingDetailId id;

    @MapsId("bookingReservationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BookingReservationID", foreignKey = @ForeignKey(name = "FK_BookingDetail_BookingReservation"))
    private BookingReservation bookingReservation;

    @MapsId("roomId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomID", foreignKey = @ForeignKey(name = "FK_BookingDetail_RoomInformation"))
    private RoomInformation room;

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "ActualPrice", columnDefinition = "money")
    private BigDecimal actualPrice;

    public BookingDetailId getId() {
        return id;
    }

    public void setId(BookingDetailId id) {
        this.id = id;
    }

    public BookingReservation getBookingReservation() {
        return bookingReservation;
    }

    public void setBookingReservation(BookingReservation bookingReservation) {
        this.bookingReservation = bookingReservation;
    }

    public RoomInformation getRoom() {
        return room;
    }

    public void setRoom(RoomInformation room) {
        this.room = room;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }
}
