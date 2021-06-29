# Installationsanleitung (Jonas Picker)  

## Java Laufzeitumgebung  

Um dem Tomcat das finden der richtigen Laufzeitumgebung zu erleichtern, sollte diese bereits vor dessen Installation auf Ihrem Server zu finden sein. Zum Zeitpunkt der Entwicklung ist aktuellste Version 16 noch nicht voll Kompatibel mit allen von uns verwendeten Werkzeugen. Wir verwenden daher die JDK 15.0.2. Wählen Sie bitte die passende Version für Ihr Betriebssystem aus [dieser Liste](https://jdk.java.net/archive/) aus und laden Sie sie herunter. Da der Installationsprozess der offiziellen Java-Version der Firma Oracle sich etwas von dem der OpenJDK (welche ohne ausführliche Installationsanweisungen daherkommt) unterscheidet, sind hier Anleitungen von externen Quellen für die Betriebssysteme [Windows](https://www.codejava.net/java-se/download-and-install-openjdk-15), [Linux](https://www.linuxfromscratch.org/blfs/view/svn/general/openjdk.html) und [macOS](https://mkyong.com/java/how-to-install-java-on-mac-osx/). Erwähnenswert ist noch die Tatsache, dass der Tomcat als letztes Mittel die Umgebungsvariable JAVA\_HOME zum finden der JDK benutzt. Näheres entnehmen Sie bitte den Links im nun folgenden Abschnitt.

## Apache Tomcat  

Die Version 10.0.x des Apache Tomcat muss auf dem Server installiert sein. Die richtige Installationsanleitung für das auf dem Server installierte Betriebsystem ist unter [diesem Link](https://tomcat.apache.org/tomcat-10.0-doc/setup.html) zu finden. Sie müssen die entsprechende Datei dann [hier](https://tomcat.apache.org/download-10.cgi) herunterladen. Achten Sie besonders auf die Abschnitte, die das Setzen der Umgebungsvariablen CATALINA\_HOME und CATALINA\_BASE beschreiben.

## Registrieren des SSL-Zertifikats  

Um verschlüsselte Kundenkommunikation zu ermöglichen, muss auf dem Tomcat nun das geforderte SSL-Zertifikat initialisiert werden. Der genaue Ablauf ist mit weiteren Informationen zusammen unter [diesem Link](https://tomcat.apache.org/tomcat-10.0-doc/ssl-howto.html#Introduction_to_SSL) zu finden.

## Aufspielen der Anwendung  

Unsere Applikation wird im .war-Format ausgeliefert, welches, wie [hier](https://tomcat.apache.org/tomcat-10.0-doc/deployer-howto.html#Deployment_on_Tomcat_startup) beschrieben, auf verschiedenen Wegen auf den Tomcat Server aufgespielt ('deployed') werden kann. Wir empfehlen einen Weg zu wählen, der die Applikation nicht auf einen laufenden Tomcat aufspielt. Ansonsten kann wegen der noch leeren config.txt (siehe nächster Abschnitt) die Datenbankverbindung fehlschlagen und ein Neustart ist erforderlich.

## Verbindungsdaten des Datenbankservers  

Unsere Applikation sollte nun unter dem Tomcat Installationsverzeichnis in einem Ordner zu finden sein. Navigieren Sie vor dem Start der Anwendung in den Ordner /WEB-INF/ und öffnen Sie das Dokument config.properties. Die weiteren Schritte zum konfigurieren des Systems finden Sie dort beschrieben. 

## Starten der Webanwendung  

Nun sollten alle nötigen Schritte zur Installation abgeschlossen sein und Sie können die Webanwendung z.B. auf dem direkten Weg starten, indem Sie im Verzeichnis der gesetzten Umgebungsvariable CATALINA_HOME den Ordner /bin ansteuern und startup.sh aufrufen. Zum Abschalten des Tomcat wählen Sie dann shutdown.sh im gleichen Ordner.
