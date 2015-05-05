package dbselect;

#############################################################################################
# Name: dbselect.pm                                                                         #
# Funktion: Dieses Modul stellt Funktionen für den Datenbankselect aus der Datenbank des    #
#           Projektes Raspberry Pi bereit.                                                  #
#                                                                                           #
# Hinweis: Alle Funktionen wurden Exportiert                                                #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

use Exporter;
@ISA = qw(Exporter);
@EXPORT = qw(db_connect db_disconnect getPictures getSwitchTime getDayTemp getThreeDayTemp getAvg getSevenDayTemp);

use strict;
use DBI;

# offene Verbindung zur MySQL-Datenbank
my $dbh;                 

# Datenbankverbindung aufbauen
sub db_connect{
	my $db_host = shift;
	my $db_port = shift;
	my $db_name = shift;

	my $db_user = shift;
	my $db_pass = shift;

	# Connection-String definieren
	my $dsn = "DBI:mysql:database=$db_name;host=$db_host;port=$db_port";

	# Verbimdung aufbauen
	$dbh = DBI->connect( $dsn, $db_user, $db_pass ) or die;
	return 1;
}

# Datenbankverbindung trennen
sub db_disconnect{
	
	$dbh->disconnect() or die;
	return 1;
}

# Gibt alle Bildernamen als mit Leerzeichen getrennten String zurück
sub getPictures{
	
	my $query = "select Bilder from projekt_pi.bilder";
	my $query_test = $dbh->prepare($query);
	
	$query_test->execute() or die;
	
	my @werte;
	my $col;
	
	while($col = $query_test->fetchrow_array()){
		# Werte in umgekehrter Reihenfolge ins Array schreiben
		unshift(@werte, $col);
	}
	return "@werte";
}

# Gibt die Zeit, welche eine Bestimme Funksteckdose in den letzten 7 Tagen eingeschaltet war zurück
sub getSwitchTime($){
	
	# Funktionsspezifischer Übergabeparameter
	my $rc = shift;

	# $date auf dem Datumswert von vor 7 Tagen setzen, im Format JJJJ-MM-TT (Wichtig für Datenbankabfrage)
	my $date = `date +%Y-%m-%d -d "7 days ago"`;
	
	my $query = "select SEC_TO_TIME( SUM( TIME_TO_SEC( `Laufzeit` ) ) ) AS total_time from zeitschaltplan where idfunksteckdose in (select idfunksteckdose from funksteckdosen where Einschaltcode = $rc) and Einschaltzeitpunkt between '$date 00:00:00' and current_timestamp()";
				
	my $query_test = $dbh->prepare($query);
	
	$query_test->execute() or die;
	
	my $col = $query_test->fetchrow_array();
	
	return $col;
}

# Gibt die Durchschnittstemperatur pro Stunde in einen mit Leerzeichen getrennten String für die letzten 24 Stunden des Vortages zurück. 
sub getDayTemp($){
	
	# Funktionsspezifischer Übergabeparameter
	my $table = shift;
	
	# $day den gestrigen Tag zuweisen
	my $day = `date +%d -d "Yesterday"`;

	my $col = 1;
	
	my $table1 = "temperatur";
	
	my $previous = 0;
	my @werte;
	# Datenbankabfrage der Durchschnittstemperatur für jede Stunde (zwischen 00:00:00 01:00:00 - 23:00:00 24:00:00)
	foreach(0..23){
	
		#Die Variablen $hour und $hour1 müssen immer aus zwei Ziffern bestehen (Für Datenbankabfrage)
		my $hour = "0";
		if ( $_ < 10 ){
			$hour = "0".$_;
		}else{
			$hour = $_;
		}
		my $hour1 = "0";
		if ( $_ < 9 ){
			$hour1 = "0".($_+1);
		}else{
			$hour1 = $_ +1;
		}
		
		# Auswahl der richtigen Tabelle nach Übergabeparameter
		if ($table == 1){
			$table1 = "temperatur";
		}else{
			$table1 = "luftfeuchtigkeit";
		}
		
		my $query = "select avg($table1) from thermosensor where tag = $day and zeitpunkt between '$hour:00:00' and '$hour1:00:00'";
		
		my $query_test = $dbh->prepare($query);
		
		$query_test->execute() or die;
		$col = $query_test->fetchrow_array();
		
		# Wenn kein Wert für die Stunde vorhanden war 0 oder vorherigen Wert verwenden
		if (!(defined $col)){
			$col = $previous;
		}
		
		# Werte in umgekehrter Reihenfolge ins Array schreiben
		unshift(@werte, $col);
		
	}
	
	return "@werte";
}

# Gibt die Durchschnittstemperatur von jeweils drei Stunden in einem mit Leerzeichen getrennten String für die letzten 3 Tage zurück. 
sub getThreeDayTemp($){
	
	# Funktionsspezifischer Übergabeparameter
	my $table = shift;
	
	# $day den Tag zuweisen
	my $days_ago = 3;
	my $day = `date +%d -d "$days_ago days ago"`;

	my $col = 1;
	
	my $table1 = "temperatur";
	
	my $previous = 0;
	my @werte;
	# Für drei Tage durchlaufen
	for(my $a = 0; $a < 3; $a ++){
		# Datenbankabfrage der Durchschnittstemperatur für alle 3 Stunden (zwischen 00:00:00 03:00:00 - 20:00:00 24:00:00)
		foreach(0,3,6,9,12,15,18,21){
		
			#Die Variablen $hour und $hour1 müssen immer aus zwei Ziffern bestehen (Für Datenbankabfrage)
			my $hour = "0";
			if ( $_ < 10 ){
				$hour = "0".$_;
			}else{
				$hour = $_;
			}
			my $hour1 = "0";
			if ( $_ < 7 ){
				$hour1 = "0".($_+3);
			}else{
				$hour1 = $_ +3;
			}
			
			# Auswahl der richtigen Tabelle nach Übergabeparameter
			if ($table == 1){
				$table1 = "temperatur";
			}else{
				$table1 = "luftfeuchtigkeit";
			}
			
			my $query = "select avg($table1) from thermosensor where tag = $day and zeitpunkt between '$hour:00:00' and '$hour1:00:00'";
			
			my $query_test = $dbh->prepare($query);
			
			$query_test->execute() or die;
			$col = $query_test->fetchrow_array();
			
			# Wenn kein Wert für die Stunde vorhanden war 0 oder vorherigen Wert verwenden
			if (!(defined $col)){
				$col = $previous;
			}
			$previous = $col;
			
			# Werte in umgekehrter Reihenfolge ins Array schreiben
			unshift(@werte, $col);
	
			if($hour1 eq "24"){
				$days_ago --;
				$day = `date +%d -d "$days_ago days ago"`;
			}
			
		}
	}
	
	return "@werte";
}

# Gibt die Durchschnittstemperatur von jeweils drei Stunden in einem mit Leerzeichen getrennten String für die letzten 3 Tage zurück. 
sub getSevenDayTemp($){
	
	# Funktionsspezifischer Übergabeparameter
	my $table = shift;
	
	# $day den Tag zuweisen
	my $days_ago = 7;
	my $day = `date +%d -d "$days_ago days ago"`;

	my $col = 1;
	my $table1 = "temperatur";
	my $previous = 0;
	my @werte;
	# Für drei Tage durchlaufen
		# Datenbankabfrage der Durchschnittstemperatur für alle 7 Stunden
	foreach(0,7,14,21,4,11,18,1,8,15,22,5,12,19,2,9,16,23,6,13,20,3,10,17){
		#Die Variablen $hour und $hour1 müssen immer aus zwei Ziffern bestehen (Für Datenbankabfrage)
		my $hour = "0";
		if ( $_ < 10 ){
			$hour = "0".$_;
		}else{
			$hour = $_;
		}
		my $hour1 = "0";
		
		if(($_ + 7) > 24){
			$hour1 = 24;
		}else{
			if ( $_ < 3 ){
				$hour1 = "0".($_+7);
			}else{
				$hour1 = $_ + 7;
			}
		}
	
		# Auswahl der richtigen Tabelle nach Übergabeparameter
		if ($table == 1){
			$table1 = "temperatur";
		}else{
			$table1 = "luftfeuchtigkeit";
		}
		
		my $query = "select avg($table1) from thermosensor where tag = $day and zeitpunkt between '$hour:00:00' and '$hour1:00:00'";
		
		my $query_test = $dbh->prepare($query);
		
		$query_test->execute() or die;
		$col = $query_test->fetchrow_array();
		
		# Wenn kein Wert für die Stunde vorhanden war 0 oder vorherigen Wert verwenden
		if (!(defined $col)){
			$col = $previous;
		}
		$previous = $col;
		
		# Werte in umgekehrter Reihenfolge ins Array schreiben
		unshift(@werte, $col);

		if($hour1 eq "24"){
			$days_ago --;
			$day = `date +%d -d "$days_ago days ago"`;
		}
			
	}
	
	return "@werte";
}


sub getAvg{
	
	my $date = `date +%d -d "Yesterday"`;
	
	my $query_get =  "select \@out";
	
	my @procedures = ("proc_avg_TagesLuft", "proc_avg_TagesTemp", "proc_max_TagesTemp", "proc_max_TagesLuft", "proc_min_TagesTemp", "proc_min_TagesLuft");
	
	my $string = "";
	my $query_temp;
	
	foreach(@procedures){
		$query_temp = "call $_($date, \@out)";
		my $query_test = $dbh->prepare($query_temp);
		$query_test->execute() or die;
		$query_test = $dbh->prepare($query_get);
		$query_test->execute() or die;
		my $col = $query_test->fetchrow_array();
		if(!($string)){
			$string = $col;
		}else{
			$string = $string." ".$col;
		}
	}
	return $string;
}

1;