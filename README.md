#AirlineAlerter


Airline ticket fare change alerter application.

##Building
###Installing new version of WebHarvest
Follow the steps:
* Checkout the fresh version from master at https://github.com/lexaux/web-harvest
* Build it with mvn clean package. This would build a webharvest-core/target/webharvest-core-2.1.0-SNAPSHOT-LEXAUX.jar
file.
* Issue a shell command to install the obtained jar library to the local maven repository (assuming you are at the project root).
```
mvn install:install-file -Dfile=path-to-your-artifact.jar -DlocalRepositoryPath=lib -Dpackaging=jar -DgroupId=net.sourceforge.web-harvest -DartifactId=webharvest-core -Dversion=2.1.0-SNAPSHOT-LEXAUX
```
Maven coordinates would be automatically obtained from the pom packaged within the jar.