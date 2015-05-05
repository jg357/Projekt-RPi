package pir;

#############################################################################################
# Name: pir.pm                                                                              #
# Funktion: Diese Modul bietet eine Funktion, die wartet bis eine Bewegung vom PIR Seonsor  #
#           erkannt wurde und dann eine 1 zurückliefert.                                    #
#                                                                                           #
# Hinweis: Alle Funktionen wurden Exportiert                                                #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

# Modul für Zugriff auf die GPIO Pins des Raspberry Pi
use Device::BCM2835;

use strict;
use Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(getPir);

# Variable des GPIO Pins deklarieren (nach Hardware Pinnummer)
my $pin = Device::BCM2835::RPI_GPIO_P1_12;

sub getPir{
	# BCM2835 initialisieren
	Device::BCM2835::init() || die "Could not init library";
	
	# Pin auf Eingang schalten
	Device::BCM2835::gpio_fsel($pin,
		Device::BCM2835::BCM2835_GPIO_FSEL_INPT);

	# Warten bis status vom Bewegungssensor auf 0 steht, damit nicht dauerhaft ausgeloesst wird
	while(Device::BCM2835::gpio_lev($pin)){
		Device::BCM2835::delay(200);
	}

	# Dauerschleife, bis Status des Sensors 1 ist
	while(1){
		if(Device::BCM2835::gpio_lev($pin) == 1){
			return 1;
		}
		# Warte halbe Sekunde zwischen den Messungen
		Device::BCM2835::delay(500);
	}
}
1;
