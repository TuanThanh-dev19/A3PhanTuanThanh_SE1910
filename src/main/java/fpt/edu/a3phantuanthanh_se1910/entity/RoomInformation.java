// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;

@Entity
@Table(name = "RoomInformation", schema = "dbo",
        uniqueConstraints = @UniqueConstraint(name = "UQ_RoomInformation_RoomNumber", columnNames = "RoomNumber"))
public class RoomInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomID")
    private Integer roomId;

    @Nationalized
    @Column(name = "RoomNumber", length = 50, nullable = false)
    private String roomNumber;

    @Nationalized
    @Column(name = "RoomDetailDescription", length = 220)
    private String roomDetailDescription;

    @Column(name = "RoomMaxCapacity")
    private Integer roomMaxCapacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomTypeID", foreignKey = @ForeignKey(name = "FK_RoomInformation_RoomType"))
    private RoomType roomType;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "RoomStatus")
    private Short roomStatus;

    @Column(name = "RoomPricePerDay", columnDefinition = "money")
    private BigDecimal roomPricePerDay;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomDetailDescription() {
        return roomDetailDescription;
    }

    public void setRoomDetailDescription(String roomDetailDescription) {
        this.roomDetailDescription = roomDetailDescription;
    }

    public Integer getRoomMaxCapacity() {
        return roomMaxCapacity;
    }

    public void setRoomMaxCapacity(Integer roomMaxCapacity) {
        this.roomMaxCapacity = roomMaxCapacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Short getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(Short roomStatus) {
        this.roomStatus = roomStatus;
    }

    public BigDecimal getRoomPricePerDay() {
        return roomPricePerDay;
    }

    public void setRoomPricePerDay(BigDecimal roomPricePerDay) {
        this.roomPricePerDay = roomPricePerDay;
    }
}
