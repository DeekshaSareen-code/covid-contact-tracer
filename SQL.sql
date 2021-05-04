create table contacts (
    contact_ID int primary key not null auto_increment,
	DeviceHash varchar(80),
	DateofContact int(11),
	Duration int(11),
	ContactdeviceHash varchar(80)
);
create table Testing (
    TestHash varchar(80) primary key not null,
    TestDate int(11) ,
	Result enum('true','false')
);
create table positiveResult (
    DeviceHash varchar(100) not null,
	Test_Hash varchar(80) primary key not null,
	foreign key(Test_Hash) references Testing(TestHash)
);

