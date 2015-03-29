wildlife-camera-machine 
=======================

![build status](https://travis-ci.org/windbender/wildlife-camera-machine.svg?branch=master) 

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

ToTo ( how to pitch in )
======================
Currently this project needs
* testing  - submit problems to the "issues" part of github.
* better use of layout/css within bootstrap to up the visual quality.  pull requests please!
* a better name -  I purposely made WLCDM as bad as possible so that I'd get something better.  and I still haven't.  send 'em!
* If you have experience with wildlife camera arrays/grid and how this data is analyzed, I would love to push in that direction.

QuickStart
===============================
install required packages
* JDK
* MySQL (or equivalent, I haven't tried postgres, but this project doesn't use anything fancy )
* an amazon S3 account  ( it might start without this, but you won't be able to upload pictures unless you set the "amazon" field to false in the config in which case you get a file system image storage.
* an SMTP server capable of sending emails.

clone the project:
```
git clone https://github.com/windbender/wildlife-camera-machine.git
```

build the project:
```
mvn clean package
```

create a database and database user:
```
create database wlcdm;
create user 'wlcdm'@'localhost' identified by 'supersecret';
grant all on wlcdm.* to 'wlcdm'@'localhost';
```

setup the config file:
```
cp serverconfig.example.yml serverconfig.yml
vi serverconfig.yml
```

Now edit serverconfig.yml.  replace all instances of <something> with more appropriate data.

create database tables ( this command uses liquibase to make sure your DB is up to date)
```
java -jar target/wlcdmsrv.jar  db migrate serverconfig.yml
```
start server:
```
java -jar target/wlcdmsrv.jar server serverconfig.yml
```

and go visit it:
http://localhost:8080/




