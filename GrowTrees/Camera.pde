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

    void applyCamera() {
        camera(xPos, yPos, zPos, xPos + cos(yaw), yPos - sin(yaw), zPos + sin(pitch), 0, 0, -1);
    }

    void moveCamera(float dx, float dy, float dz) {
        xPos += dx * CAMERA_SPEED;
        yPos += dy * CAMERA_SPEED;
        zPos += dz * CAMERA_SPEED;
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
