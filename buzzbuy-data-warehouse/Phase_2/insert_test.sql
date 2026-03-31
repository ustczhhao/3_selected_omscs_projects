-- INSERT TEST DATA
-- 1 Manufacture ------------------------------------------------
INSERT INTO Manufacture(manufacture_name)
VALUES ('manuf1'),
    ('manuf2'),
    ('manuf3'),
    ('manuf4');

-- 2 Category ------------------------------------------------
INSERT INTO Category(category_name)
VALUES ('cat1'),
    ('cat2'),
    ('cat3');

-- 3 City ------------------------------------------------
INSERT INTO City(city_name, state, population)
VALUES ('New York City', 'New York', 20000000),
    ('Atlanta', 'Georgia', 7000000),
    ('Los Angeles', 'California', 10000000),
    ('Dollas', 'Texas', 2000000),
    ('San Francisco', 'California', 15000000),
    ('Orlando', 'Florida', 5000000);

-- 4 User ------------------------------------------------
INSERT INTO `User` (
        employeeID,
        last_name,
        last_ssn,
        first_name,
        title,
        can_view_audit_log,
        full_access
    )
VALUES (
        '0000001',
        'James',
        '2345',
        'Mary',
        'Director',
        1,
        1
    ),
    ('0000002', 'Lee', '9876', 'Tom', NULL, 0, 0),
    ('0000003', 'Max', '5642', 'Harry', NULL, 0, 0),
    ('0000004', 'Jimmy', '1929', 'Olival', NULL, 0, 1),
    ('0000005', 'Lucy', '3344', 'Lancy', NULL, 0, 0),
    ('0000006', 'Bob', '5540', 'Alex', NULL, 0, 0),
    ('0000007', 'Ted', '5432', 'Ian', NULL, 0, 0),
    ('0000008', 'Ruby', '9923', 'Liu', NULL, 0, 0);

-- 5 BusinessDay ------------------------------------------------
INSERT INTO BusinessDay (business_date)
VALUES ('2024-02-02'),
    ('2024-02-03'),
    ('2024-02-04'),
    ('2024-06-05'),
    ('2024-06-06'),
    ('2024-09-07'),
    ('2024-09-08');

-- 6 District ------------------------------------------------
INSERT INTO District (district_number)
VALUES ('500002'),
    ('400006'),
    ('700003'),
    ('200005'),
    ('600008'),
    ('900007');

-- 7 Product ------------------------------------------------
INSERT INTO Product (PID, pname, retail_price, manufacture_name)
VALUES ('A1B2C3D4', 'phone', 100, 'manuf1'),
    ('0000FF999', 'pen', 200.5, 'manuf2'),
    ('23EDFG1SD', 'book', 300.6, 'manuf3'),
    ('842GGirm', 'candy', 200, 'manuf2'),
    ('9asdfghBB', 'ipad', 80, 'manuf1'),
    ('ifas1123', 'noodles', 20, 'manuf4');

-- 8 ProductCategory ------------------------------------------------
INSERT INTO ProductCategory (PID, category_name)
VALUES ('A1B2C3D4', 'cat2'),
    ('A1B2C3D4', 'cat1'),
    ('0000FF999', 'cat3'),
    ('23EDFG1SD', 'cat3'),
    ('23EDFG1SD', 'cat1');

-- 9 Discount ------------------------------------------------
INSERT INTO Discount (PID, discount_price, business_date)
VALUES ('A1B2C3D4', 40, '2024-02-02'),
    ('A1B2C3D4', 50, '2024-09-07'),
    ('0000FF999', 100.8, '2024-06-06'),
    ('0000FF999', 90, '2024-09-07'),
    ('0000FF999', 85.2, '2024-09-08'),
    ('23EDFG1SD', 200, '2024-02-04');

-- 10 Store ------------------------------------------------
INSERT INTO Store (
        store_number,
        district_number,
        phone_number,
        city_name,
        state
    )
VALUES (
        '12344553',
        '500002',
        '77777777',
        'Atlanta',
        'Georgia'
    ),
    (
        '223142',
        '500002',
        '12345678',
        'Los Angeles',
        'California'
    ),
    (
        '3567654',
        '700003',
        '12341234',
        'Los Angeles',
        'California'
    ),
    (
        '4565432',
        '400006',
        '87878787',
        'Orlando',
        'Florida'
    );

-- 11 Sold ------------------------------------------------
INSERT INTO Sold (PID, store_number, business_date, quantity)
VALUES ('A1B2C3D4', '12344553', '2024-02-02', 2),
    ('0000FF999', '4565432', '2024-02-03', 400),
    ('23EDFG1SD', '4565432', '2024-02-04', 3),
    ('0000FF999', '3567654', '2024-06-05', 50),
    ('A1B2C3D4', '3567654', '2024-06-06', 60);

-- 12 DistrictPermission ------------------------------------------------
INSERT INTO DistrictPermission (employeeID, district_number)
VALUES ('0000001', '500002'),
    ('0000001', '400006'),
    ('0000001', '700003'),
    ('0000001', '200005'),
    ('0000001', '600008'),
    ('0000001', '900007'),
    ('0000002', '500002'),
    ('0000002', '400006'),
    ('0000003', '700003'),
    ('0000004', '500002'),
    ('0000004', '400006'),
    ('0000004', '700003'),
    ('0000004', '200005'),
    ('0000004', '600008'),
    ('0000004', '900007'),
    ('0000007', '700003'),
    ('0000007', '200005'),
    ('0000007', '900007');

-- 13 Holiday ------------------------------------------------
INSERT INTO Holiday (
        business_date,
        holiday_name,
        employeeID,
        date_added
    )
VALUES (
        '2024-02-04',
        'festival1',
        '0000001',
        '2024-02-03 15:00:20'
    ),
    (
        '2024-06-06',
        'festival2',
        '0000001',
        '2024-02-03 08:00:09'
    ),
    (
        '2024-09-08',
        'festival3',
        '0000002',
        '2024-06-07 07:15:40'
    );

-- 14 AuditLog ------------------------------------------------
INSERT INTO AuditLog (
        employeeID_view_report,
        time_stamp,
        report_name
    )
VALUES (
        '0000001',
        '2024-02-04 15:00:20',
        'report1'
    ),
    (
        '0000002',
        '2024-06-05 06:00:04',
        'report2'
    ),
    (
        '0000001',
        '2024-06-06 20:00:25',
        'report3'
    ),
    (
        '0000003',
        '2024-01-06 15:00:25',
        'report4'
    ),
    (
        '0000004',
        '2024-03-06 23:00:25',
        'report5'
    );

-- 15 ViewHoliday ------------------------------------------------
INSERT INTO ViewHoliday (business_date, employeeID)
VALUES ('2024-02-03', '0000001'),
    ('2024-02-04', '0000002'),
    ('2024-06-05', '0000003');
