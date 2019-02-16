create table Users (
  id varchar(32) not null,
  lang varchar(5) default "en_US",

  primary key(id)
);

create table Guilds (
  id varchar(32) not null,
  default_hook varchar(32),

  primary key(id)
);

create table LocalPrefixes (
  prefix varchar(32) not null,
  guild varchar(32) not null,

  foreign key(guild) references Guilds(id)
);

create table Badwords (
  id int not null auto_increment,
  word varchar(256) not null,
  guild varchar(32) not null,
  active tinyint(1) not null default 1,

  primary key(id),
  foreign key(guild) references Guilds(id)
);

create table BadwordPunishment (
  badword int not null,
  duser varchar(32) not null,
  date datetime not null default current_timestamp,

  foreign key(badword) references Badwords(id),
  foreign key(duser) references Users(id)
);

create table Statistics (
  id int not null auto_increment,
  date datetime not null,

  primary key(id)
);

create table TicTacToeStatistics (
  id int not null,
  thetable varchar(9) not null,

  primary key(id),
  foreign key(id) references Statistics(id)
);

create table TTTPlayers (
  statistic int not null,
  duser varchar(32) not null,
  slot tinyint(1) not null default 0,
  winner tinyint(1) not null default 1,

  foreign key(statistic) references TicTacToeStatistics(id),
  foreign key(duser) references Users(id)
);

create table Permissions (
  guild varchar(32),
  role varchar(32),
  permission varchar(128) not null,
  type tinyint(1) not null default 0,
  is_default tinyint(1) not null default 0,

  foreign key(guild) references Guilds(id)
);

create table Tokens (
  token varchar(256) not null,
  duser varchar(32) not null,
  type varchar(16) not null,
  expires_in datetime not null,
  auth_time datetime not null,

  primary key(token),
  foreign key(duser) references User(id)
);

create table TokenScopes (
  token varchar(256) not null,
  scope varchar(32) not null,

  foreign key(token) references Tokens(token)
);