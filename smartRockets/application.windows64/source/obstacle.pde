class obstacle {

  int obstacleHt = 0;
  int obstacleWid = 0;
  PVector popsicle;

  //Constructor
  obstacle(int wid, int ht, int x, int y) {
    this.popsicle = new PVector(x,y);
    this.obstacleHt = ht;
    this.obstacleWid = wid;
  }
  
  //Draws the obstacle
  void drawObstacle() {
    pushMatrix();
    fill(255);
    rect(popsicle.x,popsicle.y,obstacleWid,obstacleHt);
    popMatrix();
  }
  
  PVector returnPos() {
    return popsicle;
  }
  
  int ObWid() {
    return obstacleWid;
  }
  
  int ObHt() {
    return obstacleHt;
  }
    
}