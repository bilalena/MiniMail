# MiniMail

A desktop email client developed in Java.

## Description

MiniMail is a desktop application for sending and receiving emails.
It uses a graphical user interface built with Java Swing and stores
email data in an embedded Apache Derby database.

## Features

- Send emails via SMTP
- Retrieve emails via POP3S
- Display received messages
- Store sent and received messages in a local database
- View email content

## Technologies

- Java
- Java Swing
- JDBC
- Apache Derby
- Jakarta Mail

## Database

The application uses an embedded Apache Derby database.

The database schema is provided in `database.sql`.

Before running the application, create the database and the three tables.
Add your own email account credentials to the `benutzer` table.

**Important:** The application stores the email password in plain text
in the local database. Do not publish your database or real credentials.

## Limitations

- Only supported text-based email formats are processed.
- HTML and image-based email content are not fully supported.
- The application is designed as a learning project.