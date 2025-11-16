/* 
  CPS510
  E-commerce Database Schema (Views & Reports Phase)
  Authors: Basmulla Atekulla, Michelle & Rochelle
  ------------------------------------------------
  Purpose: Creates all SQL views, rollups, and report queries.
  ------------------------------------------------
*/

PROMPT ===============================================================
PROMPT   📈  BUILDING VIEWS AND REPORT QUERIES
PROMPT ===============================================================

-----------------------------------------------------------------------
-- VIEW 1: Customer_Rollup
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Customer_Rollup AS
SELECT
    c.CustomerID                         AS "Customer ID",
    c.Name                               AS "Customer Name",
    c.Email                              AS "Email",
    COUNT(DISTINCT o.OrderID)            AS "Orders Count",
    NVL(SUM(o.OrderTotal), 0)            AS "Total Ordered ($)",
    NVL(SUM(pay.Amount), 0)              AS "Total Paid ($)",
    NVL(SUM(o.OrderTotal), 0) - NVL(SUM(pay.Amount), 0) AS "Outstanding ($)",
    MAX(o.OrderDate)                     AS "Last Order Date"
FROM Customer c
LEFT JOIN Orders  o   ON o.CustomerID = c.CustomerID
LEFT JOIN Payment pay ON pay.OrderID   = o.OrderID
GROUP BY c.CustomerID, c.Name, c.Email;

PROMPT ✅ View 'Customer_Rollup' created successfully.

-----------------------------------------------------------------------
-- VIEW 2: Customer_OrderLines
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Customer_OrderLines AS
SELECT
    c.CustomerID          AS "Customer ID",
    c.Name                AS "Customer Name",
    o.OrderID             AS "Order ID",
    o.OrderDate           AS "Order Date",
    od.OrderDetailID      AS "Order Detail ID",
    p.ProductID           AS "Product ID",
    p.Name                AS "Product Name",
    p.Brand               AS "Brand",
    od.Quantity           AS "Quantity",
    od.PurchasePrice      AS "Unit Price",
    (od.Quantity * od.PurchasePrice) AS "Line Total ($)"
FROM Customer c
JOIN Orders o        ON o.CustomerID = c.CustomerID
JOIN OrderDetails od ON od.OrderID   = o.OrderID
JOIN Product p       ON p.ProductID  = od.ProductID;

PROMPT ✅ View 'Customer_OrderLines' created successfully.

-----------------------------------------------------------------------
-- VIEW 3: OrderDetails_ProductInfo
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW OrderDetails_ProductInfo AS
SELECT 
    od.OrderDetailID AS "Order Detail ID",
    od.OrderID       AS "Order ID",
    p.ProductID      AS "Product ID",
    p.Name           AS "Product Name",
    p.Brand          AS "Brand",
    p.Price          AS "Unit Price",
    od.Quantity      AS "Quantity Ordered",
    od.PurchasePrice AS "Purchase Price",
    (od.Quantity * od.PurchasePrice) AS "Line Total",
    b.ISBN           AS "Book ISBN",
    b.Author         AS "Book Author",
    c.SizeLabel      AS "Clothing Size",
    c.Material       AS "Material",
    e.WarrantyPeriod AS "Warranty",
    e.PowerRating    AS "Power Rating"
FROM OrderDetails od
JOIN Product p ON od.ProductID = p.ProductID
LEFT JOIN Books b ON p.ProductID = b.ProductID
LEFT JOIN Clothing c ON p.ProductID = c.ProductID
LEFT JOIN Electronics e ON p.ProductID = e.ProductID;

PROMPT ✅ View 'OrderDetails_ProductInfo' created successfully.

-----------------------------------------------------------------------
-- VIEW 4: OrderDetails_CustomerSummary
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW OrderDetails_CustomerSummary AS
SELECT 
    c.CustomerID AS "Customer ID",
    c.Name AS "Customer Name",
    o.OrderID AS "Order ID",
    o.OrderDate AS "Order Date",
    od.OrderDetailID AS "Order Detail ID",
    p.Name AS "Product Name",
    od.Quantity AS "Quantity",
    od.PurchasePrice AS "Price per Unit",
    (od.Quantity * od.PurchasePrice) AS "Line Total",
    o.Status AS "Order Status"
FROM Customer c
JOIN Orders o ON c.CustomerID = o.CustomerID
JOIN OrderDetails od ON o.OrderID = od.OrderID
JOIN Product p ON od.ProductID = p.ProductID;

PROMPT ✅ View 'OrderDetails_CustomerSummary' created successfully.

-----------------------------------------------------------------------
-- VIEW 5: OrderDetails_StaffPerformance
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW OrderDetails_StaffPerformance AS
SELECT 
    s.StaffID AS "Staff ID",
    s.Name AS "Staff Name",
    s.Role AS "Role",
    o.OrderID AS "Order ID",
    od.OrderDetailID AS "Order Detail ID",
    p.Name AS "Product Name",
    od.Quantity AS "Quantity",
    (od.Quantity * od.PurchasePrice) AS "Line Total"
FROM Staff s
JOIN Orders o ON s.StaffID = o.StaffID
JOIN OrderDetails od ON o.OrderID = od.OrderID
JOIN Product p ON od.ProductID = p.ProductID;

PROMPT ✅ View 'OrderDetails_StaffPerformance' created successfully.

-----------------------------------------------------------------------
-- VIEW 6: OrderDetails_PaymentShipping
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW OrderDetails_PaymentShipping AS
SELECT 
    o.OrderID AS "Order ID",
    od.OrderDetailID AS "Order Detail ID",
    p.Name AS "Product Name",
    od.Quantity AS "Quantity",
    od.PurchasePrice AS "Unit Price",
    (od.Quantity * od.PurchasePrice) AS "Line Total",
    pay.PaymentMethod AS "Payment Method",
    pay.Amount AS "Payment Amount",
    ship.Courier AS "Courier",
    ship.TrackingNum AS "Tracking Number",
    ship.Status AS "Shipping Status"
FROM Orders o
JOIN OrderDetails od ON o.OrderID = od.OrderID
JOIN Product p ON od.ProductID = p.ProductID
LEFT JOIN Payment pay ON o.OrderID = pay.OrderID
LEFT JOIN Shipping ship ON o.OrderID = ship.OrderID;

PROMPT ✅ View 'OrderDetails_PaymentShipping' created successfully.

-----------------------------------------------------------------------
-- VIEW 7: ShippingInformation
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW ShippingInformation AS
SELECT
    s.ShippingID,
    s.OrderID,
    c.CustomerID,
    c.Name AS CustomerName,
    c.Email AS CustomerEmail,
    o.OrderDate,
    o.Status AS OrderStatus,
    s.Courier, 
    s.TrackingNum,
    s.DeliveryDate,
    s.Status AS ShippingStatus
FROM Shipping s
JOIN Orders o ON s.OrderID = o.OrderID
JOIN Customer c ON o.CustomerID = c.CustomerID;

PROMPT ✅ View 'ShippingInformation' created successfully.

-----------------------------------------------------------------------
-- REPORTS AND VERIFICATION QUERIES
-----------------------------------------------------------------------
PROMPT ===============================================================
PROMPT   📄  RUNNING VERIFICATION REPORTS
PROMPT ===============================================================

-- Customers Summary
SELECT * FROM Customer_Rollup ORDER BY "Customer Name";

-- Customer Purchases
SELECT * FROM Customer_OrderLines ORDER BY "Customer Name", "Order Date" DESC;

-- Outstanding Balances
SELECT
    "Customer ID",
    "Customer Name",
    "Total Ordered ($)",
    "Total Paid ($)",
    "Outstanding ($)",
    TO_CHAR("Last Order Date",'YYYY-MM-DD') AS "Last Order Date"
FROM Customer_Rollup
WHERE "Outstanding ($)" > 0
ORDER BY "Outstanding ($)" DESC;

-- Top 5 Customers by Total Orders
SELECT * FROM (
  SELECT
      "Customer ID",
      "Customer Name",
      "Total Ordered ($)"
  FROM Customer_Rollup
  ORDER BY "Total Ordered ($)" DESC
)
WHERE ROWNUM <= 5;

-- Distinct Brands Purchased
SELECT DISTINCT
    v."Customer ID",
    v."Customer Name",
    v."Brand"
FROM Customer_OrderLines v
WHERE v."Brand" IS NOT NULL
ORDER BY v."Customer Name", v."Brand";

-- Revenue by Brand per Customer
SELECT
    v."Customer ID",
    v."Customer Name",
    NVL(v."Brand", 'Unbranded') AS "Brand",
    SUM(v."Line Total ($)")     AS "Revenue ($)"
FROM Customer_OrderLines v
GROUP BY v."Customer ID", v."Customer Name", NVL(v."Brand", 'Unbranded')
ORDER BY v."Customer Name", "Revenue ($)" DESC;

PROMPT ---------------------------------------------------------------
PROMPT ✅ All Views and Reports created successfully.
PROMPT ---------------------------------------------------------------

EXIT;
