#!/usr/bin/perl

#########################################################################################
# Name: rcstate.pl                                                                      #
# Funktion: Dieses Skript bietet Funktionen mit denen man den Status der vorhandenen    #
# 			Funksteckdosen aus der Datenabank auslesen und ändern kann.                 #
#                                                                                       #
# Aufruf: rcstate.pl [IP] [Port] [Name] [Benutzer] [Passwort] [Funktion] {neuer Status}	#
#                                                                                       #
# Hinweis: In diesem Skript wird nicht auf Fehlerhaften Aufruf geprüft, da es           #
#		   nur zum Aufruf aus der Java Serveranwendung geschrieben wurde!               #
#########################################################################################

use strict;
use warnings;
use DBI;

# Übergabeparameter in Variablen schreiben
my $db_host = shift;
my $db_port = shift;
my $db_name = shift;

my $db_user = shift;
my $db_pass = shift;

my $db_switch = shift;
my $sub = shift;

my $status = shift;

# Auswahl der Subroutine anhand der $sub Variable
if($sub == 1){
	&setSwitchState;
}
if($sub == 2){
	print &getSwitchState;
}
if($sub == 3){
	print &getSwitchId;
}

# Ändern des Switch Status, bei dieser Subroutine muss bei Aufruf ein neuer Status mit übergeben werden
sub setSwitchState{
	my $dbh = DBI->connect("DBI:mysql:database=$db_name;host=$db_host;port=$db_port", $db_user, $db_pass);
	my $query = "update funksteckdosen_status set status = $status, schaltzeit = current_timestamp() where idfunksteckdose in ( select idfunksteckdose from funksteckdosen where Einschaltcode = $db_switch)";
	my $query_test = $dbh->prepare($query);
	$query_test->execute() or die;
}

# Den momentanen Status aus der Datenbank abfragen (Rückgabewert 1 oder 0)
sub getSwitchState{

	my $dbh = DBI->connect("DBI:mysql:database=$db_name;host=$db_host;port=$db_port", $db_user, $db_pass);
	my $query = "select status from funksteckdosen_status where idfunksteckdose in ( select idfunksteckdose from funksteckdosen where Einschaltcode = $db_switch)";
	my $query_test = $dbh->prepare($query);
	
	$query_test->execute() or die;

	my $col = $query_test->fetchrow_array();
	
	if (defined $col && ($col eq 0 || $col eq 1)){
		return $col;
	}else{
		return -1;
	}
	
}

# Liefert alle IDs der vorhandenen Funksteckdosen als mit Leerzeichen getrennten String zurück (-1 bei Fehler)
sub getSwitchId{
	my $dbh = DBI->connect("DBI:mysql:database=$db_name;host=$db_host;port=$db_port", $db_user, $db_pass);

	my $query_test = $dbh->prepare("SELECT idfunksteckdose FROM projekt_pi.funksteckdosen");
	$query_test->execute() or die;
	
	my $values = "0";
	while(my ($col) = $query_test->fetchrow_array()){
		if ($values eq "0"){
			$values = $col;
		}else{
			$values = $values." ".$col;
		}
	}
	if (!$values eq "0"){
		return $values;
	}else{
		return -1;
	}
	
}

