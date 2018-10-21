Float prevX;

void mouseMoved(){
    if(prevX == null){
        prevX = (float)mouseX;
    } else{
        player.camera.rotateCamera();
    }
}

void mousePressed(){
    player.camera.findLookedAt();
}
