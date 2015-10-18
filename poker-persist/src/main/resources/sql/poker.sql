insert into pokerdb.users (balance, username, password)
	values(
		10000.00,
		'asd',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq'
	);
	
insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd2',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		TRUE
	);
	
insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd3',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		FALSE
	);
	
insert into pokerdb.poker_types (name) values('HOLDEM');
insert into pokerdb.poker_types (name) values('CLASSIC');

insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('szerver', 1, 5, 3, 4.454, 102);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('szerver2', 2, 39, 5, 100, 150.15);