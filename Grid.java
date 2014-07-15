package version4B;


import java.awt.Color;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Grid {
	private static final int DEFAULT_SIZE=50;
	protected static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final double DEFAULT_CELL_SIZE = 1;
	
	private int delayDisplayTime=0;
	private int darkeningFactor = 10; //arbitrary value that is adjusted when reDraw() is called.
	private int darkeningConstant = 0;

	private ArrayList<Actor> occupants;
	private int[][] numOccurances;
	private double cellSize;

	private boolean displayGraphics;
	private boolean graphicsInitialized=false;
	
	public Grid(){
		this(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_CELL_SIZE, true);
	}

	public Grid(int height, int length, double cellSize, boolean displayGraphics){
		this.displayGraphics=displayGraphics;
		this.cellSize=cellSize;
		numOccurances=new int[(int)(height/cellSize)][(int)(length/cellSize)];
		if(displayGraphics)
			initializeDrawingCanvas();
		occupants=new ArrayList<Actor>();
	}

	public boolean containsActiveActors(){
		for(Actor actor: occupants){
			if(actor.isActive())
				return true;
		}
		return false;
	}
	
	public int getNumOccurances(Location loc) {
		return numOccurances[(int)(loc.y/cellSize)][(int)(loc.x/cellSize)];
	}

	public void addOccurance(Location loc){
		numOccurances[(int)(loc.y/cellSize)][(int)(loc.x/cellSize)]++;
	}

	public double getHeight() {
		return numOccurances.length*cellSize;
	}

	public double getLength() {
		return numOccurances[0].length*cellSize;
	}

	public boolean contains(Actor actor){
		return occupants.contains(actor);
	}

	public boolean contains(Class<?> c, Location loc){
		if(loc == null)
			return false;
		
		for(Actor actor: occupants){
			if(actor.isOnLocation(loc) && actor.getClass().equals(c))
				return true;
		}
		return false;
	}
	
	public Actor get(Location loc){
		if(loc == null)
			return null;
		
		for(Actor actor: occupants){
			if(actor.isOnLocation(loc))
				return actor;
		}
		return null;
	}
	
	public ArrayList<Location> getGridCellLocationsWithinDistance(double distance, Location location){
		ArrayList<Location> list=new ArrayList<Location>();
		for(double i=-distance; i<=distance; i+=cellSize)
			for(double j=-distance; j<=distance; j+=cellSize){
				Location loc=new Location(location.x+i, location.y+j);
				if(isValid(loc) && Math.sqrt(i*i + j*j)<=distance)
					list.add(loc);
			}
		return list;
	}

	public ArrayList<Actor> getActorsWithinDistance(double distance, Location location){
		ArrayList<Actor> actList=new ArrayList<Actor>();
		for(Actor actor: occupants)
			if(location.isWithinDistance(actor.getLocation(), distance) && actor.getLocation()!=location)
				actList.add(actor);
		return actList;
	}
	
	public boolean isValid(Location loc) {
		if(loc==null)
			return false;
		return loc.x < getLength() && loc.x >= 0 && loc.y < getHeight() && loc.y >= 0;
	}

	public ArrayList<Location> getEmptyCellLocations(){
		ArrayList<Location> list = new ArrayList<Location>();
		for (double r = 0; r < getHeight(); r+=cellSize)
			for (double c = 0; c < getLength(); c+=cellSize){
				Location loc = new Location(r, c);
				if (get(loc) == null)
					list.add(loc);
			}
		return list; 
	}

	public ArrayList<Location> getOccupiedCellLocations(){
		ArrayList<Location> list = new ArrayList<Location>();
		for (int r = 0; r < getHeight(); r++)
			for (int c = 0; c < getLength(); c++){
				Location loc = new Location(r, c);
				if (get(loc) != null)
					list.add(loc);
			}
		return list; 
	}

	public Location getRandomLocationOnGrid(){
		double randX=Math.random()*getLength();
		double randY=Math.random()*getHeight();
		return new Location(randX, randY);
	}

	public void put(Location location, Actor actor){
		occupants.add(actor);
		actor.draw();
	}

	public void remove (Actor actor){
		occupants.remove(actor);
		actor.erase();
	}
	
	public double getCellSize() {
		return cellSize;
	}

	public boolean isOccupied(Location loc){
		return get(loc)!=null;
	}

	public void run(){
		while(true)
			step();
	}

	public void step(){
		for(int i=occupants.size()-1; i>=0; i--)
				occupants.get(i).act();
	}

	public Location roundToNearestCell(Location loc){
		return new Location((int)(loc.x/cellSize)*cellSize, (int)(loc.y/cellSize)*cellSize);
	}

	public void addOccurancesBetween(Location startLoc, Location endLoc, double radius) {
		PathThread p=new PathThread(this, startLoc, endLoc, radius);
		p.start();
	}

	private void initializeDrawingCanvas() {
		
		StdDraw.setXscale(0, getLength());
		StdDraw.setYscale(0, getHeight());
		StdDraw.clear(BACKGROUND_COLOR);
		graphicsInitialized=true;
		
	}

	public void drawCell(Location loc, Color color){

		if(displayGraphics){
			StdDraw.setPenColor(color);
			StdDraw.filledSquare((int)(loc.x/cellSize)*cellSize, (int)(loc.y/cellSize)*cellSize, cellSize);
			StdDraw.show(delayDisplayTime); 
		}
		
	}

	public void eraseCell(Location loc){
		
		if(displayGraphics){
			int n=(int)Math.max(0, 255-darkeningFactor*getNumOccurances(loc)-darkeningConstant);
			drawCell(loc, new Color(n, n, n)); 
		}
		
	}

	public void reDraw() {
		if(displayGraphics){
			int temp=delayDisplayTime;
			delayDisplayTime=0;
			StdDraw.clear(BACKGROUND_COLOR);
			int max=getMaxNumberOccurances();
			int min=getMinNumberOccurances();
			double cellSize=getCellSize();
			if(max!=min)
				darkeningFactor=255/(max-min);
			darkeningConstant=min*darkeningFactor;
			for(int i=0; i<numOccurances.length; i++)
				for(int j=0; j<numOccurances[i].length; j++){
					Location loc=new Location(j*cellSize,i*cellSize);
					eraseCell(loc);
				}
			for(Actor actor: occupants)
				actor.draw();
			delayDisplayTime=temp;
		}
	}

	public int getMaxNumberOccurances(){
		int max=numOccurances[0][0];
		for(int[] arr: numOccurances)
			for(int num: arr)
				max=Math.max(max, num);
		return max;
	}

	public int getMinNumberOccurances(){
		int min=numOccurances[0][0];
		for(int[] arr: numOccurances)
			for(int num: arr)
				min=Math.min(min, num);
		return min;
	}
	
	public String toString(){
		String str="";
		for(int[] arr: numOccurances){
			for(int i: arr){
				str+=i+" ";
			}
			str+="\n";
		}
		return str;
	}
	
	public void displayGraphics(){
		if(displayGraphics)
			return;
		displayGraphics=true;
		if(!graphicsInitialized)
			initializeDrawingCanvas();
		reDraw();
	}
	
	public void hideGraphics(){
		displayGraphics=false;
	}
	
	public void save(){
		DateFormat dateFormat = new SimpleDateFormat("HH.mm.ss yyyy.MM.dd");
		Date date = new Date();
		String day=dateFormat.format(date);
		save(day);
	}

	public void save(String saveName) {
		PrintWriter writer=null;
		try {
			writer = new PrintWriter("C:\\Users\\Joshua Account\\SkyDrive\\Research\\Results\\Version 3A saveFiles\\"+saveName+".txt", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(int i=numOccurances.length-1; i>=0; i--){
			for(int j=0; j<numOccurances[i].length; j++){
				writer.print(numOccurances[i][j]+" ");
			}
			writer.println();
		}
		writer.close();
		
	}

	public boolean willDisplayGraphics() {
		return displayGraphics;
	}
}

class PathThread extends Thread{

	private Grid gr;
	private Location startLoc;
	private Location endLoc;
	private double radius;

	public PathThread(Grid gr, Location startLoc, Location endLoc, double radius) {
		super();
		this.gr=gr;
		this.startLoc=startLoc;
		this.endLoc=endLoc;
		this.radius=radius;
	}

	public void run(){	
		if(startLoc.x==endLoc.x){
			double increment=gr.getCellSize();
			double y1, y2;
			if(startLoc.y<endLoc.y){
				y1=startLoc.y;
				y2=endLoc.y;
			}
			else{
				y2=startLoc.y;
				y1=endLoc.y;
			}
			double xc=startLoc.x;
			for(double y=y1; y<y2; y+=increment){
				for(double x=xc-radius; x<=xc+radius; x+=increment){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius))
						continue;
					if(gr.isValid(loc)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}

			for(double y=y2; y<=y2+radius; y+=increment){
				for(double x=xc-radius; x<=xc+radius; x+=increment){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(gr.isValid(loc) && loc.isWithinDistance(gr.roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}
		}
		else if(startLoc.y==endLoc.y){
			double increment=gr.getCellSize();
			double x1, x2;
			if(startLoc.x<endLoc.x){
				x1=startLoc.x;
				x2=endLoc.x;
			}
			else{
				x2=startLoc.x;
				x1=endLoc.x;
			}
			double yc=startLoc.y;
			for(double x=x1; x<x2; x+=increment){
				for(double y=yc-radius; y<=yc+radius; y+=increment){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius))
						continue;
					if(gr.isValid(loc)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}

			for(double x=x2; x<=x2+radius; x+=increment){
				for(double y=yc-radius; y<=yc+radius; y+=increment){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(gr.isValid(loc) && loc.isWithinDistance(gr.roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}
		}
		else if(Math.abs(startLoc.y-endLoc.y)>Math.abs(startLoc.x-endLoc.x)){
			double slope=(startLoc.x-endLoc.x)/(startLoc.y-endLoc.y);
			double xint=startLoc.x-(slope*(startLoc.y));
			double y1, y2;
			if(startLoc.y<endLoc.y){
				y1=startLoc.y;
				y2=endLoc.y;
			}
			else{
				y2=startLoc.y;
				y1=endLoc.y;
			}
			double increment=gr.getCellSize();
			for(double y=y1; y<y2; y+=increment){
				double xc=slope*y+xint;
				for(double x=xc-radius; x<=xc+radius; x+=gr.getCellSize()){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius))
						continue;
					if(gr.isValid(loc)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}

			for(double y=y2; y<=y2+radius; y+=increment){
				double xc=slope*y+xint;
				for(double x=xc-radius; x<=xc+radius; x+=gr.getCellSize()){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(gr.isValid(loc) && loc.isWithinDistance(gr.roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}
		}

		else{
			double slope=(startLoc.y-endLoc.y)/(startLoc.x-endLoc.x);
			double yint=startLoc.y-(slope*(startLoc.x));
			double x1, x2;
			if(startLoc.x<endLoc.x){
				x1=startLoc.x;
				x2=endLoc.x;
			}
			else{
				x2=startLoc.x;
				x1=endLoc.x;
			}
			double increment=gr.getCellSize();
			for(double x=x1; x<x2; x+=increment){
				double yc=slope*x+yint;
				for(double y=yc-radius; y<=yc+radius; y+=gr.getCellSize()){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius))
						continue;
					if(gr.isValid(loc)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}

			for(double x=x2; x<=x2+radius; x+=increment){
				double yc=slope*x+yint;
				for(double y=yc-radius; y<=yc+radius; y+=gr.getCellSize()){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(gr.isValid(loc) && loc.isWithinDistance(gr.roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius)){
						gr.addOccurance(loc);
						if(gr.willDisplayGraphics())
							gr.eraseCell(loc);
					}
				}
			}
		}


	}
}
