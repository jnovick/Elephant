package version3;

import version3a.Elephant;
import version3a.Grid;
import version3a.Location;
import version3a.Water;

public class Simulation {

	public static void main(String[] args) {
		Grid grid=new Grid(100, 100, 0.5, false);
		Water lake=new Water(5);
		lake.putSelfInGrid(new Location(70, 80), grid);
		for(int i=0; i<2; i++){
			for(int j=0; j<3; j++){
			Elephant e=new Elephant();
//			Location loc=grid.getRandomLocationOnGrid();
			Location loc=new Location(1+5*i, 1+5*j);
			e.putSelfInGrid(loc, grid, Math.PI/6);
			e.setVisionDistance(50);
			e.setIdealSeperation(30);
			}
		}        
//		grid.addOccurancesBetween(new Location(5,5), new Location(5,25), 3);
//		grid.addOccurancesBetween(new Location(5,25), new Location(10,45), 3);
//		Elephant e=new Elephant();
//		Location loc2=new Location(8,3);
//		Location loc=new Location(1,1);
//		e.putSelfInGrid(loc, grid, Math.PI/6);
//		e.putSelfInGrid(loc2, grid, Math.PI/6);
//		int i=0;
		while(grid.containsActiveActors()){
			grid.step();
//			System.out.println(i++);
		}
		System.out.println(grid);
		grid.reDraw();
		grid.save();
		grid.displayGraphics();
	}
}