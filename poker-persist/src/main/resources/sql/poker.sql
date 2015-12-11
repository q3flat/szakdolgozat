INSERT INTO POKERDB.USERS (BALANCE, USERNAME, PASSWORD, ADMIN) VALUES(10000.00,'ADMIN','$2a$10$lSHTlL25aQWKccaPoiIJGu8wVjUOE1hjzy5Ukz8UCS/pavYQV2R9O',TRUE);
INSERT INTO POKERDB.USERS (BALANCE, USERNAME, PASSWORD, ADMIN) VALUES(10000.00,'test','$2a$10$irXO8m2OkL3N4xdZ3i/gMegChHBaC2KGwrqqFuO3/OuQLj6.S96m2',FALSE);
INSERT INTO POKERDB.USERS (BALANCE, USERNAME, PASSWORD, ADMIN) VALUES(10000.00,'test2','$2a$10$irXO8m2OkL3N4xdZ3i/gMegChHBaC2KGwrqqFuO3/OuQLj6.S96m2',FALSE);
INSERT INTO POKERDB.USERS (BALANCE, USERNAME, PASSWORD, ADMIN) VALUES(10000.00,'test3','$2a$10$irXO8m2OkL3N4xdZ3i/gMegChHBaC2KGwrqqFuO3/OuQLj6.S96m22',FALSE);
INSERT INTO POKERDB.USERS (BALANCE, USERNAME, PASSWORD, ADMIN) VALUES(10000.00,'test4','$2a$10$irXO8m2OkL3N4xdZ3i/gMegChHBaC2KGwrqqFuO3/OuQLj6.S96m22',FALSE);
	
INSERT INTO POKERDB.POKER_TYPES (NAME) VALUES('HOLDEM');
INSERT INTO POKERDB.POKER_TYPES (NAME) VALUES('CLASSIC');

INSERT INTO POKERDB.POKER_TABLES (NAME, POKER_TYPE_ID, MAX_TIME, MAX_PLAYERS, BIG_BLIND) VALUES('szerver1',1,5,5,100);
INSERT INTO POKERDB.POKER_TABLES (NAME, POKER_TYPE_ID, MAX_TIME, MAX_PLAYERS, BIG_BLIND) VALUES('#PL_125Q',2,39,5,200);
INSERT INTO POKERDB.POKER_TABLES (NAME, POKER_TYPE_ID, MAX_TIME, MAX_PLAYERS, BIG_BLIND) VALUES('Holdem fun',1,15,5,100);
INSERT INTO POKERDB.POKER_TABLES (NAME, POKER_TYPE_ID, MAX_TIME, MAX_PLAYERS, BIG_BLIND) VALUES('Classic fun',2,5,5,20);
INSERT INTO POKERDB.POKER_TABLES (NAME, POKER_TYPE_ID, MAX_TIME, MAX_PLAYERS, BIG_BLIND) VALUES('?^xW!',2,23,3,1);
INSERT INTO POKERDB.POKER_TABLES (NAME, POKER_TYPE_ID, MAX_TIME, MAX_PLAYERS, BIG_BLIND) VALUES('űáűúüüéáűúőöüóÍ',1,33,2,10);