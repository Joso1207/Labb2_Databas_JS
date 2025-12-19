## OM

Compentecy Control #2 för Chas Academy Fullstack JAVA; Databas.

## Instruktioner


Klona projektet och kör från main,  detta skapar upp databasen så att dens struktur syns.
Kör därefter testerna för att see ifall klasserna fungerar,  Detta kan göras via .\gradlew test

Data för test skapas automatiskt (och även slängs då Transactional Rollbacks händer per default)

## Teknisk info och designval.

Vi delar självklar upp entiteter och der dem var sin repository,
för att kommunicera mot dessa ska vi använda oss utav service lagret.  Dock har vi bara användning utav TransactionService i denna uppgift.
Vi skapar även en sekundär service för att logga transaktionsfel till databas.

## Felhantering

Felhantering sköts via undantag,  och valet föll på att göra en stark separation mellan användarfel och systemfel.
Därför väljer vi att inte logga fel som orsakas utav fel inmatning eller liknande utan dessa skötes lokalt och ej utav databas.
Systemfel eller fel under transaktion kommer därimot att loggas i en separat och isolerad databastransaktion som skapas när felet uppstår.
Denna transaktion är helt isolerad från den tidigare och skötes därför individuelt enligt ACID,  I = Isolation.

Testerna täcker flera scenarion, alltifrån att varje del gör sitt jobb, att databasen är korrekt uppbygd, samt att användarfel och systemfel kastar rätt undantag.




