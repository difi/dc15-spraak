
# dc15-spraak

An awesome application that crawles the web, and beyond, to find text.  The program then proceeds to analyze whether the language at hand is the powerfull new norwegian, or the mighty book goal. Statistical fun will ensue! Enjoy travelers, and behold the mightyness of the norwegian language!

#Korleis kome i gang
## Tekniske krav
###spraak
* Java 8.0
* [Maven](https://maven.apache.org/) for å handsame dependecies. 
	* Sjå `pom.xml` for Maven-dependencies.


### spraak_frontend
* [NodeJS](https://nodejs.org/)

For å køyre testserver, kan du anten køyre den manuelt, eller installere [Supervisor](http://supervisord.org/index.html) og køyre kommandoen
```
supervisor -e 'html|js|css' node bin/www
```
medan du er i /spraak_frontend. 
Supervisor restartar automatisk kvar gong det blir gjort endringar. Om du vil restarte manuelt, kan du køyre kommandoen
```
rs
```
Om du køyrer Windows, vil du kanskje trenge eit alternativt windows-shell for dette, f.eks. [Babun](http://babun.github.io/)

Sida vil vere tilgjengeleg på [http://localhost:3002/](http://localhost:3002/)

