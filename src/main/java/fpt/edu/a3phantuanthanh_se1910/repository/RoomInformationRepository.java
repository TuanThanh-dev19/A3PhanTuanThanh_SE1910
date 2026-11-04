// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.repository;

import fpt.edu.a3phantuanthanh_se1910.entity.RoomInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomInformationRepository extends JpaRepository<RoomInformation, Integer> {
    List<RoomInformation> findByRoomNumberContainingIgnoreCaseOrRoomDetailDescriptionContainingIgnoreCaseOrderByRoomIdAsc(String number, String description);
    Optional<RoomInformation> findByRoomNumberIgnoreCase(String roomNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RoomInformation r where r.roomId = :id")
    Optional<RoomInformation> findLockedById(@Param("id") Integer id);

    List<RoomInformation> findAllByOrderByRoomIdAsc();
}
