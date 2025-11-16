/* 
  CPS510
  E-commerce Database Schema (Create Phase)
  Authors: Basmulla Atekulla, Michelle & Rochelle
  ------------------------------------------------
  Purpose: Creates all tables and constraints.
  ------------------------------------------------
*/

PROMPT ===============================================================
PROMPT   🏗️  CREATING DATABASE STRUCTURE
PROMPT ===============================================================

------------------------------------------------------------
-- CUSTOMER
------------------------------------------------------------
CREATE TABLE Customer (
    CustomerID    NUMBER(10) PRIMARY KEY,
    Name          VARCHAR2(50) NOT NULL,
    Email         VARCHAR2(100) NOT NULL UNIQUE,
    Phone         VARCHAR2(15),
    Address       VARCHAR2(200)
);

------------------------------------------------------------
-- STAFF (Superclass + Subtypes)
------------------------------------------------------------
CREATE TABLE Staff (
    StaffID    NUMBER(10) PRIMARY KEY,
    Name       VARCHAR2(50) NOT NULL,
    Role       VARCHAR2(30) NOT NULL,
    Email      VARCHAR2(100) UNIQUE,
    CONSTRAINT staff_role_chk CHECK (Role IN ('Manager', 'Processor', 'Shipper'))
);

CREATE TABLE Manager (
    StaffID NUMBER(10) PRIMARY KEY,
    OfficeNumber NUMBER(10),
    Salary NUMBER(10,2),
    YearsExperience NUMBER(3),
    CONSTRAINT fk_manager_staff FOREIGN KEY (StaffID) REFERENCES Staff(StaffID) ON DELETE CASCADE
);

CREATE TABLE Processor (
    StaffID NUMBER(10) PRIMARY KEY,
    ProcessingStation VARCHAR2(50),
    ShiftHours VARCHAR2(20),
    CONSTRAINT fk_processor_staff FOREIGN KEY (StaffID) REFERENCES Staff(StaffID) ON DELETE CASCADE
);

CREATE TABLE Shipper (
    StaffID NUMBER(10) PRIMARY KEY,
    VehicleAssigned VARCHAR2(50),
    ShiftHours VARCHAR2(20),
    CONSTRAINT fk_shipper_staff FOREIGN KEY (StaffID) REFERENCES Staff(StaffID) ON DELETE CASCADE
);

------------------------------------------------------------
-- PRODUCT (Superclass + Subtypes)
------------------------------------------------------------
CREATE TABLE Product (
    ProductID NUMBER(10) PRIMARY KEY,
    Name VARCHAR2(100) NOT NULL,
    Description VARCHAR2(255),
    Price NUMBER(10,2) NOT NULL,
    Brand VARCHAR2(50),
    StockQuantity NUMBER(10),
    IsActive CHAR(1) CHECK (IsActive IN ('Y','N')),
    StaffID NUMBER(10),
    CONSTRAINT fk_product_staff FOREIGN KEY (StaffID) REFERENCES Staff(StaffID)
);

CREATE TABLE Books (
    ProductID NUMBER(10) PRIMARY KEY,
    ISBN VARCHAR2(20),
    Author VARCHAR2(100),
    Publisher VARCHAR2(100),
    CONSTRAINT fk_books_product FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
);

CREATE TABLE Clothing (
    ProductID NUMBER(10) PRIMARY KEY,
    SizeLabel VARCHAR2(5),
    Material VARCHAR2(50),
    GenderCategory VARCHAR2(20),
    CONSTRAINT fk_clothing_product FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
);

CREATE TABLE Electronics (
    ProductID NUMBER(10) PRIMARY KEY,
    WarrantyPeriod VARCHAR2(20),
    PowerRating VARCHAR2(20),
    CONSTRAINT fk_electronics_product FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
);

------------------------------------------------------------
-- ORDERS + DEPENDENTS
------------------------------------------------------------
CREATE TABLE Orders (
    OrderID NUMBER(10) PRIMARY KEY,
    CustomerID NUMBER(10),
    OrderDate DATE,
    Status VARCHAR2(20),
    OrderTotal NUMBER(10,2),
    StaffID NUMBER(10),
    CONSTRAINT fk_orders_customer FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    CONSTRAINT fk_orders_staff FOREIGN KEY (StaffID) REFERENCES Staff(StaffID)
);

CREATE TABLE OrderDetails (
    OrderDetailID NUMBER(10) PRIMARY KEY,
    OrderID NUMBER(10),
    ProductID NUMBER(10),
    Quantity NUMBER(10),
    PurchasePrice NUMBER(10,2),
    CONSTRAINT fk_orderdetails_orders FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE,
    CONSTRAINT fk_orderdetails_product FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

CREATE TABLE Payment (
    PaymentID NUMBER(10) PRIMARY KEY,
    OrderID NUMBER(10) UNIQUE,
    PaymentMethod VARCHAR2(20),
    Amount NUMBER(10,2),
    DatePaid DATE,
    CONSTRAINT fk_payment_order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE
);

CREATE TABLE Shipping (
    ShippingID NUMBER(10) PRIMARY KEY,
    OrderID NUMBER(10) UNIQUE,
    Courier VARCHAR2(50),
    TrackingNum VARCHAR2(50),
    DeliveryDate DATE,
    Status VARCHAR2(20),
    CONSTRAINT fk_shipping_order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE
);

PROMPT ---------------------------------------------------------------
PROMPT ✅ All tables created successfully
PROMPT ---------------------------------------------------------------

EXIT;
