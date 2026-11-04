// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.service;

import fpt.edu.a3phantuanthanh_se1910.dto.respone.CustomerResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.request.RoomRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.respone.RoomResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.request.StaffCustomerRequest;
import fpt.edu.a3phantuanthanh_se1910.entity.*;
import fpt.edu.a3phantuanthanh_se1910.exception.ApiException;
import fpt.edu.a3phantuanthanh_se1910.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StaffManagementService {
    private final CustomerRepository customers;
    private final RoomInformationRepository rooms;
    private final RoomTypeRepository types;
    private final BookingReservationRepository bookings;
    private final BookingDetailRepository details;
    private final AuthService auth;

    public StaffManagementService(CustomerRepository customers, RoomInformationRepository rooms,
            RoomTypeRepository types, BookingReservationRepository bookings, BookingDetailRepository details, AuthService auth) {
        this.customers=customers; this.rooms=rooms; this.types=types;
        this.bookings=bookings; this.details=details; this.auth=auth;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> customers(String search) {
        checkSearch(search);
        return customers.findByCustomerFullNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCaseOrderByCustomerIdAsc(search,search)
                .stream().map(CustomerResponse::from).toList();
    }

    public Customer findCustomer(int id) {
        return customers.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Customer not found."));
    }

    public CustomerResponse saveCustomer(Integer id, StaffCustomerRequest request) {
        Customer customer=id==null ? new Customer() : findCustomer(id);
        if (id==null && request.password()==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Password is required for a new customer.");
        auth.applyProfile(customer, request.profile());
        if (request.password()!=null) customer.setPassword(auth.encodePassword(request.password()));
        customer.setCustomerStatus(request.customerStatus());
        return CustomerResponse.from(customers.saveAndFlush(customer));
    }

    public void deleteCustomer(int id) {
        Customer customer=findCustomer(id);
        if (bookings.existsByCustomerCustomerId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,"This customer has booking history. Deactivate the account instead.");
        }
        customers.delete(customer);
        customers.flush();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> rooms(String search) {
        checkSearch(search);
        return rooms.findByRoomNumberContainingIgnoreCaseOrRoomDetailDescriptionContainingIgnoreCaseOrderByRoomIdAsc(search,search)
                .stream().map(RoomResponse::from).toList();
    }

    public RoomResponse room(int id) {
        return RoomResponse.from(rooms.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Room not found.")));
    }

    public RoomResponse saveRoom(Integer id, RoomRequest request) {
        RoomInformation room=id==null ? new RoomInformation() : rooms.findLockedById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Room not found."));
        String number=request.roomNumber().strip();
        if (rooms.findByRoomNumberIgnoreCase(number).filter(existing -> !existing.getRoomId().equals(id)).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT,"This room number is already in use.");
        }
        RoomType type=types.findById(request.roomTypeId()).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,"Room type not found."));
        room.setRoomNumber(number); room.setRoomDetailDescription(request.roomDetailDescription());
        room.setRoomMaxCapacity(request.roomMaxCapacity()); room.setRoomType(type);
        room.setRoomStatus(request.roomStatus()); room.setRoomPricePerDay(request.roomPricePerDay());
        return RoomResponse.from(rooms.saveAndFlush(room));
    }

    public Map<String,String> deleteRoom(int id) {
        RoomInformation room=rooms.findLockedById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Room not found."));
        if (details.existsByRoomRoomId(id)) {
            room.setRoomStatus((short) 2);
            rooms.saveAndFlush(room);
            return Map.of("message","Room deactivated because it has booking history.");
        }
        rooms.delete(room); rooms.flush();
        return Map.of("message","Room deleted.");
    }

    private void checkSearch(String search) {
        if (search.length()>100) throw new ApiException(HttpStatus.BAD_REQUEST,"Search must not exceed 100 characters.");
    }
}
