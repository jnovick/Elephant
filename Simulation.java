package version4B;

public class Simulation {

	public static void main(String[] args) {
		Grid grid=new Grid(100, 100, 0.5);
//		Water lake=new Water(5);
//		lake.putSelfInGrid(new Location(120, 150), grid);
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
			Elephant e=new Elephant();
//			Location loc=grid.getRandomLocationOnGrid();
			Location loc=new Location(40+5*i, 40+5*j);
			e.putSelfInGrid(loc, grid, Math.PI/4);
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