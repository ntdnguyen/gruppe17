import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Gamepanel extends JPanel implements Runnable, KeyListener,  ActionListener{      // Runnable ist die Spielschleife zum st�ndigem erneuern

	private static final long serialVersionUID = 1L;
	JFrame frame;
	
	long delta = 0; //zum errechnen der Zeit, die f�r den letzten Durchlauf ben�tigt wurde
	long last = 0; // Speicherung der letzten Systemzeit
	long fps = 0; // f�r die Berechnung der Bildrate (Frames per Second (FPS))
	
	Bomber Bomberman;
	Vector<Sprite> actor;
	Vector<Sprite> zeichner;	
	
	// variablen f�r die Bewegungssteuerung und Geschwindigkeit
	boolean hoch;
	boolean runter;
	boolean rechts;
	boolean links;
	boolean  start;
	//boolean bomb;
	
	int speed = 50;
	
	//Timer timer;
	//BufferedImage[] Bombe;
			
	public static void main(String[] args){
		new Gamepanel (600,600);   // Feldgr��e beliebig ver�nderbar
	}
	
	public Gamepanel (int a, int b) {              // hier �bergeben wir die gew�nschte Gr��e an unser GamePanel. Anschlie�end wird ein Fester erzeugt und das Gamepanel eingebunden.
		
		this.setPreferredSize(new Dimension(a,b));
		this.setBackground(Color.WHITE);
		frame = new JFrame("Bomberman SpielDemo");
		frame.setLocation(100,100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.addKeyListener(this);
		

		Thread th = new Thread (this);
		th.start();
	}
	
	private void Initialisieren(){
		
		last = System.nanoTime();  // wird zur Berechnung der Schleifendurchl�ufe ben�tigt

		BufferedImage[] Bomber = ladeBilder ("Bomber.gif", 4);
		//Bombe = ladeBilder("Bombe.gif", 4);
		
		actor = new Vector<Sprite>();  // Instanziieren des Vektors actor
		zeichner = new Vector<Sprite>();
		Bomberman = new Bomber (Bomber, 300, 300, 100, this);  // Instanziieren des Sprites an der Stelle 400/300 mit einer Bildwechselrate von 100ms
		actor.add(Bomberman);  // Sprite in den Vektor packen, der all unsere Objekte beinhalten soll
		
		start = true;
	}
	
	public void run(){       // Auspr�gung der Methode run hier werden die notwendigen Methoden periodisch aufgerufen damit unser Spiel funktioniert und einwandfrei l�uft
		
		while(frame.isVisible()){
						
			computeDelta();  // hier werden wir die Zeit f�r den jeweils vorhergehenden Schleifendurchlauf errechnen
			
			if(isStart()){
			tastencheck(); // Wird zur Abfrage der Tastatureingabe ben�tigt
			dologik(); // Wird zur Ausf�hrung von Logik-Operationen herangezogen
			bewegeObjekte(); // Verwenden wir zum Bewegen unserer Objekte
			Vektorclone();	
			}
			
			repaint(); // geerbte Methode die das Neuzeichnen anst��t
			
			
			try {				
				Thread.sleep(10);    // auf 10 Millisekungen eingestellt, damit 
			} catch (InterruptedException e) {}
			
			
		}
		
	}
	
	private void Vektorclone(){  // H�tten wir auch anders programmieren k�nnen, fand es aber sauberer nur klassen in unserer Initialisieren Methode zu benutzen
		
	zeichner = (Vector<Sprite>) actor.clone();	
	
	}
	
	private void tastencheck(){
		if(hoch){
			Bomberman.svertikalspeed(-speed);
		}
		if(runter){
			Bomberman.svertikalspeed(speed);
		}
		if(rechts){
			Bomberman.shorizontalspeed(speed);
		}
		if(links){
			Bomberman.shorizontalspeed(-speed);
		}
		
		if(!hoch&&!runter){  // Hier ist nur der Vergleich ob beides false ist, damit unsere Figur stehen bleibt (bei hoch/runter)
			Bomberman.svertikalspeed(0);
		}
		if(!rechts&&!links){ // selbe wie oben, nur geltend f�r rechts/links
			Bomberman.shorizontalspeed(0);
		}
	}
	
	private void dologik(){
		for (ListIterator<Sprite> it = actor.listIterator(); it.hasNext();) {
			Sprite r = it.next();
			r.dologik(delta);			
		}
		
	}
	
	private void bewegeObjekte(){
		for(ListIterator<Sprite> it = actor.listIterator(); it.hasNext();){
			Sprite r = it.next();
			r.bewegen(delta);
			
		}
		
	}
	private void computeDelta() {

		delta = System.nanoTime() - last;  // errechnen der Zeit f�r den Schleifendurchlauf in Nanosekunden
		last = System.nanoTime(); // Speicherung der aktuellen Systemzeit
		fps = ((long) 1e9)/delta; // Errechnung der Framrate
		
	}
	
	public void paintComponent(Graphics g) {  // Anzeige der fps im Spiel [es mag f�rs Spiel unwichtig sein, ist aber zur Kontrolle in der Spieleentwicklung gedacht (�berpr�fen obs fl�ssig l�uft)]
		
		super.paintComponent(g);
		
		g.setColor(Color.black);
		g.drawString("FPS: " + Long.toString(fps), 20, 10);  // Der Ort ist beliebig w�hlbar, ich habe die FPS erstmal oben links gesetzt
		
		if(!start){
			return;
		}
		
		for(ListIterator<Sprite> it = zeichner.listIterator();it.hasNext();){ // Hier wird unser Bomberman eingeladen und verarbeitet. Ich denke allerdings das diese L�sung noch �berarbeitet werden muss, je nachdem wie wir unser Programm ausbauen
				Sprite r = it.next();
				r.zeichneObjekte(g);
		}
		/*if(bomb == true){
			
		}*/
	}
	
	private BufferedImage[] ladeBilder(String path, int pics){ // Die Methode bekommt Speicherort und Anzahl der Einzelbilder �bergeben
		
		BufferedImage[] ani = new BufferedImage[pics];  // Wir erzeugen ein BufferdImage-Array in der Gr��e der Einzelbilder
		BufferedImage source = null; // Ein BufferedImage zum Laden des ganzen Bildes, welches dann sp�ter aufgeteilt wird
		
		java.net.URL pic_url = getClass().getClassLoader().getResource(path);  // Ermitteln der URL des Speicherortes, den wir als Pfadangabe �bergeben
		
		try {
			source = ImageIO.read(pic_url);  // Laden des Quellbildes �ber ImageIO
		} catch (IOException e) {}
		
		for(int x=0;x<pics;x++){
			ani[x] = source.getSubimage(x*source.getWidth()/pics, 0, source.getWidth()/pics, source.getHeight()); // Dank der Methode get.Subimage, wird das Quellbild in die angegebene Anzahl Einzelbilder geteilt 
		}
		
		return ani;
		
	}

	public void keyPressed(KeyEvent k) {   // Hier geben wir die einzelnen Tastenbelegungen ein
		if(k.getKeyCode() == KeyEvent.VK_UP){	
			hoch = true;
		}
		if(k.getKeyCode() == KeyEvent.VK_DOWN){
			runter = true;
		}
		if(k.getKeyCode() == KeyEvent.VK_RIGHT){
			rechts = true;
		}
		if(k.getKeyCode() == KeyEvent.VK_LEFT){
			links = true;
		}
		/*if(k.getKeyCode() == KeyEvent.VK_S){
			bomb = true;
		}*/
	}

	public void keyReleased(KeyEvent k) {  // Diese Methode stoppt den Vorgang, wenn man aufh�rt die Tasten zu dr�cken
		
		if(k.getKeyCode() == KeyEvent.VK_UP){	
			hoch = false;
		}
		
		if(k.getKeyCode() == KeyEvent.VK_DOWN){
			runter = false;
		}
		
		if(k.getKeyCode() == KeyEvent.VK_RIGHT){
			rechts = false;
		}
		
		if(k.getKeyCode() == KeyEvent.VK_LEFT){
			links = false;
		}
		/*if(k.getKeyCode() == KeyEvent.VK_S){
			bomb = false;
		}*/
		
		if(k.getKeyCode() == KeyEvent.VK_ENTER){
			if(!isStart()){
				Initialisieren();
				setStart(true);
			}
		}
		
		if(k.getKeyCode() == KeyEvent.VK_ESCAPE){
			if(isStart()){
				setStart(false);
			} else{
				frame.dispose();
			}
		}
		
	}
	

	public void keyTyped(KeyEvent k) {

	}
	
	public boolean isStart() {
		return start;
	}
	
	public void setStart (boolean start){
		this.start  = start;
	}

	public void actionPerformed(ActionEvent a) {
		
	}
	
}
