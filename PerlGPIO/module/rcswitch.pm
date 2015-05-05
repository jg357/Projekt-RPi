package rcswitch;

#############################################################################################
# Name: rcswitch.pm                                                                         #
# Funktion: Dieses Modul bietet eine Funktion mit welcher die Funksteckdosen ueber einen    # 
#           433Mhz Sender geschaltet werden koennen.                                        #
#           Die Methode switchRc benoegt als bergabeparemeter den Unit-Code der Steckdose   #
#           und den Zustand (0 aus, 1 ein).                                                 #
#                                                                                           #
# Hinweis: Alle Funktionen wurden Exportiert                                                #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

#Unit-Code berechnen:
#A = 1, B = 2, C = 4, D = 8, E = 16  
#Alle Dipschalter welche den Zustand 1 (oben) haben werden addiert
            

use Exporter;
@ISA = qw(Exporter);
@EXPORT = qw(switchRc);

# Hardwarezugriff auf GPIO Pins
use Device::BCM2835;
# Für sleep in Mikrosekunden
use Time::HiRes;

$repeat = 10; # Anzahl der uebertragungen
$pulselength = 350; # Pulslaenge in Mikrosekunden

@system_code = (1,0,1,1,0);

# Pin nach Hardware Schema festlegen
$pin = Device::BCM2835::RPI_GPIO_P1_16;

sub switchRc($$){
	
	# Unit Code Eingestellt nach den Dip-Schaltern an der Steckdose
	$unit_code=shift;
	# 1 = on, 0 = off
	$on_off=shift;

	# Standart Code fuer die Elro Funksteckdosen
	@bit = (142, 142, 142, 142, 142, 142, 142, 142, 142, 142, 142, 136, 128, 0, 0, 0);

	# Je nach System Code bestimmt bits auf 136 setzten
	foreach(0..4){
		if($system_code[$_] == 1){
			$bit[$_]=136;
		}
	}

	$x=1;

	# Nach Unit Code bestimmte Bits auf 136 setzen
	foreach $i (1..5){
		# Wenn Unit Code Bitweise mit $x und verknuepft groesser ist als 0
		if (($unit_code & $x) > 0){
			$bit[4+$i] = 136;
		}
		# Bitweises Shift left von $x
		$x = $x << 1;
	}

	# Wenn Steckdosen angeschalten werden sollen
	if ($on_off == 1){
		$bit[10] = 136;
		$bit[11] = 142;
	}

	# Array in dem die einzelnen Bits gespeichert werden
	@bits;

	# Code in Bit Code umwandeln
	foreach $y (0..15){
		$x = 128;
		foreach $i (1..8){
			$b = ((($bit[$y] & $x) > 0) ? 1 : 0);
			push(@bits, $b);
			$x = $x >> 1;
		}
	}

	# BCM2835 initialisieren, gewaehlten Pin auf OUTPUT setzen
	Device::BCM2835::init() || die "Could not init library";
	Device::BCM2835::gpio_fsel($pin, &Device::BCM2835::BCM2835_GPIO_FSEL_OUTP);
	Device::BCM2835::gpio_write($pin, 0);

	# Bits mit einem delay von 300 Mikrosekunden abschicken
	foreach $z (0..$repeat){
		foreach $b (@bits){
			#($s1,$m1)=Time::HiRes::gettimeofday;	
			Device::BCM2835::gpio_write($pin, $b);
			Time::HiRes::usleep($pulselength);
			#($s2,$m2)=Time::HiRes::gettimeofday;
			#print(($m2 - $m1)."\n");
		}
	}
	return 0;
}
1;

