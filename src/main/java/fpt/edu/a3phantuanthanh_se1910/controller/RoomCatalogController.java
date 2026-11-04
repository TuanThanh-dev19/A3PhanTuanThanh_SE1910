// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.controller;

import fpt.edu.a3phantuanthanh_se1910.dto.respone.RoomResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.respone.RoomTypeResponse;
import fpt.edu.a3phantuanthanh_se1910.service.RoomCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomCatalogController {
    private final RoomCatalogService service;

    public RoomCatalogController(RoomCatalogService service) {
        this.service = service;
    }

    @GetMapping("/rooms")
    public List<RoomResponse> listRooms() {
        return service.listRooms();
    }

    @GetMapping("/room-types")
    public List<RoomTypeResponse> listRoomTypes() {
        return service.listRoomTypes();
    }
}
