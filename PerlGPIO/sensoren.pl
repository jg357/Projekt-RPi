#!/usr/bin/perl

#############################################################################################
# Name: sensoren.pl                                                                         #
# Funktion: In diesem Skript sind die Module zum ansteuern der Sensoren eingebunden.        #
#                                                                                           #
# Aufruf: sensoren.pl [Sensor ID] {on(1)/off(0)}                                            #
#         Sensor ID: 1 - Temperatursensor (Ausgabe: Luftfeuchtigkeit und Temperatur)        #
#                    2 - Bewegungssensor (Wartet auf Bewegung, dann Rückgabe 1)             #
#                    3 - Funksteckdosen [switch] [1(on)|2(off)]                             #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

# Unterordner module einbinden
use Cwd qw(abs_path);
use FindBin;
use lib abs_path("$FindBin::Bin/module");

use dht22;
use pir;
use rcswitch;

use Switch;

# Anzahl der Übergabeparameter bestimmen
$parameter = @ARGV;

if($parameter < 1){
	print("Usage: sensoren.pl [Sensor ID]\n");
	exit;
}

$sensor = $ARGV[0];
$gpio = $ARGV[1];
$on = 0;

# Bei Auswahl von Funksteckdose müssen weitere Parameter übergeben werden
if($sensor == 3){
	if(!(exists $ARGV[2])){
		print("Usage: sensoren.pl [Sensor ID] [Switch Code] [1/0]\n");
		exit;
	}
	$switch = $ARGV[1];
	$on = $ARGV[2];
}

$a = 0;

switch ($sensor) {
	case 1		{($temp, $hum) = getTemp;
				 print("$temp $hum\n");
				}
	case 2		{	do{
						if(getPir()){
							print("Bewegung\n");
							$a = 1;
						}
					}while($a != 1);
				}
	case 3		{	switchRc($switch, $on);
					#$bin = "./module/driver/send 10110 $switch $on";
					#`$bin`;
				}
}
