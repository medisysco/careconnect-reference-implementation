
INSERT INTO `Location` (`LOCATION_ID`,`RES_DELETED`,`RES_CREATED`,`RES_MESSAGE_REF`,`RES_UPDATED`,`name`,`status`,`managingOrganisation`,`physicalType`,`type`)
VALUES (1,NULL,NULL,NULL,NULL,'Long Eaton Clinic',0,2,479,463);

INSERT INTO `LocationTelecom` (`LOCATION_TELECOM_ID`,`system`,`telecomUse`,`value`,`LOCATION_ID`) VALUES (1,0,1,'0115 855 4034',1);
INSERT INTO `LocationTelecom` (`LOCATION_TELECOM_ID`,`system`,`telecomUse`,`value`,`LOCATION_ID`) VALUES (2,1,1,'0532 123 4567',1);

INSERT INTO `LocationIdentifier` (`LOCATION_IDENTIFIER_ID`,`identifierUse`,`listOrder`,`value`,`SYSTEM_ID`,`LOCATION_ID`) VALUES (1,0,NULL,'RTG08',2,1);

INSERT INTO `LocationAddress` (`LOCATION_ADDRESS_ID`,`addressType`,`addressUse`,`ADDRESS_ID`,`LOCATION_ID`) VALUES (1,0,0,6,1);