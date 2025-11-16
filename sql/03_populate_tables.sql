/* 
  CPS510
  E-commerce Database Schema (Population Phase)
  Authors: Basmulla Atekulla, Michelle & Rochelle
  ------------------------------------------------
  Purpose: Inserts initial data for all entities.
  ------------------------------------------------
*/

PROMPT ===============================================================
PROMPT   📦 POPULATING DATABASE TABLES
PROMPT ===============================================================

------------------------------------------------------------
-- CUSTOMER DATA
------------------------------------------------------------
INSERT INTO Customer (CustomerID, Name, Email, Phone, Address)
VALUES (1, 'Alex Shopper', 'alex@example.com', '555-0100', '123 King St');

------------------------------------------------------------
-- STAFF DATA (Superclass + Subtypes)
------------------------------------------------------------
INSERT INTO Staff (StaffID, Name, Role, Email)
VALUES (10, 'Alice Morgan', 'Manager', 'alice.morgan@example.com');

INSERT INTO Staff (StaffID, Name, Role, Email)
VALUES (100, 'Jane Doe', 'Processor', 'jane.doe@example.com');

INSERT INTO Staff (StaffID, Name, Role, Email)
VALUES (200, 'Jay Garrick', 'Shipper', 'jay.garrick@example.com');

-- Manager
INSERT INTO Manager (StaffID, OfficeNumber, Salary, YearsExperience)
VALUES (10, 150, 140000, 25);

-- Processor
INSERT INTO Processor (StaffID, ProcessingStation, ShiftHours)
VALUES (100, 'Station A', '09:00-17:00');

-- Shipper
INSERT INTO Shipper (StaffID, VehicleAssigned, ShiftHours)
VALUES (200, 'Cargo Ship', '08:00-20:00');

------------------------------------------------------------
-- PRODUCT DATA (Superclass + Subtypes)
------------------------------------------------------------
-- Book
INSERT INTO Product (ProductID, Name, Description, Price, Brand, StockQuantity, IsActive, StaffID)
VALUES (25, 'Data Structures Textbook', 'University-level CS textbook', 59.99, 'Pearson', 20, 'Y', 10);

INSERT INTO Books(ProductID, ISBN, Author, Publisher)
VALUES (25, '978-0-061-96436-7', 'Chris Nolan', 'Pearson Education');

-- Clothing
INSERT INTO Product (ProductID, Name, Description, Price, Brand, StockQuantity, IsActive, StaffID)
VALUES (26, 'Flannel Sweater', 'Red button-down flannel sweater with hoodie', 19.99, 'American Eagle', 10, 'Y', 10);

INSERT INTO Clothing(ProductID, SizeLabel, Material, GenderCategory)
VALUES (26, 'M', 'Cotton', 'Unisex');

-- Electronics
INSERT INTO Product (ProductID, Name, Description, Price, Brand, StockQuantity, IsActive, StaffID)
VALUES (27, 'Lenovo Tab M11', 'Lightweight and durable 11-inch Android tablet', 299.99, 'Lenovo', 5, 'Y', 10);

INSERT INTO Electronics(ProductID, WarrantyPeriod, PowerRating)
VALUES (27, '5 years', '4.5/5.0');

------------------------------------------------------------
-- ORDER + DETAILS + PAYMENT + SHIPPING DATA
------------------------------------------------------------
-- Create Order
INSERT INTO Orders (OrderID, CustomerID, OrderDate, Status, OrderTotal, StaffID)
VALUES (1001, 1, SYSDATE, 'PENDING', 0, 10);

-- Order Details
INSERT INTO OrderDetails (OrderDetailID, OrderID, ProductID, Quantity, PurchasePrice)
SELECT 1, 1001, p.ProductID, 2, p.Price FROM Product p WHERE p.ProductID = 25;

INSERT INTO OrderDetails (OrderDetailID, OrderID, ProductID, Quantity, PurchasePrice)
SELECT 2, 1001, p.ProductID, 1, p.Price FROM Product p WHERE p.ProductID = 26;

INSERT INTO OrderDetails (OrderDetailID, OrderID, ProductID, Quantity, PurchasePrice)
SELECT 3, 1001, p.ProductID, 3, p.Price FROM Product p WHERE p.ProductID = 27;

-- Update derived attribute OrderTotal
UPDATE Orders o
SET o.OrderTotal = (
    SELECT SUM(od.Quantity * od.PurchasePrice)
    FROM OrderDetails od
    WHERE od.OrderID = 1001
)
WHERE o.OrderID = 1001;

-- Payment and Shipping
INSERT INTO Payment (PaymentID, OrderID, PaymentMethod, Amount, DatePaid)
VALUES (5001, 1001, 'CREDIT', 1039.94, SYSDATE);

INSERT INTO Shipping (ShippingID, OrderID, Courier, TrackingNum, DeliveryDate, Status)
VALUES (6001, 1001, 'FedEx', 'FX123456789CA', NULL, 'SHIPPING');

COMMIT;

PROMPT ---------------------------------------------------------------
PROMPT ✅ All tables populated successfully.
PROMPT ---------------------------------------------------------------

EXIT;
