package main.java.jdk.designPatterns.responsibility;

public class Boy {
	private boolean isHasHouse;
	private boolean isHasCar;
	private boolean isHasResponsibility;
	
	Boy(boolean isHasHouse, boolean isHasCar, boolean isHasResponsibility) {
		this.isHasHouse = isHasHouse;
		this.isHasCar = isHasCar;
		this.isHasResponsibility = isHasResponsibility;
	}

	public boolean isHasHouse() {
		return isHasHouse;
	}

	public void setHasHouse(boolean isHasHouse) {
		this.isHasHouse = isHasHouse;
	}

	public boolean isHasCar() {
		return isHasCar;
	}

	public void setHasCar(boolean isHasCar) {
		this.isHasCar = isHasCar;
	}

	public boolean isHasResponsibility() {
		return isHasResponsibility;
	}

	public void setHasResponsibility(boolean isHasResponsibility) {
		this.isHasResponsibility = isHasResponsibility;
	}
}
