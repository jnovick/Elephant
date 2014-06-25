package version3;

import java.awt.Color;
import java.util.ArrayList;

public class Elephant extends Animal{
	private static final double DEFAULT_ELEPHANT_SIZE = 1;
	
	
	private static int totalElephants=0;
	
	private int rank=1; //This will matter later
	private int elephantNumber;
	private boolean isThirsty;
	
	public Elephant() {
		this(DEFAULT_ELEPHANT_SIZE);
	}

	public Elephant(double size){
		super(Color.BLUE, size);
		isThirsty=true;
		elephantNumber=++totalElephants;
	}

	@Override
	public String toString() {
		return "Elephant #"+elephantNumber+". Total steps = "+steps;
	}
	
	@Override
	protected double getRandomTurnDirection() {
		double angle=Math.random()*getMaxTurningRadius()*2-getMaxTurningRadius();
		return angle;
	}
	
	@Override
	protected double getResourceDirection(Resource actor) {
		Location loc=actor.getLocation();
		double angle=getTurnAngleTo(loc);
		angle=fixTurnAngle(angle);
		return angle;
	}

	@Override
	protected double getRepulsionDirection(Animal actor) {
		Location loc=actor.getLocation();
		double angle=getTurnAngleTo(loc);
		/*
		if(angle>Math.PI/2 || angle < -Math.PI/2)
			return 0;
			*/
		angle=fixTurnAngle(angle+Math.PI);
		return angle;
	}

	@Override
	protected double getAttractionDirection(Animal actor) {
		Location loc=actor.getLocation();
		double angle=getTurnAngleTo(loc);
		angle=fixTurnAngle(angle);
		return angle;
	}

	@Override
	protected double getAlignmentDirection(Animal actor) {
		double angle=actor.getDirection()-getDirection();
		angle=fixTurnAngle(angle);
		return angle;
	}

	@Override
	protected double getRandomTurnFactorWeight() {
		return 0.00;
	}
	
	@Override
	protected double getResourceFactorWeight(Resource actor) {
		if(!isThirsty)
			return 0;
		double distance=getLocation().distanceTo(actor.getLocation());
		return 1/Math.sqrt(distance);
	}

	@Override
	protected double getRepulsionFactorWeight(Animal actor) {
		double distance=getLocation().distanceTo(actor.getLocation());
		if(distance>idealSeperation)
			return 0;
		else
			return (-distance/idealSeperation)+1;
	}

	@Override
	protected double getAttractionFactorWeight(Animal actor) {
		double distance=getLocation().distanceTo(actor.getLocation());
		if(distance<idealSeperation)
			return 0;
		else
			return ((distance-idealSeperation)/idealSeperation);
			
	}

	@Override
	protected double getAlignmentFactorWeight(Animal actor) {
		return 1;
//		return 1/3 + 1/rank;
	}
}
