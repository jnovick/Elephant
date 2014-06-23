package version4B;

import java.util.ArrayList;

public class PathThread extends Thread{

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
		double angle = startLoc.getAngleTo(endLoc);
		ArrayList<Location> alreadyCounted=gr.getGridCellLocationsWithinDistance(radius, startLoc);
		double totalDist=startLoc.distanceTo(endLoc);
		double xCenter, yCenter;
		for(double dist=0; dist<totalDist+gr.getCellSize(); dist+=gr.getCellSize()){
			if(totalDist<dist)
				dist=totalDist;
			xCenter=startLoc.x + dist * Math.cos(angle);
			yCenter=startLoc.y + dist * Math.sin(angle);
			for(double ang=angle-Math.PI/2; ang<angle+Math.PI/2; ang+=Math.PI/(radius*5)*gr.getCellSize()){
				double x=xCenter + radius * Math.cos(ang);
				double y=yCenter + radius * Math.sin(ang);
				Location loc=new Location(x,y);
				loc=gr.roundToNearestCell(loc);
				if(gr.isValid(loc) && !alreadyCounted.contains(loc)){
					gr.addOccurance(loc);
					gr.eraseCell(loc);
					alreadyCounted.add(loc);
				}
			}
			
			for(double ang=angle+Math.PI/2; ang<angle+3*Math.PI/2; ang+=Math.PI/(radius*5)*gr.getCellSize()){
				double x=xCenter + radius * Math.cos(ang);
				double y=yCenter + radius * Math.sin(ang);
				Location loc=new Location(x,y);
				if(gr.isValid(loc)){
					gr.eraseCell(loc);
				}
			}
		}
		
	}
}
