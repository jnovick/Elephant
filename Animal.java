/**
 * This model implements the agent based model described by 
 * "Spatial structures in Simulations" of animal grouping by
 * Vincent Mirabet, Pierre Auger, and Christophe Lett
 * 
 * The balance of the weights of alignment, attraction, and
 * repulsion were used according to model D by Warbton (1987)
 * An added resource factor has been added
 * 
 * @author Joshua Novick
 * @author Greg Kiker
 */

package version4B;

import java.awt.Color;
import java.util.ArrayList;

public abstract class Animal extends Actor{
	private double direction;
	private double maxTurningRadius=Math.PI/12;
	
	protected int steps;
	protected double visionDistance; //Must be greater than or equal to than zero
	protected double stepLength;
	protected double idealSeperation=15;
	
	public Animal(Color color, double radius) {
		super(color, radius);
		direction=Math.random()*Math.PI*2;
		steps=0;
		stepLength=0.25;
		visionDistance=-1;
		//Default vision distance is a fifth of the smaller dimension of the first grid it is put on. 

	}

	public Animal(Color color, double radius, int direction, int visionDistance, double stepLength) {
		super(color, radius);
		this.direction = direction;
		steps=0;
		this.visionDistance = visionDistance;
		this.stepLength=stepLength;
		
	}

	public double fixTurnAngle(double angle){
		angle%=Math.PI*2;
		if(angle>Math.PI)
			angle-=Math.PI*2;
		if(angle<-Math.PI)
			angle+=Math.PI*2;
		if(Math.PI==angle || -Math.PI==angle)
			return 0;
		if(angle>getMaxTurningRadius())
			return getMaxTurningRadius();
		if(angle<-getMaxTurningRadius())
			return -getMaxTurningRadius();
		return angle;
		
	}
	
	public void putSelfInGrid(Location startLoc, Grid grid) {
		super.putSelfInGrid(startLoc, grid);
		if(visionDistance<0)
			visionDistance=Math.min(getGrid().getLength(), getGrid().getHeight())/5;
	}
	
	public void putSelfInGrid(Location startLoc, Grid grid, double direction) {
		this.direction=direction;
		putSelfInGrid(startLoc, grid);
	}
	
	public void act(){
		if(canMove()){
			turn();
			move();
			steps++;
		}
	}
	
	public void move(){
		Location loc=getLocation().getRelativeLocation(getDirection(), stepLength);
		getGrid().addOccurancesBetween(getLocation(), loc, getRadius());
		moveTo(loc);
	}

	public void turn(){                          
		double resource   =  getResourceFactor()   ;
		double random     =  getRandomTurnFactor() ;
		double repulsion  =  getRepulsionFactor()  ;
		double attraction =  getAttractionFactor() ;
		double alignment  =  getAlignmentFactor()  ;
		double sum=resource+random+repulsion+attraction+alignment;
		double avg=sum/5;
		double angle=fixTurnAngle(avg);
		setDirection(getDirection()+angle);
	}                                            
	
	private boolean canMove() {
		return getGrid()!=null && getLocation()!=null;
	}

	protected boolean isActive(){
		return true;
	}

	protected double getTurnAngleTo(Location loc){
		double angle=getLocation().getAngleTo(loc);
		angle-=direction;
		if(angle<-Math.PI)
			angle+=2*Math.PI;
		return angle;
	}
	
	protected abstract double getResourceDirection(Resource actor);
	protected abstract double getRandomTurnDirection();
	protected abstract double getRepulsionDirection(Animal actor);
	protected abstract double getAttractionDirection(Animal actor);
	protected abstract double getAlignmentDirection(Animal actor);
	
	protected abstract double getResourceFactorWeight(Resource actor);
	protected abstract double getRandomTurnFactorWeight();
	protected abstract double getRepulsionFactorWeight(Animal actor); 
	protected abstract double getAttractionFactorWeight(Animal actor);
	protected abstract double getAlignmentFactorWeight(Animal actor); 
	
	protected double getResourceFactor(){
		ArrayList<Actor> actList = getGrid().getActorsWithinDistance(visionDistance, getLocation());
		if(actList.size()==0)
			return 0;
		double sum=0;
		for(Actor actor: actList){
			if(!(actor instanceof Resource))
				continue;
			sum+=getResourceDirection((Resource)actor)* getResourceFactorWeight((Resource)actor);
		}
		return sum;
	}
	protected double getRandomTurnFactor(){
		return getRandomTurnDirection() * getRandomTurnFactorWeight();
	}
	protected double getRepulsionFactor(){
		ArrayList<Actor> actList = getGrid().getActorsWithinDistance(visionDistance, getLocation());
		if(actList.size()==0)
			return 0;
		double sum=0;
		int num=0;
		for(Actor actor: actList){
			if(!(actor instanceof Animal))
				continue;
			sum+=getRepulsionDirection((Animal)actor)* getRepulsionFactorWeight((Animal)actor);
			num++;
		}
		if(num==0)
			return 0;
		return sum/num;
	}
	protected double getAttractionFactor(){
		ArrayList<Actor> actList = getGrid().getActorsWithinDistance(visionDistance, getLocation());
		if(actList.size()==0)
			return 0;
		double sum=0;
		int num=0;
		for(Actor actor: actList){
			if(!(actor instanceof Animal))
				continue;
			sum+=getAttractionDirection((Animal)actor) * getAttractionFactorWeight((Animal)actor);
			num++;
		}
		if(num==0)
			return 0;
		return sum/num;
	}
	protected double getAlignmentFactor(){
		ArrayList<Actor> actList = getGrid().getActorsWithinDistance(visionDistance, getLocation());
		if(actList.size()==0)
			return 0;
		double sum=0;
		int num=0;
		for(Actor actor: actList){
			if(!(actor instanceof Animal))
				continue;
			sum+=getAlignmentDirection((Animal)actor) * getAlignmentFactorWeight((Animal)actor);
			num++;
		}
		if(num==0)
			return 0;
		return sum/num;
	}
	
	public double getDirection(){
		return direction;
	}

	public void setDirection(double direction){
		this.direction=direction;
	}

	public double getStepLength() {
		return stepLength;
	}

	public void setStepLength(double stepLength) {
		this.stepLength = stepLength;
	}

	public double getMaxTurningRadius() {
		return maxTurningRadius;
	}

	public void setMaxTurningRadius(double maxTurningRadius) {
		this.maxTurningRadius = maxTurningRadius;
	}
	
	public double getVisionDistance() {
		return visionDistance;
	}

	public void setVisionDistance(double visionDistance) {
		this.visionDistance = visionDistance;
	}

	public double getIdealSeperation() {
		return idealSeperation;
	}

	public void setIdealSeperation(double idealSeperation) {
		this.idealSeperation = idealSeperation;
	}
}
