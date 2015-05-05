#!/usr/bin/perl

#############################################################################################
# Name: dbselect.pl                                                                         #
# Funktion: Bindet das Modul dbselect.pm ein, welches Funktionen bereitstellet um Daten     #
#           aus der Datenbank abzurufen.                                                    #
#                                                                                           #
# Aufruf: dbselect.pl [IP] [Port] [Name] [Benutzer] [Passwort] [Funktion] {Tabelle|Switch}  #
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

use dbselect;
use strict;
use warnings;
use Switch;

# Übergabeparameter abfangen
my $db_host = shift;
my $db_port = shift;
my $db_name = shift;

my $db_user = shift;
my $db_pass = shift;
my $switch = shift;

# Datenbankverbindung aufbauen
&db_connect($db_host,$db_port,$db_name,$db_user,$db_pass);

# Nach Übergebener Variable $switch die Funktion auswählen
switch ($switch){
	case 1	{print &getPictures();}
	case 2	{my $rc = shift; 
             print &getSwitchTime($rc);}
	case 3	{my $table = shift;
	         print &getDayTemp($table);}
	case 4	{my $table = shift;
			 print &getThreeDayTemp($table);}
	case 5	{print &getAvg;}
	case 6	{my $table = shift;
			 print &getSevenDayTemp($table);}
}

# Datenbankverbindung trennen
&db_disconnect;