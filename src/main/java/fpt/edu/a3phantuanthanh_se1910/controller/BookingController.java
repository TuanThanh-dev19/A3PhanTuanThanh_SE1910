// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.controller;

import fpt.edu.a3phantuanthanh_se1910.dto.request.BookingRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.respone.BookingResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.request.BookingStatusRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.request.StaffBookingRequest;
import fpt.edu.a3phantuanthanh_se1910.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody BookingRequest request) {
        return service.create(Integer.parseInt(jwt.getSubject()), request);
    }

    @GetMapping("/bookings")
    public List<BookingResponse> history(@AuthenticationPrincipal Jwt jwt) {
        return service.history(Integer.parseInt(jwt.getSubject()));
    }

    @GetMapping("/bookings/{id}")
    public BookingResponse ownBooking(@AuthenticationPrincipal Jwt jwt, @PathVariable int id) {
        return service.ownBooking(Integer.parseInt(jwt.getSubject()), id);
    }

    @GetMapping("/staff/bookings")
    public List<BookingResponse> list(@RequestParam(defaultValue = "") String search) {
        return service.list(search);
    }

    @GetMapping("/staff/bookings/{id}")
    public BookingResponse get(@PathVariable int id) {
        return service.get(id);
    }

    @PostMapping("/staff/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createStaff(@Valid @RequestBody StaffBookingRequest request) {
        return service.saveStaff(null, request);
    }

    @PutMapping("/staff/bookings/{id}")
    public BookingResponse update(@PathVariable int id, @Valid @RequestBody StaffBookingRequest request) {
        return service.saveStaff(id, request);
    }

    @PatchMapping("/staff/bookings/{id}/status")
    public BookingResponse changeStatus(@PathVariable int id, @Valid @RequestBody BookingStatusRequest request) {
        return service.changeStatus(id, request.bookingStatus());
    }

    @DeleteMapping("/staff/bookings/{id}")
    public BookingResponse cancel(@PathVariable int id) {
        return service.changeStatus(id, (short) 2);
    }
}
