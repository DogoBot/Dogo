create table Users (
  id varchar(32) not null,
  lang varchar(5) default "en_US",

  primary key(id)
);

create table Guilds (
  id varchar(32) not null,
  dafault_hook varchar(32),

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
  active bit(1) not null default 1,

  primary key(id),
  foreign key(guild) references Guilds(id)
);

create table BadwordPunishment (
  badword int not null,
  duser varchar(32) not null,

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
  slot bit(1) not null default 0,
  winner bit(1) not null default 1,

  foreign key(statistic) references TicTacToeStatistics(id),
  foreign key(duser) references Users(id)
);

create table PermGroups (
  id int not null auto_increment,

  primary key(id)
);

create table Permissions (
  permgroup int not null,
  permission varchar(128) not null,
  type bit(1) not null default 0,

  foreign key(permgroup) references PermGroups(id)
);

create table UserBoundPermGroup (
  id int not null,
  duser varchar(32) not null,

  primary key(id),
  foreign key(id) references PermGroups(id),
  foreign key(duser) references Users(id)
);

create table RoleBoundPermGroup (
  id int not null,
  role varchar(32) not null,

  primary key(id),
  foreign key(id) references PermGroups(id)
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