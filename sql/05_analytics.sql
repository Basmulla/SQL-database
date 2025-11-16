/* 
  CPS510
  E-commerce Database (Advanced Analytics Phase)
  Authors: Basmulla Atekulla, Michelle & Rochelle
  ------------------------------------------------
  Purpose: Builds analytical queries, snapshots, and BI-style reports.
  ------------------------------------------------
*/

PROMPT ===============================================================
PROMPT   📊  ADVANCED ANALYTICS & INSIGHTS
PROMPT ===============================================================

-----------------------------------------------------------------------
-- 1️⃣ CUSTOMER RFM SNAPSHOT (Recency, Frequency, Monetary)
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Customer_RFM AS
SELECT
    c.CustomerID,
    c.Name AS CustomerName,
    MAX(o.OrderDate) AS LastPurchaseDate,
    ROUND(SYSDATE - MAX(o.OrderDate)) AS RecencyDays,
    COUNT(o.OrderID) AS Frequency,
    NVL(SUM(o.OrderTotal), 0) AS MonetaryValue,
    CASE 
        WHEN ROUND(SYSDATE - MAX(o.OrderDate)) <= 7 THEN 'Active'
        WHEN ROUND(SYSDATE - MAX(o.OrderDate)) BETWEEN 8 AND 30 THEN 'Warm'
        ELSE 'Inactive'
    END AS EngagementStatus
FROM Customer c
LEFT JOIN Orders o ON c.CustomerID = o.CustomerID
GROUP BY c.CustomerID, c.Name;

PROMPT ✅ View 'Customer_RFM' created successfully.

-----------------------------------------------------------------------
-- 2️⃣ PRODUCT PROFITABILITY SNAPSHOT
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Product_Profitability AS
SELECT
    p.ProductID,
    p.Name AS ProductName,
    p.Brand,
    SUM(od.Quantity) AS TotalUnitsSold,
    SUM(od.Quantity * od.PurchasePrice) AS Revenue,
    AVG(od.PurchasePrice) AS AvgSellingPrice,
    p.Price AS ListedPrice,
    (SUM(od.Quantity * od.PurchasePrice) - (SUM(od.Quantity) * p.Price * 0.6)) AS EstimatedProfit
FROM Product p
JOIN OrderDetails od ON p.ProductID = od.ProductID
GROUP BY p.ProductID, p.Name, p.Brand, p.Price;

PROMPT ✅ View 'Product_Profitability' created successfully.

-----------------------------------------------------------------------
-- 3️⃣ INVENTORY ALERTS (Low Stock)
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Low_Stock_Alerts AS
SELECT
    p.ProductID,
    p.Name AS ProductName,
    p.Brand,
    p.StockQuantity,
    CASE 
        WHEN p.StockQuantity = 0 THEN 'OUT OF STOCK ⚠️'
        WHEN p.StockQuantity <= 5 THEN 'LOW STOCK'
        ELSE 'OK'
    END AS StockStatus
FROM Product p
ORDER BY p.StockQuantity ASC;

PROMPT ✅ View 'Low_Stock_Alerts' created successfully.

-----------------------------------------------------------------------
-- 4️⃣ SHIPPING SLA MONITORING (Aging Analysis)
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Shipping_Aging AS
SELECT
    s.ShippingID,
    s.OrderID,
    c.Name AS CustomerName,
    s.Courier,
    s.Status AS ShippingStatus,
    s.TrackingNum,
    s.DeliveryDate,
    ROUND(SYSDATE - o.OrderDate) AS DaysSinceOrder,
    CASE
        WHEN s.Status = 'DELIVERED' THEN 'Delivered On Time'
        WHEN s.Status = 'SHIPPING' AND (SYSDATE - o.OrderDate) > 7 THEN '⚠️ Delayed'
        ELSE 'In Transit'
    END AS SLA_Status
FROM Shipping s
JOIN Orders o ON s.OrderID = o.OrderID
JOIN Customer c ON o.CustomerID = c.CustomerID;

PROMPT ✅ View 'Shipping_Aging' created successfully.

-----------------------------------------------------------------------
-- 5️⃣ PAYMENT METHOD DISTRIBUTION
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Payment_Distribution AS
SELECT
    pay.PaymentMethod,
    COUNT(pay.PaymentID) AS NumPayments,
    SUM(pay.Amount) AS TotalPaid,
    ROUND(AVG(pay.Amount), 2) AS AvgPayment
FROM Payment pay
GROUP BY pay.PaymentMethod;

PROMPT ✅ View 'Payment_Distribution' created successfully.

-----------------------------------------------------------------------
-- 6️⃣ STAFF PERFORMANCE (Orders Processed + Revenue)
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW Staff_Performance_Summary AS
SELECT
    s.StaffID,
    s.Name AS StaffName,
    s.Role,
    COUNT(DISTINCT o.OrderID) AS OrdersHandled,
    NVL(SUM(o.OrderTotal), 0) AS TotalValueProcessed
FROM Staff s
LEFT JOIN Orders o ON s.StaffID = o.StaffID
GROUP BY s.StaffID, s.Name, s.Role;

PROMPT ✅ View 'Staff_Performance_Summary' created successfully.

-----------------------------------------------------------------------
-- 7️⃣ AGGREGATED DASHBOARD SNAPSHOT
-----------------------------------------------------------------------
CREATE OR REPLACE VIEW System_Dashboard AS
SELECT
    (SELECT COUNT(*) FROM Customer) AS Total_Customers,
    (SELECT COUNT(*) FROM Product) AS Total_Products,
    (SELECT COUNT(*) FROM Orders) AS Total_Orders,
    (SELECT COUNT(*) FROM Payment) AS Total_Payments,
    (SELECT COUNT(*) FROM Shipping) AS Total_Shipments,
    (SELECT COUNT(*) FROM Staff) AS Total_Staff
FROM dual;

PROMPT ✅ View 'System_Dashboard' created successfully.

-----------------------------------------------------------------------
-- 8️⃣ EXECUTION QUERIES (VERIFICATION)
-----------------------------------------------------------------------
PROMPT ===============================================================
PROMPT   📄  RUNNING ANALYTICAL REPORTS
PROMPT ===============================================================

-- Customer Engagement Summary
SELECT * FROM Customer_RFM ORDER BY RecencyDays;

-- Product Profitability Summary
SELECT * FROM Product_Profitability ORDER BY Revenue DESC;

-- Stock Status
SELECT * FROM Low_Stock_Alerts;

-- Shipping SLA Report
SELECT * FROM Shipping_Aging ORDER BY DaysSinceOrder DESC;

-- Payment Breakdown
SELECT * FROM Payment_Distribution ORDER BY TotalPaid DESC;

-- Staff Performance
SELECT * FROM Staff_Performance_Summary ORDER BY TotalValueProcessed DESC;

-- System Overview Snapshot
SELECT * FROM System_Dashboard;

PROMPT ---------------------------------------------------------------
PROMPT ✅ All Analytics Views and Reports generated successfully.
PROMPT ---------------------------------------------------------------

EXIT;
