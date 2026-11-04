// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "RoomType", schema = "dbo")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomTypeID")
    private Integer roomTypeId;

    @Nationalized
    @Column(name = "RoomTypeName", length = 50, nullable = false)
    private String roomTypeName;

    @Nationalized
    @Column(name = "TypeDescription", length = 250)
    private String typeDescription;

    @Nationalized
    @Column(name = "TypeNote", length = 250)
    private String typeNote;

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getTypeNote() {
        return typeNote;
    }

    public void setTypeNote(String typeNote) {
        this.typeNote = typeNote;
    }
}
