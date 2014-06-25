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

/*	public void run(){
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
		
	}*/
	
	public void run(){
		double increment=gr.getCellSize();
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
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius))
						continue;
					if(gr.isValid(loc))
						gr.addOccurance(loc);
				}
			}

			for(double y=y2; y<=y2+radius; y+=increment){
				for(double x=xc-radius; x<=xc+radius; x+=increment){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(gr.isValid(loc) && loc.isWithinDistance(gr.roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius))
						gr.addOccurance(loc);
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
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius) || loc.isWithinDistance(gr.roundToNearestCell(endLoc), radius))
						continue;
					if(gr.isValid(loc))
						gr.addOccurance(loc);
				}
			}
			
			for(double xc=x2; xc<=x2+radius; xc+=increment){
				double y=slope*xc+yint;
				for(double x=xc-radius; x<=xc+radius; x+=increment){
					Location loc=gr.roundToNearestCell(new Location(x,y));
					if(gr.isValid(loc) && loc.isWithinDistance(gr.roundToNearestCell(endLoc), radius) && !loc.isWithinDistance(gr.roundToNearestCell(startLoc), radius))
						gr.addOccurance(loc);
				}
			}
		}
	}
}
