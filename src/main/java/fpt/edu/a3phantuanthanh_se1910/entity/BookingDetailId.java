// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BookingDetailId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "BookingReservationID")
    private Integer bookingReservationId;

    @Column(name = "RoomID")
    private Integer roomId;

    public BookingDetailId() {
    }

    public BookingDetailId(Integer bookingReservationId, Integer roomId) {
        this.bookingReservationId = bookingReservationId;
        this.roomId = roomId;
    }

    public Integer getBookingReservationId() {
        return bookingReservationId;
    }

    public void setBookingReservationId(Integer bookingReservationId) {
        this.bookingReservationId = bookingReservationId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BookingDetailId that)) return false;
        return Objects.equals(bookingReservationId, that.bookingReservationId)
                && Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingReservationId, roomId);
    }
}
