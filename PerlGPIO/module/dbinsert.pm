package dbinsert;

#############################################################################################
# Name: dbinsert.pm                                                                         #
# Funktion: Dieses Objektorientierte Modul stellt Methoden für den Datenbankinsert in die   #
#           Datenbank des Projektes Raspberry Pi bereit.                                    #
#                                                                                           #
# Hinweis: Von diesem Modul muss ein Objekt mit den unten stehenden Eigenschaften erzeugt   #
#          werden.                                                                          #
#                                                                                           #
# Author: Projektgruppe Pi 2015                                                             #
#############################################################################################

use DBI;
use Moose;
use autodie;

# Objekteigenschaften definieren
has "db_name" => (is => 'ro', isa => 'Str');
has "db_host" => (is => 'ro', isa => 'Str');
has "db_port" => (is => 'ro', isa => 'Str');
has "db_user" => (is => 'ro', isa => 'Str');
has "db_pass" => (is => 'ro', isa => 'Str');

# Methode für Datenbankverbindung
sub db_connect{
	my $self = shift;
	my $db_name = $self->db_name;
	my $db_host = $self->db_host;
	my $db_port = $self->db_port;
	my $dbh;

	# Connection-String definieren
	my $dsn = "DBI:mysql:database=$db_name;host=$db_host;port=$db_port";

	# Verbimdung aufbauen
	return $dbh = DBI->connect( $dsn, $self->db_user, $self->db_pass );
}

# Subroutine mit der Temperatur und Luftfeuchtigkeit in die Datenbank geschrieben werden kann
sub db_insert_temp{
	my $self = shift;
	my $dbh = shift;
	my $hum = shift;
	my $temp = shift;
	my $room = shift;
	
	# $day auf aktuellen Tag setzen
	my $day = `date +%d`;
	chomp($day);
	
	my $db_insert = "insert into thermosensor (tag, Temperatur, Luftfeuchtigkeit, RaumNr, zeitpunkt) values ($day, $temp, $hum, $room, current_time())";
	
	my $query = $dbh->prepare($db_insert);
	$query->execute();
}

# Subroutine um den Namen eines neuen Bildes in die Datenbank zu schreiben
sub db_insert_movement{
	my $self = shift;
	my $dbh = shift;
	my $image_path = shift;
	my $room = shift;

	my $db_insert = "insert into bilder (Bilder, Zeitpunkt, RaumNr) values ('$image_path', now(), $room)";
	
	my $query = $dbh->prepare($db_insert);
	$query->execute();
}

# Subroutine mit der Ein- und Ausschaltzeitpunkt der Funksteckdosen in die Datenbank geschrieben wird
sub db_insert_zeitschaltplan{
	my $self = shift;
	my $dbh = shift;
	
	my $rcswitch = shift;
	my $room = shift;
	my $on_time = shift;
	my $off_time = shift;
	
	my $db_select = "SELECT idfunksteckdose FROM projekt_pi.funksteckdosen where Einschaltcode = $rcswitch";
	my $query_select = $dbh->prepare($db_select);
	$query_select->execute();
	my $rc_id = $query_select->fetchrow_array();
	
	my $db_insert = "insert into zeitschaltplan (idfunksteckdose, RaumNr, Einschaltzeitpunkt, Ausschaltzeitpunkt)
							SELECT $rc_id, $room, Schaltzeit, current_timestamp()
							FROM   funksteckdosen_status
							WHERE  idfunksteckdose = $rc_id";
	my $query = $dbh->prepare($db_insert);
	$query->execute();
}

# Diese Subroutine ruft das Stored Procedure in der Datenbank auf mit der die Anzahl der Temperatureinträge begrenzt wird
sub db_delete_temp{
	my $self = shift;
	my $dbh = shift;
	
	my $db_delete_rows = "call projekt_pi.delete_rows()";
	
	my $query = $dbh->prepare($db_delete_rows);
	$query->execute();
}

# Diese Subroutine ruft das Stored Procedure in der Datenbank auf mit der die Anzahl der Bilder begrenzt wird
sub db_delete_pictures{
	my $self = shift;
	my $dbh = shift;
	
	my $db_delete_rows = "call projekt_pi.delete_pictures()";
	
	my $query = $dbh->prepare($db_delete_rows);
	$query->execute();
}

# Datenbankverbindung trennen
sub db_disconnect{
	
	my $dbh = $_[1];
	
	$dbh->disconnect();
	
	return 1;
}

1;