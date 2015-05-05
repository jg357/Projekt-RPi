# Projekt-RPi

Dieses Projekt hat das Ziel eine Hausüberwachung und -automation für den Raspberry Pi zu entwickeln.

Die Software ist für den Raspberry Pi geschrieben und ist auch nur Vollständig lauffähig, 
wenn sie auf einem Raspberry Pi mit den folgenden Voraussetzungen gestartet wird.

Allgemein:
- Mysql Server (mit Datenbank projekt_pi)
- Apache Server mit Unterordner /var/www/bilder (für Bilder Freigaben)
- BCM 2835 Bibliothek (zur ansteuerung der GPIO Pins)
- WiringPi (für den Treiber des DHT22 sensors)
- das init.d Skript muss in den Ordner /etc/init.d kopiert werden (um den Server mit dem service Befehl zu starten)
- Pfade der Ordner: /home/pi/Projekt/Java   /home/pi/Projekt/PerlGPIO (Können auch verschoben werden, müssen aber nebeneinander liegen)

Java:
- jre für linux

Perl:
- Modul Moose
- Modul Device::BCM2835
- Modul DBI
