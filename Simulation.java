package version4B;

public class Simulation {

	public static void main(String[] args) {
		Grid grid=new Grid(50, 50, 1);
//		Water lake=new Water(5);
//		lake.putSelfInGrid(new Location(120, 150), grid);
		for(int i=0; i<1; i++){
			for(int j=0; j<1; j++){
			Elephant e=new Elephant();
//			Location loc=grid.getRandomLocationOnGrid();
			Location loc=new Location(30+5*i, 30+5*j);
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


