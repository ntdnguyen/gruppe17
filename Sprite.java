import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Sprite extends Rectangle2D.Double implements Bewegungen, Zeichnungen {

	private static final long serialVersionUID = 1L;
	long delay; // Insanzvariable um das Umschalten zwischen den Bildern unseres Arrays zu steuern
	long animation = 0; // Vorerst nur die Variable hinterlegt, welche wir sp�ter f�r die Animationen ben�tigen
	Gamepanel parent; // Referenz auf unser Gamepanel
	BufferedImage [] pics; // zum Speichern der Animationen in Einzelbilder
	int currentpic = 0; // Z�hler f�r das aktuell anzuzeigende Bild
	
	protected double dx;    // Dies wird unsere Bewegungsvariable (dx = horizontal bewegen, dy = vertikal bewegen)
	protected double dy;
	
	
	
	public Sprite (BufferedImage[] i, double x, double y, long delay, Gamepanel p) {
		
		pics = i;
		this.x = x;
		this.y = y;
		this.delay = delay;
		this.width = pics[0].getWidth();
		this.height = pics[0].getHeight();
		parent = p;
		// Dies ist der Konstruktor mit der �bergabe des Image-Arrays f�r die Animation, Positionswerten, Verz�gerung der Animation und der Referenz zum Gamepanel.
		// Zus�tzlich erhalten wir aus dem ersten Bild die H�he und Breite, unter der Annahme, das alle folgenden die gleichen Werte besitzen
	}
	
	
	public void zeichneObjekte(Graphics g) {
		g.drawImage(pics[currentpic], (int) x, (int) y, null);   // Beim x und y Parametern beachten, dass die Zahlen auf Ganze Zahlen herunter gebrochen werden!! Das ist notwendig, weil Graphics Ganze Zahlen verlangt
		
	}                                                            

	public void dologik(long delta) {
		animation += (delta/1000000);  // delta/1000000, ich benutze hier Millisekunden, da wir die Animationsgeschwindigkeit so besser einstellen k�nnen
		
		if (animation > delay){
			animation = 0;
			computeAnimation();
		}
		
	}	
	private void computeAnimation(){
		currentpic++;        // Hier lassen wir unsere Bilder durchlaufen. Momentan nur von 0 bis n, kann bzw sollte aber sp�ter noch etwas ausgebaut werden
		
		if (currentpic >= pics.length){    
			currentpic = 0;
		}
		
	}

	public void bewegen(long delta) {  
		// Wenn unser Delta nicht null ist, ver�ndern wir so die Position unseres Objektes in die entsprechende Richtung
		// Durch die Ber�cksichtigung der Durchlaufzeit, m�sste somit eine gleichf�rmige Bewegung gew�hrleistet sein
		// >>>> Hoffentlich auch dann, wenn umfangreichere Berechnungen anfallen sollten <<<<
		if (dx != 0){
			x += dx * (delta/1e9);
		}
		
		if (dy != 0){
			
			y += dy * (delta/1e9);
		}
	}

	public double ghorizontalspeed() {
		return dx;
	}
	
	public void shorizontalspeed(double dx) {
		this.dx = dx;
	}
	
	public double gvertikalspeed () {
		return dy;		
	}
	
	public void svertikalspeed(double dy) {
		this.dy = dy;
	}
	
}
