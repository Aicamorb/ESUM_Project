-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 24, 2024 at 11:46 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `esundatabase`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `createOrder` (IN `OrderID` VARCHAR(16), IN `MemberID` VARCHAR(20), IN `Price` INT)   BEGIN
INSERT INTO `order`(`OrderID`, `MemberID`, `Price` ) VALUES (OrderID,MemberID,Price);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createOrderdetail` (IN `OrderID` VARCHAR(16), IN `ProductID` VARCHAR(4), IN `Quantity` INT, IN `StandPrice` INT)   BEGIN
declare ItemPrice int default 0;
declare pQuantity int;
set ItemPrice= StandPrice*Quantity;
INSERT INTO `orderdetail`( `OrderID`, `ProductID`, `Quantity`, `StandPrice`, `ItemPrice`) VALUES (OrderID,ProductID,Quantity,StandPrice,ItemPrice);
SELECT product.Quantity INTO pQuantity FROM `product` WHERE product.ProductID = ProductID;
UPDATE `product` SET `Quantity`=(pQuantity-Quantity) WHERE product.ProductID =ProductID;

 
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getProductInStock` ()   BEGIN
SELECT * FROM product  WHERE product.Quantity > 0;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getProductSelect` (IN `pID` VARCHAR(2000))   begin
 SET @sql = CONCAT('SELECT * FROM Product WHERE ProductID  IN (', pID, ')');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
end$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `newProduct` (IN `pID` VARCHAR(4), IN `pName` VARCHAR(50), IN `pPrice` INT, IN `pCount` INT)   BEGIN
	INSERT INTO `product`(`ProductID`, `ProductName`, `Price`, `Quantity`) VALUES (pID,pName,pPrice,pCount);
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

CREATE TABLE `order` (
  `OrderID` varchar(16) NOT NULL,
  `MemberID` varchar(20) NOT NULL,
  `Price` int(11) NOT NULL,
  `PayStatus` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `order`
--

INSERT INTO `order` (`OrderID`, `MemberID`, `Price`, `PayStatus`) VALUES
('Ms202405241509', 'cdy', 1345, 0),
('Ms202405241516', 'ertyuio', 1345, 0),
('Ms202405241717', 'Kop', 222, 0),
('Ms202405241725', 'KKKoo890', 211, 0);

-- --------------------------------------------------------

--
-- Table structure for table `orderdetail`
--

CREATE TABLE `orderdetail` (
  `OrderItemSN` int(11) NOT NULL,
  `OrderID` char(16) NOT NULL,
  `ProductID` char(4) NOT NULL,
  `Quantity` int(11) NOT NULL,
  `StandPrice` int(11) NOT NULL,
  `ItemPrice` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `orderdetail`
--

INSERT INTO `orderdetail` (`OrderItemSN`, `OrderID`, `ProductID`, `Quantity`, `StandPrice`, `ItemPrice`) VALUES
(25, 'Ms202405241509', '9p01', 1, 100, 100),
(26, 'Ms202405241509', 'p005', 1, 1245, 1245),
(27, 'Ms202405241516', '9p01', 1, 100, 100),
(28, 'Ms202405241516', 'p005', 1, 1245, 1245),
(29, 'Ms202405241717', 'p001', 2, 111, 222),
(30, 'Ms202405241725', '9p01', 1, 100, 100),
(31, 'Ms202405241725', 'p001', 1, 111, 111);

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `ProductID` char(4) NOT NULL,
  `ProductName` varchar(50) NOT NULL,
  `Price` int(11) NOT NULL,
  `Quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`ProductID`, `ProductName`, `Price`, `Quantity`) VALUES
('9p01', '商品6', 100, 6),
('p001', '商品1號', 111, 7),
('p004', '測試4', 111, 0),
('p005', '測試5', 1245, 0),
('p006', '商品6', 6666, 0),
('p010', '商品10', 100, 6),
('p013', '商品13', 1313, 10);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `order`
--
ALTER TABLE `order`
  ADD PRIMARY KEY (`OrderID`);

--
-- Indexes for table `orderdetail`
--
ALTER TABLE `orderdetail`
  ADD PRIMARY KEY (`OrderItemSN`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`ProductID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `orderdetail`
--
ALTER TABLE `orderdetail`
  MODIFY `OrderItemSN` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
