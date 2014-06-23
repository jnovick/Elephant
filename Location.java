package version4B;

public class Location {

	public static final int LEFT=270;
	public static final int RIGHT=90;
	public static final int UP=0;
	public static final int FORWARD=0;
	public static final int DOWN=180;
	public static final int BACKWARD=180;

	public double x;
	public double y;

	public Location(double x, double y){
		this.x=x;
		this.y=y;
	}

	public boolean isWithinDistance(Location loc, double distance){
		return distance >= distanceTo(loc);
	}

	public double distanceTo(Location loc){
		double xDist=(x-loc.x);
		double yDist=(y-loc.y);
		double distance = Math.sqrt(xDist*xDist + yDist*yDist);
		return distance;
	}

	public Location getRelativeLocation(double dir, double distance){
		double x= this.x + distance*Math.cos(dir);
		double y= this.y + distance*Math.sin(dir);
		Location loc=new Location(x,y);
		return loc;
	}

	public static double randomDirection(){
		return Math.random()*360;
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		// object must be Test at this point
		Location location = (Location)obj;
		return x == location.x && y == location.y;
	}

	@Override
	public int hashCode(){
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + "]";
	}

	public double getAngleTo(Location otherLoc) {
		double yDist=otherLoc.y-this.y;
		double xDist=otherLoc.x-this.x;
		double angle;
		if(xDist==0)
			if(yDist>0)
				angle=Math.PI/2;
			else
				angle=-Math.PI/2;
		else
			angle=Math.atan(yDist/xDist);
		
		if(this.x>otherLoc.x)
			if(angle<0)
				angle+=Math.PI;
			else
				angle-=Math.PI;
		return angle;
	}
}
