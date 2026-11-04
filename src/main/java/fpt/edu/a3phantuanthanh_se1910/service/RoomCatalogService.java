// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.service;

import fpt.edu.a3phantuanthanh_se1910.dto.respone.RoomResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.respone.RoomTypeResponse;
import fpt.edu.a3phantuanthanh_se1910.repository.RoomInformationRepository;
import fpt.edu.a3phantuanthanh_se1910.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomCatalogService {
    private final RoomInformationRepository rooms;
    private final RoomTypeRepository roomTypes;

    public RoomCatalogService(RoomInformationRepository rooms, RoomTypeRepository roomTypes) {
        this.rooms = rooms;
        this.roomTypes = roomTypes;
    }

    public List<RoomTypeResponse> listRoomTypes() {
        return roomTypes.findAllByOrderByRoomTypeIdAsc().stream()
                .map(type -> new RoomTypeResponse(type.getRoomTypeId(), type.getRoomTypeName(),
                        type.getTypeDescription(), type.getTypeNote()))
                .toList();
    }

    public List<RoomResponse> listRooms() {
        return rooms.findAllByOrderByRoomIdAsc().stream()
                .map(room -> new RoomResponse(room.getRoomId(), room.getRoomNumber(),
                        room.getRoomDetailDescription(), room.getRoomMaxCapacity(), room.getRoomStatus(),
                        room.getRoomPricePerDay(), room.getRoomType().getRoomTypeId(),
                        room.getRoomType().getRoomTypeName()))
                .toList();
    }
}
