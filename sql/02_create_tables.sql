
CREATE TABLE Customer (
    CustomerID NUMBER PRIMARY KEY,
    Name VARCHAR2(100) NOT NULL,
    Email VARCHAR2(100),
    Phone VARCHAR2(20),
    Address VARCHAR2(200)
);

CREATE TABLE Staff (
    StaffID NUMBER PRIMARY KEY,
    Name VARCHAR2(100) NOT NULL,
    Role VARCHAR2(50),
    Email VARCHAR2(100)
);

CREATE TABLE Manager (
    StaffID NUMBER PRIMARY KEY REFERENCES Staff(StaffID),
    OfficeNumber NUMBER,
    Salary NUMBER(10,2),
    YearsExperience NUMBER
);

CREATE TABLE Processor (
    StaffID NUMBER PRIMARY KEY REFERENCES Staff(StaffID),
    ProcessingStation VARCHAR2(50),
    ShiftHours VARCHAR2(20)
);

CREATE TABLE Shipper (
    StaffID NUMBER PRIMARY KEY REFERENCES Staff(StaffID),
    VehicleAssigned VARCHAR2(50),
    ShiftHours VARCHAR2(20)
);

CREATE TABLE Product (
    ProductID NUMBER PRIMARY KEY,
    Name VARCHAR2(100) NOT NULL,
    Description VARCHAR2(255),
    Price NUMBER(10,2),
    Brand VARCHAR2(50),
    StockQuantity NUMBER,
    IsActive CHAR(1),
    StaffID NUMBER REFERENCES Staff(StaffID)
);

CREATE TABLE Books (
    ProductID NUMBER PRIMARY KEY REFERENCES Product(ProductID),
    ISBN VARCHAR2(20),
    Author VARCHAR2(100),
    Publisher VARCHAR2(100)
);

CREATE TABLE Clothing (
    ProductID NUMBER PRIMARY KEY REFERENCES Product(ProductID),
    SizeLabel VARCHAR2(10),
    Material VARCHAR2(50),
    GenderCategory VARCHAR2(20)
);

CREATE TABLE Electronics (
    ProductID NUMBER PRIMARY KEY REFERENCES Product(ProductID),
    WarrantyPeriod VARCHAR2(20),
    PowerRating VARCHAR2(20)
);

CREATE TABLE Orders (
    OrderID NUMBER PRIMARY KEY,
    CustomerID NUMBER REFERENCES Customer(CustomerID),
    OrderDate DATE,
    Status VARCHAR2(20),
    OrderTotal NUMBER(10,2),
    StaffID NUMBER REFERENCES Staff(StaffID)
);

CREATE TABLE OrderDetails (
    OrderDetailID NUMBER PRIMARY KEY,
    OrderID NUMBER REFERENCES Orders(OrderID),
    ProductID NUMBER REFERENCES Product(ProductID),
    Quantity NUMBER,
    PurchasePrice NUMBER(10,2)
);

CREATE TABLE Payment (
    PaymentID NUMBER PRIMARY KEY,
    OrderID NUMBER REFERENCES Orders(OrderID),
    PaymentMethod VARCHAR2(20),
    Amount NUMBER(10,2),
    DatePaid DATE
);

CREATE TABLE Shipping (
    ShippingID NUMBER PRIMARY KEY,
    OrderID NUMBER REFERENCES Orders(OrderID),
    Courier VARCHAR2(50),
    TrackingNum VARCHAR2(50),
    DeliveryDate DATE,
    Status VARCHAR2(20)
);

