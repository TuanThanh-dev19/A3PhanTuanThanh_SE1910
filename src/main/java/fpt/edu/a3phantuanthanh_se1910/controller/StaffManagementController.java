// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.controller;

import fpt.edu.a3phantuanthanh_se1910.dto.*;
import fpt.edu.a3phantuanthanh_se1910.service.StaffManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffManagementController {
    private final StaffManagementService service;
    public StaffManagementController(StaffManagementService service) { this.service=service; }

    @GetMapping("/customers")
    public List<CustomerResponse> customers(@RequestParam(defaultValue = "") String search) { return service.customers(search); }
    @GetMapping("/customers/{id}")
    public CustomerResponse customer(@PathVariable int id) { return CustomerResponse.from(service.findCustomer(id)); }
    @PostMapping("/customers") @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@Valid @RequestBody StaffCustomerRequest request) { return service.saveCustomer(null,request); }
    @PutMapping("/customers/{id}")
    public CustomerResponse updateCustomer(@PathVariable int id, @Valid @RequestBody StaffCustomerRequest request) { return service.saveCustomer(id,request); }
    @DeleteMapping("/customers/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable int id) { service.deleteCustomer(id); }

    @GetMapping("/rooms")
    public List<RoomResponse> rooms(@RequestParam(defaultValue = "") String search) { return service.rooms(search); }
    @GetMapping("/rooms/{id}")
    public RoomResponse room(@PathVariable int id) { return service.room(id); }
    @PostMapping("/rooms") @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody RoomRequest request) { return service.saveRoom(null,request); }
    @PutMapping("/rooms/{id}")
    public RoomResponse updateRoom(@PathVariable int id, @Valid @RequestBody RoomRequest request) { return service.saveRoom(id,request); }
    @DeleteMapping("/rooms/{id}")
    public Map<String,String> deleteRoom(@PathVariable int id) { return service.deleteRoom(id); }
}
