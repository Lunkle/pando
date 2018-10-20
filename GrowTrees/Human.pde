class Human extends Model{
  float speed;
  int intention; 
  int humanType;
  int destructiveWeapons;
  
  Human(float speed, int intention, int humanType, int destructiveWeapons){
    this.intention = intention;
    this.speed = speed;
    this.humanType = humanType;
  }

}
