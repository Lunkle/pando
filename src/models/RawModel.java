package models;

public class RawModel {

	private int vaoID;
	private int indexCount;

	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.indexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getIndexCount() {
		return indexCount;
	}

}
