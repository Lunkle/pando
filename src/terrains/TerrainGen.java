package terrains;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TerrainGen {
	
	// X and Y dimensions
	private int y;
	private int x;

	// File data
	private String fileName;
	private String location;
	
	public TerrainGen(int x, int y, String fileName, String location) {
		this.x = x;
		this.y = y;
		this.fileName = fileName;
		this.location = location;
	}
	
	public void makeDefaultFile() throws IOException{
		// Write to a new file named fileName in location
		BufferedWriter writer = new BufferedWriter(new FileWriter(location + "/" + fileName + ".txt"));
		
		// File metadata, might not be neccecary
		writer.write(Integer.toString(x));
		writer.write(" ");
		writer.write(Integer.toString(y));
		
		// Write y lines of x zeros
		for (int i = 0; i < y; i++) {
			writer.write('\n');
			for (int n = 0; n < x; n ++) {
				writer.write("0");
			}
		}
		
		// To confirm that the file was created
//		System.out.println("Created " + fileName + " at " + "res/" + fileName + ".txt");
		
		writer.close();
	}
	
	// Get functions, not sure if these are neccecary, but oh well
	public String getName() {
		return location + '/' + fileName + ".txt";
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}