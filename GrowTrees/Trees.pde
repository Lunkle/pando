class Tree extends Model{  
  String name;
  int seedDispersal;
  float treeHeight;
  float treeAge;

  Tree(PVector position, float rotation, float scale, PShape model, String name, int seedDispersal, float treeHeight, float treeAge) {
    super(position, rotation, scale, model);
    this.name = name;
    this.seedDispersal = seedDispersal;
    this.treeHeight = treeHeight;
    this.treeAge = treeAge;
  }
  
  void killTree(){}
  
  void plantTree(){}
  
}
