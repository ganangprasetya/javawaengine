-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 21, 2018 at 05:46 AM
-- Server version: 10.1.28-MariaDB
-- PHP Version: 7.1.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `whatsapp`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `id` int(11) NOT NULL,
  `original` varchar(15) DEFAULT NULL,
  `token` varchar(100) NOT NULL,
  `url` varchar(255) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0=nonaktif, 1=aktif'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `original`, `token`, `url`, `status`) VALUES
(1, '6285213654343', '86p03sqihgcswv24', 'https://eu9.chat-api.com/instance8111/', 0),
(2, '6285881172081', 'r8goefk0lqq0bfp5', 'https://eu9.chat-api.com/instance7914/', 0),
(3, '6285213654343', 'dd0p452ij8x4zunj', 'https://eu9.chat-api.com/instance9651/', 1);

-- --------------------------------------------------------

--
-- Table structure for table `keywords`
--

CREATE TABLE `keywords` (
  `id` int(11) NOT NULL,
  `word` varchar(200) NOT NULL,
  `response` text NOT NULL,
  `note` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `keywords`
--

INSERT INTO `keywords` (`id`, `word`, `response`, `note`) VALUES
(1, 'HAI', 'Halo,Selamat Datang di HSBC Whatsapp', 'Salam '),
(2, 'DEFAULT', 'Maaf, keyword yang anda masukkan belum tersedia.', 'kalo salah'),
(3, 'CEK PROMO', 'Makin seru dukung Indonesia dengan Kartu Kredit HSBC. Dapatkan Tambahan diskon hingga 18% di 18 online merchant Hingga 31 Agustus 2018. Info Selengkapnya : https://www.hsbc.co.id/1/2/id/personal/offers/kartu-kredit/offers/kejutan1poin-ekstra', 'cek promo baru');

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `messageid` varchar(100) NOT NULL,
  `sender` varchar(13) DEFAULT NULL,
  `sendto` varchar(15) NOT NULL,
  `message` longtext,
  `messagetype` int(1) NOT NULL DEFAULT '1' COMMENT '1=message,2=file',
  `receivedate` datetime DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0=belum dikirim, 1=terkirim',
  `statusDescription` varchar(255) DEFAULT NULL,
  `sentdate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `messages_mo`
--

CREATE TABLE `messages_mo` (
  `id` int(11) NOT NULL,
  `sender` varchar(14) NOT NULL,
  `sendername` varchar(60) DEFAULT NULL,
  `sendto` varchar(14) NOT NULL,
  `message` varchar(100) NOT NULL,
  `receivedate` datetime NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0 : belum diproses 1:sudah diproses',
  `thirdid` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `keywords`
--
ALTER TABLE `keywords`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `messageid` (`messageid`);

--
-- Indexes for table `messages_mo`
--
ALTER TABLE `messages_mo`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `keywords`
--
ALTER TABLE `keywords`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=314;

--
-- AUTO_INCREMENT for table `messages_mo`
--
ALTER TABLE `messages_mo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
