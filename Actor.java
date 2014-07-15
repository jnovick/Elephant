package version4B;

import java.awt.Color;
import java.util.ArrayList;

public abstract class Actor {
	private Color color;
	private Location location;
	private Grid grid;
	private double radius;

	public Actor(Color color, double radius){
		this.color=color;
		this.radius=radius;
	}

	public abstract void act();

	public void draw(){
		
		ArrayList<Location> list=grid.getGridCellLocationsWithinDistance(radius, location);
		for(Location loc: list)
			grid.drawCell(loc, color);
			
	}
	
	public void erase(){
		
		ArrayList<Location> list=grid.getGridCellLocationsWithinDistance(radius, location);
		for(Location loc: list)
			grid.eraseCell(loc);
			
	}

	public Grid getGrid(){
		return grid;
	}

	public double getRadius() {
		return radius;
	}

	public void putSelfInGrid(Location loc, Grid grid){
		this.grid=grid;
		Actor other=grid.get(loc);
		if(other!=null)
			other.removeSelfFromGrid();
		location=loc;
		grid.put(loc, this);
	}

	public void removeSelfFromGrid(){
		grid.remove(this);
		grid=null;
		location=null;
	}

	public void moveTo(Location loc){
		if(loc==null || !getGrid().isValid(loc)){
			removeSelfFromGrid();
			return;
		}
		
		if (loc.equals(location))
			return;
		
		else{
			grid.remove(this);
			Actor other = grid.get(loc);
			if (other != null && !(other instanceof Water))
				other.removeSelfFromGrid();
			location = loc;
			grid.put(location, this);
			location=loc;
		}
	}

	public Location getLocation(){
		return location;
	}

	protected void setLocation(Location location) {
		this.location = location;
	}

	public void setColor(Color color){
		this.color=color;
	}

	public Color getColor(){
		return color;
	}

	public boolean isOnLocation(Location loc) {
		if(loc==null || location==null)
			return false;
		return loc.isWithinDistance(location, radius);
	}

	protected abstract boolean isActive();
}