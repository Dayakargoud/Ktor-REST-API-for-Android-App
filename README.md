#  simple RESTful web service in Kotlin for Android Notes App 

This is a very simple project using kotlin framework KTor.
Sample Notes API allows basic CRUD operations (immutable database) via REST API and JSON as data exchange format.
The project provides a set of APIs for a Android app or web app where one can manage Notes such as CRUD Operations.

I intend to enhance this project as I learn more about Kotlin and KTor.
I connected my own Notes App with this REST API where i can sync my notes to server in realtime.

This Notes Api uses Postgres database by default but you can change to other by passing DB_TYPE=postgres environment variable. 
The connection settings can also be passed via environment variables (please check application.conf)
