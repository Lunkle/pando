class Camera {
    static final float CAMERA_SPEED = 5; 
    float xPos, yPos, zPos;
    float yaw; //When yaw is 0, we look down x-axis and increases counterclockwise
    float pitch;

    Camera(float xPos, float yPos, float zPos, float xAngle, float yAngle) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.yaw = xAngle;
        this.pitch = yAngle;
    }
    
    PVector getLookVector(){
        return new PVector(cos(pitch)*cos(yaw), -cos(pitch)*sin(yaw), sin(pitch));
    }

    void applyCamera() {
        PVector lookVector = getLookVector();
        camera(xPos, yPos, zPos, xPos + lookVector.x, yPos + lookVector.y, zPos + lookVector.z, 0,0,-1);
    }

    void rotateCamera() {
        this.yaw = map(mouseX, width/2, -width/2, -QUARTER_PI, TWO_PI - QUARTER_PI);
        this.pitch = map(mouseY, 0, height, HALF_PI, -HALF_PI);
    }

    void moveCamera(float dx, float dy, float dz) {
        xPos += dx * CAMERA_SPEED;
        yPos += dy * CAMERA_SPEED;
        zPos += dz * CAMERA_SPEED;
    }

    void findLookedAt() {
        Block b;
        if (pitch > 0 && zPos > testMap.mapSizeZ) return;
        PVector lookVector = getLookVector();
        drawVector(0, 0, 0, lookVector, 100);
        float relZ = 0;
        for (float i = 0; relZ < zPos; i++) {
            relZ = cos(pitch) * i;
            float Vector2DLength = tan(pitch) * i;
            float x = -Vector2DLength * cos(yaw);
            float y = Vector2DLength * sin(yaw);
            try {
                b = testMap.mapData[int(zPos - relZ) / 30][int(yPos - y) / 30][int(xPos - x) / 30];
                println("Gere");
                if (!(b instanceof Air)) {
                    println(b instanceof Air);
                    testMap.mapData[int(zPos - relZ) / 30][int(yPos - y) / 30][int(xPos - x) / 30] = new Air(b.gridX, b.gridY, b.gridZ);

                    println(b.gridX, b.gridY, b.gridZ);
                    return;
                }
            }
            catch(Exception e) {
            }
        }
    }
}

void drawAxis() {
    strokeWeight(20);
    stroke(255, 0, 0);
    line(0, 0, 0, 500, 0, 0);
    stroke(0, 255, 0);
    line(0, 0, 0, 0, 500, 0);
    stroke(0, 0, 255);
    line(0, 0, 0, 0, 0, 500);
    strokeWeight(1);
}

void drawVector(float x, float y, float z, PVector v, float scale) {
    strokeWeight(20);
    PVector temp = new PVector(v.x, v.y, v.z).mult(scale);
    line(x, y, z, temp.x, temp.y, temp.z);
    strokeWeight(1);
}
