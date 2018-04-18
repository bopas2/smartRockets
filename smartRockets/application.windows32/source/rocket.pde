class rocket {
  //Rocket movement variables
  PVector pos = new PVector(width/1.2, height - 50);
  PVector vel = new PVector();
  PVector acel = new PVector();
  //How good our rocket is
  float fitness = 0;
  //Determines if rocket is in good positioning or not
  boolean stuck = false;
  boolean atTarget = false; 
  //Colors of the rocket
  int red = 0;
  int blue = 0;
  int green = 0;
  
  dna Dna; 
  
  //Rocket constructor
  rocket(dna x) {
    //Make new DNA if neccessary, or not
    if(x == null) {
      this.Dna = new dna(null);
    }
    else {
      this.Dna = x;
    }
    //Give each rocket a random color, because colors are pretty
    red = int(random(255));
    blue = int(random(255));
    green = int(random(255));
  }
  
  //Returns the force that is currently acting on the rocket
  PVector getForce(int time) {
    return Dna.getForce(time);
  }
  
  //check for a collision between a rocket and an obstacle
  void checkCollision(obstacle obby) {
    int x = floor(obby.returnPos().x);
    int y = floor(obby.returnPos().y);
    int wid = obby.ObWid();
    int ht = obby.ObHt();
    
    if(this.pos.x < x + wid/2 && this.pos.x > x - wid/2 && this.pos.y > y - ht/2 && this.pos.y < y + ht/2) 
      this.stuck = true;
  }
  
  //Updates position, velocity, acceleration of the particle, and checks that it has not collided with obstacles, or with the screen edges.
  void update(ArrayList<obstacle> obs, target targo) {
    int x = floor(targo.targPos().x);
    int y = floor(targo.targPos().y);
    int tWid = targo.targWid();
    
    if(this.pos.x < 0 || this.pos.x > width || this.pos.y > height || this.pos.y < 0)
      this.stuck = true;

    if(this.pos.x < x + tWid/2 && this.pos.x > x - tWid/2 && this.pos.y < y + tWid/2 && this.pos.y > y - tWid/2) {
      this.pos.x = x;
      this.pos.y = y;
      this.atTarget = true;
    }

    for(obstacle a : obs) 
      checkCollision(a);

    if(!stuck && !atTarget) {
      this.vel.add(this.acel);
      this.pos.add(this.vel);
      this.acel.mult(0);
    }
  }
  
  //Returns a boolean stating if the rocket has reached its target
  boolean atTarget() {
    return this.atTarget;
  }
  
  //Displays the rocket
  void show() {
      pushMatrix(); 
      fill(this.red,this.blue,this.green);
      translate(this.pos.x,this.pos.y);
      rotate(this.vel.heading()); //Points towards it's current velocity
      rectMode(CENTER);
      rect(0,0,40,10);
      popMatrix();
  }
  
  //Applies vector forces
  void applyForce(PVector force) {
    this.acel.add(force);
  }
  
  //Calculates fitness of the rocket
  float calculateFitness(PVector targ, int targWid) {
    
    float baseScore = pow(1/this.pos.dist(targ),5); //Closer to the target is good, far away is bad
    
    if(atTarget) 
      this.fitness = pow(1/targWid,5)*1000; 
      //use the target width, because we move the particles to the center of the target once they reach the target, 
      //effectively making their dist to the target 0, 1/0 is infinity, shit breaks.
      
    if(baseScore > this.fitness && !atTarget) { //Update max fitness score, or don't depending on current fitness state
      this.fitness = baseScore;
    }
    
    return this.fitness;
  }
  
  //Combines two rocket's dna
  dna crossBreed(rocket parent2) {
    
    //Combines two rockets where each 'gene' is randomly taken from rocket 1 or rocket 2
    
    PVector[] newGenes = new PVector[lifespan];
    for(int i = 0; i < lifespan; i++) {
      int ra = int(random(100));
      if(ra >= 50) {
        newGenes[i] = this.Dna.getForce(i);
      }
      else {
        newGenes[i] = parent2.getDna().getForce(i);
      }
    }
    
    //Combines two rocket using a midpoint, where up to that midpoint is rocket 1's dna
    //and after this midpoint is only rocket two's dna
    
    //PVector[] newGenes = new PVector[lifespan];
    //int randomMidpt = floor(random(lifespan));
    //for(int i = 0; i < lifespan; i++) {
    //  if(i <= randomMidpt){
    //    newGenes[i] = this.Dna.getForce(i);
    //  }
    //  else {
    //    newGenes[i] = parent2.getDna().getForce(i);
    //  }
    //}
    
    dna newDna = new dna(newGenes);
    return newDna;
  }
  
  //Returns the Rocket's DNA
  dna getDna() {
    return this.Dna;
  }
  
  //Returns the best fitness of the rocket
  float returnFitness() {
    return this.fitness;
  }
   
}