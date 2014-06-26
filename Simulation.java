package version3;

public class Simulation {

	public static void main(String[] args) {
		Grid grid=new Grid(20, 20, 0.5);
//		Water lake=new Water(5);
//		lake.putSelfInGrid(new Location(20, 40), grid);
		for(int i=0; i<1; i++){
			for(int j=0; j<1; j++){
			Elephant e=new Elephant();
//			Location loc=grid.getRandomLocationOnGrid();
			Location loc=new Location(5+5*i, 5+5*j);
			e.putSelfInGrid(loc, grid, Math.PI/2);
			e.setVisionDistance(50);
			e.setIdealSeperation(30);
			}
		}
		int i=0;
		while(grid.containsActiveActors()){
			grid.step();
//			System.out.println(i++);
		}
		grid.reDraw();
		System.out.println(grid);
	}
}