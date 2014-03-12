
public class Tester {

	int data_size = 4;
	int number_processes = 10;
	int max_t_sleep = 5000;
	int max_t_work = 3000;
	
	Semaphor[] s = new Semaphor[data_size];
	Process[] p = new Process[number_processes];
	
	public static void main(String[] args) {
		Tester t = new Tester();
	}
	
	public Tester() {
		for (int i=0; i<data_size; i++) {
			s[i] = new Semaphor();
		}	
		for (int i=0; i<number_processes; i++) {
			//Abwechselnd Reader/Writer erstellen
			if (i%2 == 0) { //Reader
				p[i] = new Process(this, i, false);
			}
			else { //Writer
				p[i] = new Process(this, i, true);
			}
			p[i].start();
		}
	}
	
}
