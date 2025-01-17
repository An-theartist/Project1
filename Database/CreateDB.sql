Create database Project1;
Use Project1;

Create table SV(
MSSV int not null primary key,
HoTen nvarchar(50));

Create table Lophoc(
MaLop int not null primary key,
MaMon varchar(10),
TenMon nvarchar(50));

Create table DiemSV(
MSSV int not null primary key,
MaLop int not null,
Diem int,
Foreign key(MSSV) references SV(MSSV),
foreign key(MaLop) references Lophoc(MaLop));

