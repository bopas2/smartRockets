class target {
  int targetWidth = 0;
  PVector target;
  
  //constructor
  target(int wid, int x, int y) {
    this.targetWidth = wid;
    this.target = new PVector(x,y);
  }
  
  PVector targPos() {
    return this.target;
  }
  
  int targWid() {
    return targetWidth;
  }
  
  //draws the target
  void drawTarget() {
    background(0);
    pushMatrix();
    fill(255);
    ellipse(target.x,target.y,targetWidth,targetWidth);
    popMatrix();
  }

  
}