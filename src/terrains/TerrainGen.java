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

	public void makeDefaultFile() {
		System.out.println("Writing to a map file");
		BufferedWriter writer = null;
		try {
			// Write to a new file named fileName in location
			writer = new BufferedWriter(new FileWriter(location + "/" + fileName + ".txt"));

			// File metadata, might not be neccecary
			writer.write(Integer.toString(x));
			writer.write(" ");
			writer.write(Integer.toString(y));

			// Write y lines of x zeros
			for (int i = 0; i < y; i++) {
				writer.write('\n');
				writer.write("0");
				for (int n = 0; n < x - 1; n++) {
					writer.write(", 0");
				}
			}
			writer.flush();
			closeWriter(writer);
		} catch (Exception e) {
			System.out.println("Error in writing to a map file");
		} finally {
			if (writer != null) {
				closeWriter(writer);
			}
		}
	}

	private void closeWriter(BufferedWriter writer) {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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