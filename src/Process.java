import java.util.Random;


public class Process extends Thread {

	private Tester tester;
	private int PID, data_size, t_work, data_index;
	private boolean writer = false;		//false -> Reader, true -> Writer
	private boolean first_run = true;
	private boolean from_List = false;	//Prozess wurde soeben aus Warteliste genommen
	
	public Process(Tester t, int pid, boolean w) {
		PID = pid;
		data_size = t.data_size;
		tester = t;
		writer = w;
	}
	
	public void run() {
		if (first_run) { // -> Datenzugriff in x ms planen
			Random rd = new Random();
			int t_sleep = rd.nextInt(tester.max_t_sleep)+1;
			t_work = rd.nextInt(tester.max_t_work)+1;
			data_index = rd.nextInt(data_size);
			
			try {
				System.out.println("P"+PID+" geplanter Zugriff an Pos. "+data_index+" in "+t_sleep+" ms.");
				Thread.sleep(t_sleep);
				first_run = false;
				P(tester.s[data_index]);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		else { // first_run == false -> Datenzugriff
			if (!writer) {
				System.out.println("P"+PID+" liest Position "+data_index+" für "+t_work+" ms...");
				tester.s[data_index].number_reading++;
			} else {
				System.out.println("P"+PID+" beschreibt Position "+data_index+" für "+t_work+" ms...");
				tester.s[data_index].writing = true;
			}
			
			try {
				Thread.sleep(t_work);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			if (!writer) {
				System.out.println("P"+PID+" Pos. "+data_index+" lesen fertig.");
				tester.s[data_index].number_reading--;
			} else {
				System.out.println("P"+PID+" Pos. "+data_index+" schreiben fertig.");
				tester.s[data_index].writing = false;
			}
			V(tester.s[data_index]);
		}
	}
	
	public void P(Semaphor S) {
		// momentan beschrieben OR (momentan gelesen AND schreiben wollen) -> blocken
		if (tester.s[data_index].writing || (tester.s[data_index].number_reading > 0 && this.writer)) {
			if (from_List) {		// soeben aus Warteliste geholt
				S.W.add(0, this);	// -> wieder vorne in Liste schreiben (FCFS)
			}
			else {					// neuer Prozess, nicht aus Warteliste
				S.W.add(this);		// -> hinten in Warteliste schreiben
			}
			System.out.println("P"+PID+" Liste W"+data_index+" hinzugefügt und blockiert.");
			//automatisch blockiert da run()-Methode am Ende
		}
		else {
			// nicht blocken -> 2. mal run() -> first_run ist false -> Datenzugriff
			run();
		}
	}
	
	public void V(Semaphor S) {
		if (S.W.size() > 0) {
			Process px = S.W.remove(0);
			System.out.println("P"+px.PID+" aus Liste W"+data_index+" genommen.");
			px.from_List = true;
			px.P(tester.s[data_index]);
		}
	}
}
