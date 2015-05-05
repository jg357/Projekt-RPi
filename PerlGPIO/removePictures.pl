#!/usr/bin/perl

#############################################################################################
# Name: removePictures.pl                                                                   #
# Funktion: Diese Skript prüft bei Aufruf die Anzahl der gespeicherten Bilder im Ordner     #
#           /var/www/Bilder, bei mehr als 10 Bildern werden die ältesten gelöscht.          #
#                                                                                           #
# Aufruf: removePictures.pl                                                                 #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

use strict;

# Dateien aus Ordner in @Dateien schreiben
opendir(DIR,"/var/www/bilder");
my @Dateien = readdir(DIR);
closedir(DIR);

my $i=0;

# Prüfen wieviele Dateien mit der Endung .jpeg vorhanden sind
foreach (@Dateien) {
	if($_ =~ m/.jpeg$/){
		$i = $i + 1;
	}
}

my %hash;
my $alter;

# Bei mehr als 10 jpeg Dateien die Bilder in ein hash mit dem Alter als Schlüssel schreiben
if ($i > 10){
	foreach(@Dateien){
		if($_ =~ m/.jpeg$/){
			$alter = (-M "/var/www/bilder/".$_);
			$hash{$alter} = $_;
		}
	}

	# Das hash nach schlüssel, also nach dem Alter sortieren
	foreach(sort{$b <=> $a} keys(%hash)){
	
		# Solange Bilder löschen bis die Anzahl höchstens 10 ist
		if($i > 10){
			system("rm /var/www/bilder/".$hash{$_});
			$i = $i - 1;
		}
	}
}

