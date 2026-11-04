// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import java.math.BigDecimal;
import fpt.edu.a3phantuanthanh_se1910.entity.RoomInformation;

public record RoomResponse(Integer roomId, String roomNumber, String roomDetailDescription,
                           Integer roomMaxCapacity, Short roomStatus, BigDecimal roomPricePerDay,
                           Integer roomTypeId, String roomTypeName) {
    public static RoomResponse from(RoomInformation room) {
        return new RoomResponse(room.getRoomId(), room.getRoomNumber(), room.getRoomDetailDescription(),
                room.getRoomMaxCapacity(), room.getRoomStatus(), room.getRoomPricePerDay(),
                room.getRoomType().getRoomTypeId(), room.getRoomType().getRoomTypeName());
    }
}
