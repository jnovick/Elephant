package version3;


import java.awt.Color;
import java.util.ArrayList;

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

	public Grid(){
		this(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_CELL_SIZE);
	}

	public Grid(int height, int length, double cellSize){
		this.cellSize=cellSize;
		numOccurances=new int[(int)(height/cellSize)][(int)(length/cellSize)];
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
		double increment=getCellSize();
		if(startLoc.x==endLoc.x){
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
					Location loc=roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(roundToNearestCell(startLoc), radius))
						continue;
					if(isValid(loc))
						addOccurance(loc);
				}
			}

			for(double y=y2; y<=y2+radius; y+=increment){
				for(double x=xc-radius; x<=xc+radius; x+=increment){
					Location loc=roundToNearestCell(new Location(x,y));
					if(isValid(loc) && loc.isWithinDistance(roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(roundToNearestCell(startLoc), radius))
						addOccurance(loc);
				}
			}
		}
		else{
			double slope=(startLoc.y-endLoc.y)/(startLoc.x-endLoc.x);
			double yint=startLoc.y-(slope*startLoc.x);
			double x1, x2;
			if(startLoc.x<endLoc.x){
				x1=startLoc.x;
				x2=endLoc.x;
			}
			else{
				x2=startLoc.x;
				x1=endLoc.x;
			}
			for(double xc=x1; xc<=x2; xc+=increment){
				double y=slope*xc+yint;
				for(double x=xc-radius; x<=xc+radius; x+=increment){
					Location loc=roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(roundToNearestCell(startLoc), radius))
						continue;
					if(isValid(loc))
						addOccurance(loc);
				}
			}
			
			for(double xc=x2; xc<=x2+radius; xc+=increment){
				double y=slope*xc+yint;
				for(double x=xc-radius; x<=xc+radius; x+=increment){
					Location loc=roundToNearestCell(new Location(x,y));
					if(isValid(loc) && loc.isWithinDistance(roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(roundToNearestCell(startLoc), radius))
						addOccurance(loc);
				}
			}
		}
	}

	private void initializeDrawingCanvas() {
		
		StdDraw.setXscale(0, getLength());
		StdDraw.setYscale(0, getHeight());
		StdDraw.clear(BACKGROUND_COLOR);
		
	}

	public void drawCell(Location loc, Color color){
		
		StdDraw.setPenColor(color);
		StdDraw.filledSquare((int)(loc.x/cellSize)*cellSize, (int)(loc.y/cellSize)*cellSize, cellSize);
		StdDraw.show(delayDisplayTime); 
		
	}

	public void eraseCell(Location loc){
		
		int n=(int)Math.max(0, 255-darkeningFactor*getNumOccurances(loc)-darkeningConstant);
		drawCell(loc, new Color(n, n, n)); 
		
	}

	public void reDraw() {
		int temp=delayDisplayTime;
		delayDisplayTime=0;
		StdDraw.clear(BACKGROUND_COLOR);
		if(getMaxNumberOccurances()!=getMinNumberOccurances())
			darkeningFactor=255/(getMaxNumberOccurances()-getMinNumberOccurances());
		darkeningConstant=getMinNumberOccurances()*darkeningFactor;
		ArrayList<Location> occupied=getOccupiedCellLocations();
		ArrayList<Location> empty=getEmptyCellLocations();
		for(Location loc: occupied){
			get(loc).draw();
		}
	
		for(Location loc: empty){
			eraseCell(loc);
		}
		delayDisplayTime=temp;
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
}
