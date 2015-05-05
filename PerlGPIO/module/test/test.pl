#!/usr/bin/perl

#############################################################################################
# Name: test.pl                                                                             #
# Funktion: Testskript für die Module mit Rückgabewert.                                     #
#                                                                                           #
# Aufruf: test.pl                                                                           #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

#Verzeichnis Oberhalb einbinden
use Cwd qw(abs_path);
use FindBin;
use lib abs_path("$FindBin::Bin/../");

use dbselect;

use Test::Simple tests => 6;

# Modul dbselect.pm testen
print("Test der Funktionen des Moduls dbselect.pm\n");

&db_connect("127.0.0.1", 3306, "projekt_pi", "user", "raspberry");

print("Funktion getPictures = ");
ok(&getPictures() =~ /\A(201[5-9]-[0-1][0-9]-[0-3][0-9]_[0-2][0-9]-[0-6][0-9]-[0-6][0-9]\.jpeg ){9}(201[5-9]-[0-1][0-9]-[0-3][0-9]_[0-2][0-9]-[0-6][0-9]-[0-6][0-9]\.jpeg)$/);
print("Funktion getSwitchTime = ");
ok(&getSwitchTime(1) =~ /\A[0-9]+:[0-6][0-9]:[0-6][0-9]$/);
print("Funktion getDayTemp = ");
ok(&getDayTemp(1) =~ /\A([0-9]{1,3}\.?[0-9]* ){23}([0-9]{1,3}\.?[0-9]*)$/);
print("Funktion getThreeDayTemp = ");
ok(&getThreeDayTemp(1) =~ /\A([0-9]{1,3}\.?[0-9]* ){23}([0-9]{1,3}\.?[0-9]*)$/);
print("Funktion getSevenDayTemp = ");
ok(&getSevenDayTemp(1) =~ /\A([0-9]{1,3}\.?[0-9]* ){23}([0-9]{1,3}\.?[0-9]*)$/);
print("Funktion getAvg = ");
ok(&getAvg() =~ /\A([0-9]{1,3}\.[0-9]{2} ){5}([0-9]{1,3}\.[0-9]{2})$/);


&db_disconnect();
