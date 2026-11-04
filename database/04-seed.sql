-- Author: Phan Tuan Thanh - CE190595
USE [A3PhanTuanThanh_SE1910];
GO
SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF NOT EXISTS (SELECT 1 FROM dbo.RoomType WHERE RoomTypeName = N'Standard')
    INSERT dbo.RoomType (RoomTypeName, TypeDescription, TypeNote)
    VALUES (N'Standard', N'A comfortable room for two guests.', N'Breakfast is available separately.');
IF NOT EXISTS (SELECT 1 FROM dbo.RoomType WHERE RoomTypeName = N'Deluxe')
    INSERT dbo.RoomType (RoomTypeName, TypeDescription, TypeNote)
    VALUES (N'Deluxe', N'A spacious room for up to four guests.', N'Includes a seating area.');

IF NOT EXISTS (SELECT 1 FROM dbo.RoomInformation WHERE RoomNumber = N'101')
    INSERT dbo.RoomInformation (RoomNumber, RoomDetailDescription, RoomMaxCapacity, RoomTypeID, RoomStatus, RoomPricePerDay)
    SELECT N'101', N'Quiet standard room with a double bed.', 2, RoomTypeID, 1, 500000
    FROM dbo.RoomType WHERE RoomTypeName = N'Standard';
IF NOT EXISTS (SELECT 1 FROM dbo.RoomInformation WHERE RoomNumber = N'102')
    INSERT dbo.RoomInformation (RoomNumber, RoomDetailDescription, RoomMaxCapacity, RoomTypeID, RoomStatus, RoomPricePerDay)
    SELECT N'102', N'Bright standard room with twin beds.', 2, RoomTypeID, 1, 550000
    FROM dbo.RoomType WHERE RoomTypeName = N'Standard';
IF NOT EXISTS (SELECT 1 FROM dbo.RoomInformation WHERE RoomNumber = N'201')
    INSERT dbo.RoomInformation (RoomNumber, RoomDetailDescription, RoomMaxCapacity, RoomTypeID, RoomStatus, RoomPricePerDay)
    SELECT N'201', N'Deluxe family room with a seating area.', 4, RoomTypeID, 1, 900000
    FROM dbo.RoomType WHERE RoomTypeName = N'Deluxe';

COMMIT TRANSACTION;
GO
