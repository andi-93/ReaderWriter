import java.util.List;
import java.util.ArrayList;

/*
 * für alle gemeinsam benutzten Daten eines kritischen Abschnittes
 * muss eine Variable vom Typ Semaphor deklariert werden.
 */

public class Semaphor {

	List<Process> W = new ArrayList<Process>();
	boolean writing = false;
	int number_reading = 0;
	
}
