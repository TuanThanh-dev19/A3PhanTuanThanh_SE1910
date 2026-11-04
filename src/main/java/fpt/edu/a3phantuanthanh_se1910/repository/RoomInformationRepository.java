// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.repository;

import fpt.edu.a3phantuanthanh_se1910.entity.RoomInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomInformationRepository extends JpaRepository<RoomInformation, Integer> {
    List<RoomInformation> findAllByOrderByRoomIdAsc();
}
