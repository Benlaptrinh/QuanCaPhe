-- Migration: add DA_GOP enum value to hoa_don.trang_thai
-- Run by Flyway if configured, or execute manually against your MySQL/MariaDB server.
ALTER TABLE hoa_don
  MODIFY COLUMN trang_thai ENUM('MOI_TAO','DA_THANH_TOAN','HUY','DA_GOP') NOT NULL DEFAULT 'MOI_TAO';


