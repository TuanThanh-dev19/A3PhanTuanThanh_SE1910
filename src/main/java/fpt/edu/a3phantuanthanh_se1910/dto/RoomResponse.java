// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import java.math.BigDecimal;

public record RoomResponse(Integer roomId, String roomNumber, String roomDetailDescription,
                           Integer roomMaxCapacity, Short roomStatus, BigDecimal roomPricePerDay,
                           Integer roomTypeId, String roomTypeName) {
}
