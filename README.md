wildlife-camera-machine
=======================

This project aims to make the categorization and study of wildlife camera pictures fun and easy.

Three easy steps:

* Upload
* Categorize
* Report

Technology
============
Dropwizard ( https://dropwizard.github.io/dropwizard/ )

AngularJS ( https://angularjs.org/ )

Relational Database (currently mysql, but postgres would also work )

Bootstrap ( http://getbootstrap.com/ )

ToTo
======================
Currently this project needs
* testing
* better use of layout/css within bootstrap to up the visual quality
* a better name


QuickStart
===============================
install required packages
* JDK
* MySQL (or equivalent, I haven't tried postgres, but this project doesn't use anything fancy )

clone the project:

git clone https://github.com/windbender/wildlife-camera-machine.git

build the project:

mvn clean package

create a database and database user:

create database wlcdm;
create user 'wlcdm'@'localhost' identified by 'supersecret';
grant all on wlcdm.* to 'wlcdm'@'localhost';

setup the config file:

vi serverconfig.yml
```
something
```


create database tables:

java -jar target/wlcdmsrv.jar  db migrate serverconfig.yml

start server:

java -jar target/wlcdmsrv.jar server serverconfig.yml



