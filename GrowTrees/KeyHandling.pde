boolean wPressed, aPressed, sPressed, dPressed;
boolean qPressed, ePressed, zPressed;

void keyPressed() {
    switch(key) {
    case 'w':
        wPressed = true;
        break;
    case 'a':
        aPressed = true;
        break;
    case 's':
        sPressed = true;
        break;
    case 'd':
        dPressed = true;
        break;
    case 'q':
        qPressed = true;
        break;
    case 'e':
        ePressed = true;
        break;
    case 'z':
        zPressed = true;
        break;
    }
}

void keyRespond() {
    float xValue = 0;
    float yValue = 0;
    float zValue = 0;
    if (wPressed) {
        xValue += cos(player.camera.yaw);
        yValue -= sin(player.camera.yaw);
    }
    if (aPressed) {
        yValue -= cos(player.camera.yaw);
        xValue -= sin(player.camera.yaw);
    }
    if (sPressed) {
        xValue -= cos(player.camera.yaw);
        yValue += sin(player.camera.yaw);
    }
    if (dPressed) {
        yValue += cos(player.camera.yaw);
        xValue += sin(player.camera.yaw);
    }
    if(qPressed){
        zValue -= 1;
    } 
    if(ePressed){
        zValue += 1;
    }
    player.camera.moveCamera(xValue, yValue, zValue);
}

//When keys are released set status to false.
void keyReleased() {
    switch(key) {
    case 'w':
        wPressed = false;
        break;
    case 'a':
        aPressed = false;
        break;
    case 's':
        sPressed = false;
        break;
    case 'd':
        dPressed = false;
        break;
    case 'q':
        qPressed = false;
        break;
    case 'e':
        ePressed = false;
        break;
    case 'z':
        zPressed = false;
        break;
    }
}
