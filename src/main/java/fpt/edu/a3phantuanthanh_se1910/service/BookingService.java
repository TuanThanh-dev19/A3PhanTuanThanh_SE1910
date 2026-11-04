// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.service;

import fpt.edu.a3phantuanthanh_se1910.dto.respone.BookingDetailResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.request.BookingRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.respone.BookingResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.request.BookingRoomRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.request.StaffBookingRequest;
import fpt.edu.a3phantuanthanh_se1910.entity.*;
import fpt.edu.a3phantuanthanh_se1910.exception.ApiException;
import fpt.edu.a3phantuanthanh_se1910.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class BookingService {
    private final BookingReservationRepository bookings;
    private final BookingDetailRepository details;
    private final RoomInformationRepository rooms;
    private final AuthService auth;

    public BookingService(BookingReservationRepository bookings, BookingDetailRepository details,
                          RoomInformationRepository rooms, AuthService auth) {
        this.bookings = bookings;
        this.details = details;
        this.rooms = rooms;
        this.auth = auth;
    }

    public BookingResponse create(int customerId, BookingRequest request) {
        return save(null, customerId, (short) 1, request.rooms());
    }

    public BookingResponse saveStaff(Integer id, StaffBookingRequest request) {
        return save(id, request.customerId(), request.bookingStatus(), request.rooms());
    }

    private BookingResponse save(Integer id, int customerId, short status, List<BookingRoomRequest> requested) {
        Customer customer = auth.requireCustomer(customerId);
        BookingReservation booking = id == null ? new BookingReservation() : lockedBooking(id);
        List<BookingDetail> old = id == null ? List.of() : details.findByBookingReservationBookingReservationIdOrderByRoomRoomIdAsc(id);
        Set<Integer> requestedIds = new HashSet<>();
        for (BookingRoomRequest room : requested) {
            if (!requestedIds.add(room.roomId())) throw bad("A room may appear only once in a booking.");
            if (room.startDate().isBefore(LocalDate.now()) || !room.endDate().isAfter(room.startDate()) || room.endDate().getYear() > 9999) {
                throw bad("Stay dates must start today or later, and checkout must be after check-in (year 9999 or earlier).");
            }
        }
        // Lock every affected room in ascending ID order to serialize availability checks.
        Set<Integer> affected = new TreeSet<>(requestedIds);
        old.forEach(detail -> affected.add(detail.getRoom().getRoomId()));
        Map<Integer, RoomInformation> locked = new HashMap<>();
        for (int roomId : affected)
            locked.put(roomId, rooms.findLockedById(roomId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Room not found: " + roomId)));
        BigDecimal total = BigDecimal.ZERO;
        for (BookingRoomRequest room : requested) {
            RoomInformation stored = locked.get(room.roomId());
            if (status == 1) validateAvailability(stored, room.startDate(), room.endDate(), id == null ? -1 : id);
            if (stored.getRoomPricePerDay() == null || stored.getRoomPricePerDay().signum() <= 0)
                throw bad("A selected room has no valid price.");
            total = total.add(stored.getRoomPricePerDay().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(room.startDate(), room.endDate()))));
        }
        if (total.compareTo(new BigDecimal("922337203685477.5807")) > 0)
            throw bad("The booking total exceeds the supported money range.");
        booking.setCustomer(customer);
        booking.setBookingStatus(status);
        booking.setTotalPrice(total);
        if (id == null) booking.setBookingDate(LocalDate.now());
        bookings.saveAndFlush(booking);
        Map<Integer, BookingDetail> existing = new HashMap<>();
        old.forEach(detail -> existing.put(detail.getRoom().getRoomId(), detail));
        for (BookingDetail detail : old)
            if (!requestedIds.contains(detail.getRoom().getRoomId())) details.delete(detail);
        for (BookingRoomRequest room : requested) {
            BookingDetail detail = existing.getOrDefault(room.roomId(), new BookingDetail());
            detail.setId(new BookingDetailId(booking.getBookingReservationId(), room.roomId()));
            detail.setBookingReservation(booking);
            detail.setRoom(locked.get(room.roomId()));
            detail.setStartDate(room.startDate());
            detail.setEndDate(room.endDate());
            detail.setActualPrice(locked.get(room.roomId()).getRoomPricePerDay());
            details.save(detail);
        }
        details.flush();
        return response(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> history(int customerId) {
        auth.requireCustomer(customerId);
        return bookings.findByCustomerCustomerIdOrderByBookingReservationIdDesc(customerId).stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse ownBooking(int customerId, int id) {
        auth.requireCustomer(customerId);
        BookingReservation booking = bookings.findById(id).filter(b -> b.getCustomer().getCustomerId() == customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found."));
        return response(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> list(String search) {
        if (search.length() > 100) throw bad("Search must not exceed 100 characters.");
        String term = search.toLowerCase(Locale.ROOT);
        return bookings.findAllByOrderByBookingReservationIdDesc().stream()
                .filter(b -> b.getBookingReservationId().toString().contains(term)
                        || b.getCustomer().getEmailAddress().toLowerCase(Locale.ROOT).contains(term)
                        || Optional.ofNullable(b.getCustomer().getCustomerFullName()).orElse("").toLowerCase(Locale.ROOT).contains(term))
                .map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse get(int id) {
        return response(bookings.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found.")));
    }

    public BookingResponse changeStatus(int id, short status) {
        BookingReservation booking = lockedBooking(id);
        List<BookingDetail> stored = details.findByBookingReservationBookingReservationIdOrderByRoomRoomIdAsc(id);
        for (BookingDetail detail : stored) {
            RoomInformation room = rooms.findLockedById(detail.getRoom().getRoomId()).orElseThrow();
            if (status == 1) validateAvailability(room, detail.getStartDate(), detail.getEndDate(), id);
        }
        booking.setBookingStatus(status);
        bookings.saveAndFlush(booking);
        return response(booking);
    }

    private void validateAvailability(RoomInformation room, LocalDate start, LocalDate end, int excludedId) {
        if (!Short.valueOf((short) 1).equals(room.getRoomStatus()))
            throw new ApiException(HttpStatus.CONFLICT, "Room " + room.getRoomNumber() + " is inactive.");
        if (details.countOverlaps(room.getRoomId(), start, end, excludedId) > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Room " + room.getRoomNumber() + " is already booked for those dates.");
        }
    }

    private BookingReservation lockedBooking(int id) {
        return bookings.findLockedById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found."));
    }

    private BookingResponse response(BookingReservation booking) {
        List<BookingDetailResponse> lines = details.findByBookingReservationBookingReservationIdOrderByRoomRoomIdAsc(booking.getBookingReservationId())
                .stream().map(d -> new BookingDetailResponse(d.getRoom().getRoomId(), d.getRoom().getRoomNumber(), d.getStartDate(), d.getEndDate(),
                        d.getActualPrice(), d.getActualPrice().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(d.getStartDate(), d.getEndDate()))))).toList();
        Customer customer = booking.getCustomer();
        return new BookingResponse(booking.getBookingReservationId(), booking.getBookingDate(), booking.getTotalPrice(), customer.getCustomerId(),
                customer.getCustomerFullName(), customer.getEmailAddress(), booking.getBookingStatus(), lines);
    }

    private ApiException bad(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }
}
