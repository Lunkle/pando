class Player { //do stuff to control where trees appear
    float camX = 0, camY = 0, camZ = 0;
<<<<<<< HEAD
    float camRotX = 0, camRotY = 1, camRotZ = 0;
=======
    float camRotX = 0, camRotY = 0, camRotZ = 0;
>>>>>>> parent of c79a19d... Movement and Camera Orientation
    int camMoveAmount = 100;

    Player() {
    }

    void moveCam() {
        if (key == CODED) {
            if (keyCode == SHIFT) camY+=10;
            else if (keyCode == CONTROL) camY -= 10;
        } else {
            switch(key) {
            case 'd':
                camX += camMoveAmount;
                println('d');
                break;
            case 'a':
                camX -= camMoveAmount;
                println('a');
                break;
            case 'w':
                camZ -= camMoveAmount;
                println('w');
                break;
            case 's':
                camZ += camMoveAmount;
                println('s');
                break;
            }
        }
    }
}
