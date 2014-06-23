package version4B;

import java.awt.Color;

public abstract class Resource extends Actor{

	public Resource(Color color, double radius) {
		super(color, radius);
	}
	
	protected boolean isActive(){
		return false;
	}
}
