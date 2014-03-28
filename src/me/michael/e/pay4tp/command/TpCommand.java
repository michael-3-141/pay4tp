package me.michael.e.pay4tp.command;

public class TpCommand {

	private String name;
	private int x;
	private int y;
	private int z;
	private int paymentStartBlocks;
	private int[] pointsPerBlocks;
	


	public TpCommand(String name, int x, int y, int z, int paymentStartBlocks,
			int[] pointsPerBlocks) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.paymentStartBlocks = paymentStartBlocks;
		this.pointsPerBlocks = pointsPerBlocks;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public int getZ() {
		return z;
	}


	public void setZ(int z) {
		this.z = z;
	}


	public int getPaymentStartBlocks() {
		return paymentStartBlocks;
	}


	public void setPaymentStartBlocks(int paymentStartBlocks) {
		this.paymentStartBlocks = paymentStartBlocks;
	}


	public int[] getPointsPerBlocks() {
		return pointsPerBlocks;
	}


	public void setPointsPerBlocks(int[] pointsPerBlocks) {
		this.pointsPerBlocks = pointsPerBlocks;
	}

}
