-- Author: Phan Tuan Thanh - CE190595
USE [A3PhanTuanThanh_SE1910];
GO
SET XACT_ABORT ON;
BEGIN TRANSACTION;

ALTER TABLE dbo.Customer ALTER COLUMN [Password] nvarchar(255) NULL;

IF OBJECT_ID(N'dbo.BookingReservationSequence', N'SO') IS NULL
BEGIN
    IF EXISTS (SELECT 1 FROM dbo.BookingReservation)
        THROW 50001, 'Existing bookings require a reviewed sequence starting value before migration.', 1;
    EXEC(N'CREATE SEQUENCE dbo.BookingReservationSequence AS int START WITH 1 INCREMENT BY 1');
END;

-- Preserve history by rejecting parent deletion instead of cascading it.
ALTER TABLE dbo.RoomInformation DROP CONSTRAINT FK_RoomInformation_RoomType;
ALTER TABLE dbo.RoomInformation ADD CONSTRAINT FK_RoomInformation_RoomType
    FOREIGN KEY (RoomTypeID) REFERENCES dbo.RoomType(RoomTypeID);
ALTER TABLE dbo.BookingReservation DROP CONSTRAINT FK_BookingReservation_Customer;
ALTER TABLE dbo.BookingReservation ADD CONSTRAINT FK_BookingReservation_Customer
    FOREIGN KEY (CustomerID) REFERENCES dbo.Customer(CustomerID);
ALTER TABLE dbo.BookingDetail DROP CONSTRAINT FK_BookingDetail_RoomInformation;
ALTER TABLE dbo.BookingDetail ADD CONSTRAINT FK_BookingDetail_RoomInformation
    FOREIGN KEY (RoomID) REFERENCES dbo.RoomInformation(RoomID);
ALTER TABLE dbo.BookingDetail DROP CONSTRAINT FK_BookingDetail_BookingReservation;
ALTER TABLE dbo.BookingDetail ADD CONSTRAINT FK_BookingDetail_BookingReservation
    FOREIGN KEY (BookingReservationID) REFERENCES dbo.BookingReservation(BookingReservationID);

COMMIT TRANSACTION;
GO
