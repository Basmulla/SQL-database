/* 
  CPS510 
  E-commerce Database Schema (Drop Phase)
  Authors: Basmulla Atekulla, Michelle & Rochelle
  ------------------------------------------------
  Purpose: Drops all tables safely in dependency order.
  ------------------------------------------------
*/

WHENEVER SQLERROR CONTINUE

PROMPT ===============================================================
PROMPT   🔻 DROPPING ALL TABLES (if exist)
PROMPT ===============================================================

DROP TABLE OrderDetails CASCADE CONSTRAINTS;
DROP TABLE Payment CASCADE CONSTRAINTS;
DROP TABLE Shipping CASCADE CONSTRAINTS;
DROP TABLE Orders CASCADE CONSTRAINTS;
DROP TABLE Books CASCADE CONSTRAINTS;
DROP TABLE Clothing CASCADE CONSTRAINTS;
DROP TABLE Electronics CASCADE CONSTRAINTS;
DROP TABLE Product CASCADE CONSTRAINTS;
DROP TABLE Manager CASCADE CONSTRAINTS;
DROP TABLE Processor CASCADE CONSTRAINTS;
DROP TABLE Shipper CASCADE CONSTRAINTS;
DROP TABLE Staff CASCADE CONSTRAINTS;
DROP TABLE Customer CASCADE CONSTRAINTS;

PROMPT ---------------------------------------------------------------
PROMPT ✅ All tables dropped successfully (if they existed)
PROMPT ---------------------------------------------------------------

EXIT;
