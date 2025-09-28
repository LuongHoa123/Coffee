
CREATE DATABASE IF NOT EXISTS CoffeeDB
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE CoffeeDB;

-- ============================================
-- 1. Bảng Setting (Category, Role, Unit, Status...)
-- ================================issues============
CREATE TABLE Setting (
    SettingID INT AUTO_INCREMENT PRIMARY KEY,
    Type NVARCHAR(50) NOT NULL,   -- 'Role', 'Category', 'Unit', 'Status'
    Value NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255),
    IsActive TINYINT(1) DEFAULT 1
);

-- ============================================
-- 2. Bảng Supplier
-- ============================================
CREATE TABLE Suppliers (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    SupplierName NVARCHAR(100) NOT NULL,
    ContactName NVARCHAR(100),
    Email NVARCHAR(100),
    Phone NVARCHAR(20),
    Address NVARCHAR(255),
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 3. Bảng User
-- ============================================
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255) NOT NULL,
    Phone NVARCHAR(20),
    Address NVARCHAR(255),
    RoleID INT NOT NULL,  -- Tham chiếu Setting(Type='Role')
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (RoleID) REFERENCES Setting(SettingID)
);

-- ============================================
-- 4. Bảng Shop
-- ============================================
CREATE TABLE Shops (
    ShopID INT AUTO_INCREMENT PRIMARY KEY,
    ShopName NVARCHAR(100) NOT NULL,
    Address NVARCHAR(255),
    Phone NVARCHAR(20),
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 5. Bảng Product (sản phẩm bán)
-- ============================================
CREATE TABLE Products (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    ProductName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255),
    CategoryID INT NOT NULL,   -- Tham chiếu Setting(Type='Category')
    Price DECIMAL(10,2) NOT NULL,
    SupplierID INT,            -- Nhà cung cấp
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CategoryID) REFERENCES Setting(SettingID),
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID)
);

-- ============================================
-- 6. Bảng Ingredient (nguyên liệu)
-- ============================================
CREATE TABLE Ingredients (
    IngredientID INT AUTO_INCREMENT PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    UnitID INT,   -- Tham chiếu Setting(Type='Unit')
    StockQuantity DECIMAL(10,2) DEFAULT 0,
    SupplierID INT,   -- Nhà cung cấp
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UnitID) REFERENCES Setting(SettingID),
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID)
);

-- ============================================
-- 7. Bảng Purchase Order (PO - nhập hàng từ Supplier)
-- ============================================
CREATE TABLE PurchaseOrders (
    POID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT NOT NULL,
    SupplierID INT NOT NULL,
    CreatedBy INT NOT NULL,
    StatusID INT,   -- Tham chiếu Setting(Type='POStatus')
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ShopID) REFERENCES Shops(ShopID),
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
    FOREIGN KEY (StatusID) REFERENCES Setting(SettingID)
);

CREATE TABLE PurchaseOrderDetails (
    PODetailID INT AUTO_INCREMENT PRIMARY KEY,
    POID INT NOT NULL,
    IngredientID INT NOT NULL,
    Quantity DECIMAL(10,2) NOT NULL,
    ReceivedQuantity DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (POID) REFERENCES PurchaseOrders(POID),
    FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID)
);

-- ============================================
-- 8. Bảng Issue (nguyên liệu hỏng/lỗi)
-- ============================================
CREATE TABLE Issues (
    IssueID INT AUTO_INCREMENT PRIMARY KEY,
    IngredientID INT NOT NULL,
    Quantity DECIMAL(10,2) NOT NULL,
    StatusID INT,  -- Tham chiếu Setting(Type='IssueStatus')
    CreatedBy INT NOT NULL,
    ConfirmedBy INT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
    FOREIGN KEY (ConfirmedBy) REFERENCES Users(UserID),
    FOREIGN KEY (StatusID) REFERENCES Setting(SettingID)
);

-- ============================================
-- 9. Bảng Order (khách đặt sản phẩm)
-- ============================================
CREATE TABLE Orders (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    ShopID INT NOT NULL,
    CreatedBy INT NOT NULL,
    StatusID INT,   -- Tham chiếu Setting(Type='OrderStatus')
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ShopID) REFERENCES Shops(ShopID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
    FOREIGN KEY (StatusID) REFERENCES Setting(SettingID)
);

CREATE TABLE OrderDetails (
    OrderDetailID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- ============================================
-- DATA INSERTION - DỮ LIỆU MẪU
-- ============================================

-- 1. Thêm dữ liệu cho bảng Setting
INSERT INTO Setting (Type, Value, Description, IsActive) VALUES
-- Roles
('Role', 'HR', 'Nhân sự - Quản lý nhân viên', 1),
('Role', 'Admin', 'Quản trị viên hệ thống', 1),
('Role', 'Inventory', 'Quản lý kho - Nhập xuất hàng', 1),
('Role', 'Barista', 'Pha chế - Nhân viên pha cà phê', 1),

-- Categories
('Category', 'Espresso', 'Các loại cà phê espresso', 1),
('Category', 'Cold Brew', 'Cà phê pha lạnh', 1),
('Category', 'Latte', 'Cà phê sữa nghệ thuật', 1),
('Category', 'Frappuccino', 'Đồ uống đá xay', 1),
('Category', 'Tea', 'Các loại trà', 1),
('Category', 'Pastry', 'Bánh ngọt và bánh mì', 1),
('Category', 'Dessert', 'Tráng miệng', 1),

-- Units
('Unit', 'kg', 'Kilogram', 1),
('Unit', 'g', 'Gram', 1),
('Unit', 'l', 'Lít', 1),
('Unit', 'ml', 'Mililit', 1),
('Unit', 'pack', 'Gói', 1),
('Unit', 'bottle', 'Chai', 1),
('Unit', 'bag', 'Bao', 1),

-- Purchase Order Status
('POStatus', 'Pending', 'Đơn hàng chờ xử lý', 1),
('POStatus', 'Approved', 'Đơn hàng đã được duyệt', 1),
('POStatus', 'Shipping', 'Đang giao hàng', 1),
('POStatus', 'Received', 'Đã nhận hàng', 1),
('POStatus', 'Cancelled', 'Đã hủy đơn hàng', 1),

-- Issue Status  
('IssueStatus', 'Reported', 'Đã báo cáo sự cố', 1),
('IssueStatus', 'Under Investigation', 'Đang điều tra', 1),
('IssueStatus', 'Resolved', 'Đã giải quyết', 1),
('IssueStatus', 'Rejected', 'Từ chối xử lý', 1),

-- Order Status
('OrderStatus', 'New', 'Đơn hàng mới', 1),
('OrderStatus', 'Preparing', 'Đang chuẩn bị', 1),
('OrderStatus', 'Ready', 'Sẵn sàng', 1),
('OrderStatus', 'Completed', 'Đã hoàn thành', 1),
('OrderStatus', 'Cancelled', 'Đã hủy', 1);

-- 2. Thêm dữ liệu cho bảng Suppliers
INSERT INTO Suppliers (SupplierName, ContactName, Email, Phone, Address, IsActive) VALUES
('Công ty TNHH Cà phê Highlands', 'Nguyễn Văn An', 'contact@highlands.com.vn', '0901234567', '123 Đường Nguyễn Huệ, Q1, TP.HCM', 1),
('Trung Nguyên Coffee', 'Lê Thị Bình', 'sales@trungnguyencoffee.com', '0912345678', '456 Đường Lê Lợi, Q1, TP.HCM', 1),
('Công ty Sữa TH True Milk', 'Trần Minh Châu', 'wholesale@thmilk.vn', '0923456789', '789 Đường Điện Biên Phủ, Q3, TP.HCM', 1),
('Công ty Bánh Kẹo Kinh Đô', 'Phạm Văn Dũng', 'b2b@kinh-do.com.vn', '0934567890', '321 Đường Cách Mạng Tháng 8, Q10, TP.HCM', 1),
('Công ty Đường Biên Hòa', 'Hoàng Thị Lan', 'contact@bienhoasugar.com', '0945678901', '654 Đường Xô Viết Nghệ Tĩnh, Biên Hòa, Đồng Nai', 1);

-- 3. Thêm dữ liệu cho bảng Users (Mật khẩu đã hash cho "password123")
INSERT INTO Users (FullName, Email, PasswordHash, Phone, Address, RoleID, IsActive) VALUES
('Nguyễn Thị Hồng', 'hr@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0901234567', '123 Đường Lê Lợi, Q1, TP.HCM', 1, 1),
('Trần Minh Quân', 'admin@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0912345678', '456 Đường Nguyễn Huệ, Q1, TP.HCM', 2, 1),
('Lê Thị Mai', 'inventory.hcm@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0923456789', '789 Đường Điện Biên Phủ, Q3, TP.HCM', 3, 1),
('Nguyễn Văn Hùng', 'inventory.hn@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0934567890', '321 Đường Hoàn Kiếm, Hà Nội', 3, 1),
('Phạm Thị Linh', 'employee01@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0945678901', '654 Đường Cách Mạng Tháng 8, Q10, TP.HCM', 3, 1),
('Hoàng Minh Tú', 'employee02@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0956789012', '987 Đường Trần Phú, Q5, TP.HCM', 3, 1),
('Vũ Thị Nam', 'cashier01@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0967890123', '147 Đường Lý Tự Trọng, Q1, TP.HCM', 4, 1),
('Đỗ Văn Phong', 'cashier02@coffeelux.com', '$2a$10$X9Y7ZqKkQpLmN5rO8sT4veBcD2fG6hJ1kL3mP9qR5sU7wX0zA2bC4', '0978901234', '258 Đường Võ Thị Sáu, Q3, TP.HCM', 4, 1);

-- 4. Thêm dữ liệu cho bảng Shops
INSERT INTO Shops (ShopName, Address, Phone, IsActive) VALUES
('CoffeeLux - Chi nhánh Quận 1', '123 Đường Đồng Khởi, P. Bến Nghé, Q1, TP.HCM', '02838234567', 1),
('CoffeeLux - Chi nhánh Quận 3', '456 Đường Võ Văn Tần, P.6, Q3, TP.HCM', '02838345678', 1),
('CoffeeLux - Chi nhánh Hà Nội', '789 Đường Hoàn Kiếm, P. Hàng Trống, Q. Hoàn Kiếm, HN', '02438456789', 1),
('CoffeeLux - Chi nhánh Đà Nẵng', '321 Đường Trần Phú, P. Thạch Thang, Q. Hải Châu, ĐN', '02363567890', 1);

-- 5. Thêm dữ liệu cho bảng Products
INSERT INTO Products (ProductName, Description, CategoryID, Price, SupplierID, IsActive) VALUES
-- Espresso Products
('Americano', 'Cà phê đen truyền thống', 5, 35000.00, 1, 1),
('Espresso', 'Cà phê espresso đậm đà', 5, 30000.00, 1, 1),
('Double Espresso', 'Espresso tăng cường', 5, 45000.00, 1, 1),

-- Cold Brew Products  
('Cold Brew Original', 'Cà phê pha lạnh nguyên chất', 6, 45000.00, 2, 1),
('Cold Brew Vanilla', 'Cà phê lạnh vị vanilla', 6, 50000.00, 2, 1),

-- Latte Products
('Caffe Latte', 'Cà phê sữa nghệ thuật', 7, 55000.00, 1, 1),
('Vanilla Latte', 'Latte vị vanilla', 7, 60000.00, 1, 1),
('Caramel Latte', 'Latte vị caramel', 7, 65000.00, 1, 1),

-- Frappuccino Products
('Chocolate Frappuccino', 'Đá xay chocolate', 8, 70000.00, 3, 1),
('Coffee Frappuccino', 'Đá xay cà phê', 8, 65000.00, 3, 1),

-- Tea Products
('Green Tea Latte', 'Trà xanh sữa', 9, 50000.00, 2, 1),
('Earl Grey Tea', 'Trà Earl Grey', 9, 40000.00, 2, 1),

-- Pastry Products
('Croissant', 'Bánh sừng bò bơ', 10, 25000.00, 4, 1),
('Chocolate Muffin', 'Bánh muffin chocolate', 10, 30000.00, 4, 1),
('Blueberry Scone', 'Bánh scone việt quất', 10, 35000.00, 4, 1),

-- Dessert Products
('Tiramisu', 'Bánh tiramisu Ý', 11, 65000.00, 4, 1),
('Cheesecake', 'Bánh phô mai New York', 11, 70000.00, 4, 1);

-- 6. Thêm dữ liệu cho bảng Ingredients
INSERT INTO Ingredients (Name, UnitID, StockQuantity, SupplierID, IsActive) VALUES
-- Coffee beans and powder
('Cà phê Arabica hạt', 12, 50.00, 1, 1),      -- kg
('Cà phê Robusta hạt', 12, 30.00, 2, 1),      -- kg  
('Cà phê Espresso xay', 12, 20.00, 1, 1),     -- kg

-- Dairy products
('Sữa tươi nguyên kem', 15, 100.00, 3, 1),    -- l
('Sữa đặc có đường', 18, 50.00, 3, 1),        -- bottle
('Kem tươi', 15, 25.00, 3, 1),                -- l
('Sữa hạnh nhân', 15, 30.00, 3, 1),           -- l

-- Sweeteners  
('Đường trắng', 12, 25.00, 5, 1),             -- kg
('Đường nâu', 12, 15.00, 5, 1),               -- kg
('Mật ong', 18, 20.00, 5, 1),                 -- bottle

-- Syrups and flavors
('Syrup Vanilla', 18, 15.00, 4, 1),           -- bottle
('Syrup Caramel', 18, 12.00, 4, 1),           -- bottle
('Syrup Hazelnut', 18, 10.00, 4, 1),          -- bottle

-- Baking ingredients
('Bột mì đa dụng', 12, 40.00, 4, 1),          -- kg
('Bột chocolate', 12, 8.00, 4, 1),            -- kg
('Bột nở', 17, 20.00, 4, 1),                  -- pack
('Trứng gà', 17, 50.00, 4, 1),                -- pack
('Bơ lạt', 12, 15.00, 4, 1),                  -- kg

-- Tea leaves
('Lá trà xanh', 17, 25.00, 2, 1),             -- pack
('Lá trà Earl Grey', 17, 20.00, 2, 1),        -- pack
('Lá trà Oolong', 17, 15.00, 2, 1);           -- pack

-- 7. Thêm dữ liệu cho bảng PurchaseOrders
INSERT INTO PurchaseOrders (ShopID, SupplierID, CreatedBy, StatusID) VALUES
(1, 1, 3, 22), -- Received - Created by Inventory HCM
(1, 3, 3, 21), -- Shipping - Created by Inventory HCM
(2, 2, 4, 20), -- Approved - Created by Inventory HN
(3, 4, 4, 19), -- Pending - Created by Inventory HN
(4, 5, 3, 22); -- Received - Created by Inventory HCM

-- 8. Thêm dữ liệu cho bảng PurchaseOrderDetails
INSERT INTO PurchaseOrderDetails (POID, IngredientID, Quantity, ReceivedQuantity) VALUES
-- PO 1 (Received)
(1, 1, 20.00, 20.00),    -- Cà phê Arabica
(1, 4, 50.00, 50.00),    -- Sữa tươi
(1, 7, 10.00, 10.00),    -- Đường trắng

-- PO 2 (Shipping)
(2, 4, 30.00, 0.00),     -- Sữa tươi  
(2, 5, 20.00, 0.00),     -- Sữa đặc
(2, 6, 15.00, 0.00),     -- Kem tươi

-- PO 3 (Approved)  
(3, 2, 15.00, 0.00),     -- Cà phê Robusta
(3, 3, 10.00, 0.00),     -- Espresso xay

-- PO 4 (Pending)
(4, 13, 30.00, 0.00),    -- Bột mì
(4, 15, 15.00, 0.00),    -- Bột nở  
(4, 16, 40.00, 0.00),    -- Trứng

-- PO 5 (Received)
(5, 8, 20.00, 20.00),    -- Đường nâu
(5, 9, 15.00, 15.00);    -- Mật ong

-- 9. Thêm dữ liệu cho bảng Issues
INSERT INTO Issues (IngredientID, Quantity, StatusID, CreatedBy, ConfirmedBy) VALUES
(1, 2.50, 25, 5, 3),     -- Cà phê Arabica bị ẩm mốc - Resolved (Barista report, Inventory confirm)
(4, 5.00, 23, 6, NULL),  -- Sữa tươi hết hạn - Reported (Barista report)
(7, 1.00, 24, 5, 4),     -- Đường bị vón cục - Under Investigation (Barista report, Inventory investigate)
(13, 3.00, 25, 7, 3);    -- Bột mì bị mọt - Resolved (Barista report, Inventory confirm)

-- 10. Thêm dữ liệu cho bảng Orders  
INSERT INTO Orders (ShopID, CreatedBy, StatusID) VALUES
(1, 5, 30), -- Completed - Barista 01
(1, 6, 29), -- Ready - Barista 02
(2, 7, 28), -- Preparing - Barista 03
(1, 5, 30), -- Completed - Barista 01
(3, 6, 27), -- New - Barista 02
(2, 8, 30), -- Completed - Barista 04
(4, 7, 28), -- Preparing - Barista 03
(1, 5, 29); -- Ready - Barista 01

-- 11. Thêm dữ liệu cho bảng OrderDetails
INSERT INTO OrderDetails (OrderID, ProductID, Quantity, Price) VALUES
-- Order 1 (Completed)
(1, 1, 2, 35000.00),     -- 2 Americano
(1, 13, 1, 25000.00),    -- 1 Croissant

-- Order 2 (Ready)  
(2, 6, 1, 55000.00),     -- 1 Caffe Latte
(2, 14, 1, 30000.00),    -- 1 Chocolate Muffin

-- Order 3 (Preparing)
(3, 9, 1, 70000.00),     -- 1 Chocolate Frappuccino
(3, 16, 1, 65000.00),    -- 1 Tiramisu

-- Order 4 (Completed)
(4, 4, 1, 45000.00),     -- 1 Cold Brew Original  
(4, 11, 1, 50000.00),    -- 1 Green Tea Latte

-- Order 5 (New)
(5, 2, 1, 30000.00),     -- 1 Espresso
(5, 15, 1, 35000.00),    -- 1 Blueberry Scone

-- Order 6 (Completed)
(6, 8, 1, 65000.00),     -- 1 Caramel Latte
(6, 17, 1, 70000.00),    -- 1 Cheesecake

-- Order 7 (Preparing)  
(7, 5, 1, 50000.00),     -- 1 Cold Brew Vanilla
(7, 12, 1, 40000.00),    -- 1 Earl Grey Tea

-- Order 8 (Ready)
(8, 10, 1, 65000.00),    -- 1 Coffee Frappuccino
(8, 13, 2, 25000.00);    -- 2 Croissant

-- ============================================
-- TEST DATA VERIFICATION QUERIES
-- ============================================
/*
-- Kiểm tra dữ liệu đã insert:

-- 1. Xem tất cả settings
SELECT * FROM Setting ORDER BY Type, SettingID;

-- 2. Xem users với role names  
SELECT u.*, s.Value as RoleName 
FROM Users u 
JOIN Setting s ON u.RoleID = s.SettingID 
WHERE s.Type = 'Role';

-- 3. Xem products với category và supplier
SELECT p.ProductName, p.Price, 
       c.Value as Category, 
       s.SupplierName
FROM Products p
JOIN Setting c ON p.CategoryID = c.SettingID
LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID
ORDER BY c.Value, p.ProductName;

-- 4. Xem orders với details
SELECT o.OrderID, sh.ShopName, u.FullName as CreatedBy,
       st.Value as Status, o.CreatedAt,
       p.ProductName, od.Quantity, od.Price
FROM Orders o
JOIN Shops sh ON o.ShopID = sh.ShopID  
JOIN Users u ON o.CreatedBy = u.UserID
JOIN Setting st ON o.StatusID = st.SettingID
JOIN OrderDetails od ON o.OrderID = od.OrderID
JOIN Products p ON od.ProductID = p.ProductID
ORDER BY o.OrderID, od.OrderDetailID;

-- 5. Xem inventory status
SELECT i.Name, i.StockQuantity, 
       u.Value as Unit,
       s.SupplierName
FROM Ingredients i
JOIN Setting u ON i.UnitID = u.SettingID
LEFT JOIN Suppliers s ON i.SupplierID = s.SupplierID
ORDER BY i.Name;
*/

