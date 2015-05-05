#!/usr/bin/perl

#############################################################################################
# Name: dbinsert.pl                                                                         #
# Funktion: Bindet das Objektorientierte Modul dbinsert.pm ein um mit den Funktionen        #
#           die Werte der Sensoren in die Datenbank zu schreiben.                           #
#                                                                                           #
# Aufruf: dbinsert.pl [IP] [Port] [Name] [Benutzer] [Passwort] {Funktionsspezifische Werte} #
#                                                                                           #
# Hinweis: In diesem Skript wird nicht auf Fehlerhaften Aufruf geprüft, da es               #
#          nur zum Aufruf aus der Java Serveranwendung geschrieben wurde!                   #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

# Das Unterverzeichnis "module" einbinden
use Cwd qw(abs_path);
use FindBin;
use lib abs_path("$FindBin::Bin/module");

use dbinsert;

# Anzahl der Übergabeparameter bestimmen
$anzahl = @ARGV;

# Übergabeparameter in Variablen schreiben
$db_host = shift;
$db_port = shift;
$db_name = shift;

$db_user = shift;
$db_pass = shift;

# Objekt des Moduls dbinsert mit den Variablen initialisieren
$db1=dbinsert->new(db_host=>$db_host, db_port=>$db_port, db_name=>$db_name, db_user=>$db_user, db_pass=>$db_pass);

# Datenbankverbindung herstellen
$dbh=$db1->db_connect();

# Bei 9 Parametern Aufruf der Funktion für die Tabelle zeitschaltplan
if($anzahl == 9){
	$rcswitch = shift;
	$room = shift;
	$on_time = shift;
	$off_time = shift;

	$db1->db_insert_zeitschaltplan($dbh, $rcswitch, $room, $on_time, $off_time);
	$db1->db_disconnect($dbh);
}

# Bei 8 Parametern Aufruf der Funktion für die Tabelle Thermosensor
if($anzahl == 8){
	$db_hum = shift;
	$db_temp = shift;
	
	$db_room = shift;

	$db1->db_insert_temp($dbh, $db_hum, $db_temp, $db_room);
	$db1->db_delete_temp($dbh);
	$db1->db_disconnect($dbh);
}

# Bei 7 Parametern Aufruf der Funktion für die Tabelle Bilder
if($anzahl == 7){

	$db_image = shift;
	$db_room = shift;
	
	# String für Kommandozeilenaufruf der Kamera
	#$take_image = "raspistill -o /var/www/bilder/$db_image -n -w 530 -h 700 -t 1000";
	$take_image = "raspistill -o /var/www/bilder/$db_image -n -w 1000 -h 1400 -t 1000";	

	$db1->db_insert_movement($dbh, $db_image, $db_room);
	$db1->db_delete_pictures($dbh);
	$db1->db_disconnect($dbh);
	
	# Beim insert in Tabelle Bilder wird zusätzlich ein Bild geschossen
	$ok = `$take_image`;
	print("$ok");
	
	# Ruft das Skript auf mit dem die Bilder ab einer bestimmten Anzahl wieder gelöscht werden
	system(abs_path("$FindBin::Bin/")."/removePictures.pl");
}
