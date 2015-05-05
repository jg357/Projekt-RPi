package dht22;

#############################################################################################
# Name: dht22.pm                                                                            #
# Funktion: Diese Modul bietet eine Funktion um die aktuelle Temperatur und Luftfeuchtigkeit#
#           des DHT22 Sensors auszulesen.                                                   #
#           Dies geschiet mithilfe des Treibers im Unterordner driver.                      #
#                                                                                           #
# Hinweis: Alle Funktionen wurden Exportiert                                                #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

use Cwd qw(abs_path);
use FindBin;

use strict;

use Exporter;
our @ISA = qw(Exporter);
our @EXPORT = qw(getTemp);

# Variable des GPIO Pins deklarieren (nach WiringPi Schema)
my $GPIO = 7;

# Relativen Pfad des Treibers für den Sensor mit Aufruf der Pin Nummer
my $bin = abs_path("$FindBin::Bin/module/")."/driver/dht22_driver $GPIO";

# Subroutine um die aktuelle Temperatur und Luftfeuchtigkeit zu erhalten
sub getTemp{
	my $a;
	my @werte;
	my $temp;
	my $result;
	do{
	
		# Treiber aufrufen und Ergebnis in $result schreiben
		chomp($result = `$bin`);
		
		# Prüfen ob der Treiber richtige Werte zurückgibt
		if($result eq "Data not good, skip"){
			$a ++;
			
			# Nach 5 Fehlerhaften versuchen mit Rückgabewert 1 abbrechen
			if($a == 5){
				return 1;
			}
		}else{
			# Aus Rückgabestring die Temperatur herausfiltern
			@werte = split(" ", $result);
		}
	}while($result eq "Data not good, skip");
	
	return $werte[2], $werte[6];
}
1;
