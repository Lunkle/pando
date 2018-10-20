class Human extends Model{
  float speed;
  int intentionHostility; 
  int humanType;
  int destructiveWeapons;
  
  Human(float speed, int intention, int humanType, int destructiveWeapons){
    super();
    this.intentionHostility = intention;
    this.speed = speed;
    this.humanType = humanType;
    this.destructiveWeapons = destructiveWeapons;
  }

}
