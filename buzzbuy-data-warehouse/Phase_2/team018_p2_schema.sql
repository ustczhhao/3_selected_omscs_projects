-- add CREATE DATABASE and USE database queries
DROP DATABASE IF EXISTS cs6400_phase2;

CREATE DATABASE cs6400_phase2;

USE cs6400_phase2;

SET SESSION sql_mode = 'PAD_CHAR_TO_FULL_LENGTH';

-- ------------------------------------------------------------------------------------- --
-- ------------------------------------------------------------------------------------- --
-- CREATE TABLE
-- 0 Report ------------------------------------------------
CREATE TABLE Report (
    report_name varchar(250) NOT NULL,
    PRIMARY KEY(report_name)
);

-- CREATE TABLE
-- 1 Manufacture ------------------------------------------------
CREATE TABLE Manufacture (
    manufacture_name varchar(250) NOT NULL,
    PRIMARY KEY(manufacture_name)
);

-- 2 Category ------------------------------------------------
CREATE TABLE Category (
    category_name varchar(250) NOT NULL,
    PRIMARY KEY(category_name)
);

-- 3 City ------------------------------------------------
CREATE TABLE City (
    city_name varchar(50) NOT NULL,
    state varchar(50) NOT NULL,
    population INT NOT NULL,
    PRIMARY KEY(city_name, state),
    -- add a KEY to City
    KEY state (state)
);

-- 4 User ------------------------------------------------
CREATE TABLE `User` (
    employeeID char(7) NOT NULL,
    last_name varchar(50) NOT NULL,
    last_ssn char(4) NOT NULL,
    first_name varchar(50) NOT NULL,
    title varchar(50),
    can_view_audit_log boolean NOT NULL,
    districts_assigned VARCHAR(255) NOT NULL,
    PRIMARY KEY(employeeID)
);

-- 5 BusinessDay ------------------------------------------------
CREATE TABLE BusinessDay (
    business_date date NOT NULL,
    PRIMARY KEY (business_date)
);

-- 6 District ------------------------------------------------
CREATE TABLE District (
    district_number char(12) NOT NULL,
    PRIMARY KEY(district_number)
);

-- 7 Product ------------------------------------------------
CREATE TABLE Product (
    PID varchar(12) NOT NULL,
    pname varchar(250) NOT NULL,
    retail_price float NOT NULL,
    manufacture_name varchar(250) NOT NULL,
    PRIMARY KEY(PID)
);

-- 8 ProductCategory ------------------------------------------------
CREATE TABLE ProductCategory (
    PID varchar(12) NOT NULL,
    category_name varchar(250) NOT NULL,
    PRIMARY KEY(PID, category_name),
    FOREIGN KEY (PID) REFERENCES product(PID),
    FOREIGN KEY (category_name) REFERENCES category(category_name)
);

-- 9 Discount ------------------------------------------------
CREATE TABLE Discount (
    PID varchar(12) NOT NULL,
    business_date date NOT NULL,
    discount_price float NOT NULL,
    PRIMARY KEY(PID, business_date)
);

-- 10 Store ------------------------------------------------
CREATE TABLE Store (
    store_number varchar(10) NOT NULL,
    -- todo: check store_number type
    district_number char(12) NOT NULL,
    phone_number varchar(10) NOT NULL,
    city_name varchar(50) NOT NULL,
    state varchar(50) NOT NULL,
    PRIMARY KEY(store_number)
);

-- 11 Sold ------------------------------------------------
CREATE TABLE Sold (
    PID varchar(12) NOT NULL,
    store_number varchar(10) NOT NULL,
    business_date date NOT NULL,
    quantity int NOT NULL,
    PRIMARY KEY(PID, store_number, business_date)
);

-- (Deleted) 12 DistrictPermission ------------------------------------------------
-- CREATE TABLE DistrictPermission (
--     employeeID varchar(7) NOT NULL,
--     district_number char(12) NOT NULL,
--     PRIMARY KEY(employeeID, district_number)
-- );
-- 13 Holiday ------------------------------------------------
CREATE TABLE Holiday (
    business_date date NOT NULL,
    holiday_name varchar(50) NOT NULL,
    employeeID varchar(7) NOT NULL,
    date_added datetime NULL,
    PRIMARY KEY (business_date)
);

-- 14 AuditLog ------------------------------------------------
CREATE TABLE AuditLog (
    employeeID_view_report varchar(7) NOT NULL,
    time_stamp datetime NOT NULL,
    report_name varchar(50) NOT NULL,
    PRIMARY KEY (employeeID_VIEW_REPORT, time_stamp)
);

-- (Deleted) 15 ViewHoliday ------------------------------------------------
-- CREATE TABLE ViewHoliday (
--    business_date date NOT NULL,
--    employeeID varchar(7) NOT NULL,
--    PRIMARY KEY (business_date, employeeID)
-- );
-- ------------------------------------------------------------------------------------- --
-- ------------------------------------------------------------------------------------- --
-- CONSTRAINTS
-- 1 Manufacture ------------------------------------------------
-- No constraints required
-- 2 Category ------------------------------------------------
-- No constraints required
-- 3 City ------------------------------------------------
ALTER TABLE City
ADD CONSTRAINT chk_state CHECK (
        state IN (
            'Alabama',
            'Alaska',
            'Arizona',
            'Arkansas',
            'California',
            'Colorado',
            'Connecticut',
            'Delaware',
            'Florida',
            'Georgia',
            'Hawaii',
            'Idaho',
            'Illinois',
            'Indiana',
            'Iowa',
            'Kansas',
            'Kentucky',
            'Louisiana',
            'Maine',
            'Maryland',
            'Massachusetts',
            'Michigan',
            'Minnesota',
            'Mississippi',
            'Missouri',
            'Montana',
            'Nebraska',
            'Nevada',
            'New Hampshire',
            'New Jersey',
            'New Mexico',
            'New York',
            'North Carolina',
            'North Dakota',
            'Ohio',
            'Oklahoma',
            'Oregon',
            'Pennsylvania',
            'Rhode Island',
            'South Carolina',
            'South Dakota',
            'Tennessee',
            'Texas',
            'Utah',
            'Vermont',
            'Virginia',
            'Washington',
            'West Virginia',
            'Wisconsin',
            'Wyoming',
            'District of Columbia'
        )
    );

ALTER TABLE City
ADD CONSTRAINT chk_population CHECK(
        population > 0
        and population = ROUND(population, 0)
    );

-- 4 User ------------------------------------------------
-- No constraints required
-- 5 BusinessDay ------------------------------------------------
-- No constraints required
-- 6 District ------------------------------------------------
-- -- no constraints for District---
-- 7 Product ------------------------------------------------
ALTER TABLE Product
ADD CONSTRAINT fk_Product_manufacture_name_Manufacture_manufacture_name FOREIGN KEY (manufacture_name) REFERENCES `Manufacture` (manufacture_name);

-- 8 ProductCategory ------------------------------------------------
ALTER TABLE ProductCategory
ADD CONSTRAINT fk_ProductCategory_PID_Product_PID FOREIGN KEY (PID) REFERENCES `Product` (PID);

ALTER TABLE ProductCategory
ADD CONSTRAINT fk_ProductCategory_category_name_Category_category_name FOREIGN KEY (category_name) REFERENCES `Category` (category_name);

-- 9 Discount ------------------------------------------------
ALTER TABLE Discount
ADD CONSTRAINT fk_Discount_PID_Product_PID FOREIGN KEY (PID) REFERENCES `Product` (PID);

ALTER TABLE Discount
ADD CONSTRAINT fk_Discount_business_date_BusinessDay_business_date FOREIGN KEY (business_date) REFERENCES `BusinessDay` (business_date);

-- 10 Store ------------------------------------------------
ALTER TABLE Store
ADD CONSTRAINT fk_Store_district_num_District_district_num FOREIGN KEY (district_number) REFERENCES `District` (district_number);

ALTER TABLE Store
ADD CONSTRAINT fk_Store_city_name_City_city_name FOREIGN KEY (city_name) REFERENCES `City` (city_name);

-- add a KEY to City
ALTER TABLE Store
ADD CONSTRAINT fk_Store_state_City_state FOREIGN KEY (state) REFERENCES `City` (state);

-- 11 Sold ------------------------------------------------
ALTER TABLE Sold
ADD CONSTRAINT fk_Sold_PID_Product_email FOREIGN KEY (PID) REFERENCES `Product` (PID);

ALTER TABLE Sold
ADD CONSTRAINT fk_Sold_business_date_BusinessDay_business_date FOREIGN KEY (business_date) REFERENCES `BusinessDay` (business_date);

ALTER TABLE Sold
ADD CONSTRAINT fk_Sold_store_number_Store_store_number FOREIGN KEY (store_number) REFERENCES `Store` (store_number);

-- (Deleted) 12 DistrictPermission ------------------------------------------------
-- ALTER TABLE DistrictPermission
-- ADD CONSTRAINT fk_DP_employeeID_User_employeeID FOREIGN KEY (employeeID) REFERENCES `User` (employeeID);
-- ALTER TABLE DistrictPermission
-- ADD CONSTRAINT fk_DP_district_num_User_district_num FOREIGN KEY (district_number) REFERENCES `District` (district_number);
-- 13 Holiday ------------------------------------------------
ALTER TABLE Holiday
ADD CONSTRAINT fk_Holiday_business_date_BusinessDay_business_date FOREIGN KEY (business_date) REFERENCES BusinessDay (business_date);

-- 14 AuditLog ------------------------------------------------
ALTER TABLE AuditLog
ADD CONSTRAINT fk_AuditLog_User_employeeID FOREIGN KEY (employeeID_VIEW_REPORT) REFERENCES `User` (employeeID);

ALTER TABLE AuditLog
ADD CONSTRAINT fk_AuditLog_Report_report_name FOREIGN KEY (report_name) REFERENCES `Report` (report_name);

-- (Deleted) 15 ViewHoliday ------------------------------------------------
-- ALTER TABLE ViewHoliday
-- ADD CONSTRAINT fk_VH_business_date_BusinessDay_business_date FOREIGN KEY (business_date) REFERENCES `BusinessDay` (business_date);
-- ALTER TABLE ViewHoliday
-- ADD CONSTRAINT fk_VH_employeeID_User_employeeID FOREIGN KEY (employeeID) REFERENCES `User` (employeeID);
-- ------------------------------------------------------------------------------------- --
-- ------------------------------------------------------------------------------------- --