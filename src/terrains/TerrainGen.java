package terrains;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TerrainGen {
	
	private int y;
	private int x;
	private String fileName;
	
	public TerrainGen(int x, int y, String fileName) {
		this.x = x;
		this.y = y;
		this.fileName = fileName;
	}
	
	public void makeDefaultFile() throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter("res/" + fileName + ".txt"));
		writer.write(Integer.toString(x));
		writer.write(Integer.toString(y));
		for (int i = 0; i < y; i++) {
			writer.write('\n');
			for (int n = 0; n < x; n ++) {
				writer.write("0");
			}
		}
		System.out.println("Created " + fileName + " at " + "res/" + fileName + ".txt");
		writer.close();
	}
	
	public String getName() {
		return fileName;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}