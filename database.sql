CREATE TABLE benutzer(
                         iNummer int NOT NULL GENERATED ALWAYS AS IDENTITY(
                             START WITH 1,
        INCREMENT BY 1
    ),
                         name varchar(255) NOT NULL,
                         email varchar(255) NOT NULL,
                         passwort varchar(255) NOT NULL,
                         PRIMARY KEY (iNummer)
);

CREATE TABLE gesendet(
                         iNummer int NOT NULL GENERATED ALWAYS AS IDENTITY(
                             START WITH 1,
        INCREMENT BY 1
    ),
                         empfaenger varchar(255) NOT NULL,
                         betreff varchar(255),
                         inhalt CLOB(64 K),
                         PRIMARY KEY (iNummer)
);

CREATE TABLE empfangen(
                          iNummer int NOT NULL GENERATED ALWAYS AS IDENTITY(
                              START WITH 1,
        INCREMENT BY 1
    ),
                          sender varchar(255) NOT NULL,
                          betreff varchar(255),
                          inhalt CLOB(64 K),
                          PRIMARY KEY (iNummer)
);