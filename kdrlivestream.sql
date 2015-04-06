--
-- Table structure for table `kdrlivestream-lastseen`
--

CREATE TABLE IF NOT EXISTS `kdrlivestream-lastseen` (
  `streamname` varchar(50) NOT NULL,
  `userindex` int(11) NOT NULL,
  `userispublishing` tinyint(1) NOT NULL DEFAULT '0',
  `public` tinyint(1) NOT NULL DEFAULT '0', 
  `lastseen` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `streamuser` (`streamname`,`userindex`),
  KEY `userindex` (`userindex`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `kdrlivestream-users`
--

CREATE TABLE IF NOT EXISTS `kdrlivestream-users` (
  `index` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `passhash` varchar(40) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `canpublish` tinyint(1) NOT NULL DEFAULT '0',
  `allowmultipleinstances` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`index`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Constraints for table `kdrlivestream-lastseen`
--
ALTER TABLE `kdrlivestream-lastseen`
  ADD CONSTRAINT `kdrlivestream-lastseen_ibfk_1` FOREIGN KEY (`userindex`) REFERENCES `kdrlivestream-users` (`index`) ON DELETE CASCADE ON UPDATE CASCADE;
