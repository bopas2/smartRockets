import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class smartRockets extends PApplet {

//size of screen
int width = 700;
int height = 800;
//how long each particle's life is
int lifespan = 150;
//maximum force that can be exerted on the particle
int maxForce = 3;
//how many particles we have
int numOfParticles = 500;
//counter to track time
int time = 0;
//how often a vector should be random
int mutationRate = 6;


target targ;
obstacle ob1;
obstacle ob2;
population popul;

//keep a list of all the obstacles we have
ArrayList<obstacle> obstacles = new ArrayList<obstacle>();

//Basic setup
public void setup() {
    
    popul = new population(numOfParticles);  
    targ = new target(60,width/2,height/6);
    //ob1 = new obstacle(400,80,width/2,height/2);
    //ob2 = new obstacle(400,80,width,height/2);
    //obstacles.add(ob1);
    //obstacles.add(ob2);
}

//Endless loop
public void draw() {
  //Timer for each experiments duration
  if(time < lifespan) {
    //Draw non-particles
    targ.drawTarget();
    for(obstacle a : obstacles) {
      a.drawObstacle();
    }
    //Draw + update particles
    for(int j = 0; j < numOfParticles; j++) {
      rocket current = popul.returnRock(j);   //get rocket
      PVector force = current.getForce(time); //get force that should be exerted on it right then
      current.applyForce(force);  //apply the force
      current.update(obstacles,targ);          //update+check particle positions
      current.show();           //update display
      current.calculateFitness(targ.targPos(),targ.targWid());
    }
    time++;
  }
  else {
    time = 0;
    //Create a new generation, rinse and repeat. 
    popul.repopulate(mutationRate);
  }
}

int clickNumber = 0;
int initX, initY, endX, endY = 0;

//for using mouse to create obstacles!
public void mousePressed() {
  if(clickNumber == 0){
    initX = mouseX;
    initY = mouseY;
    clickNumber++;
  }
  else {
    endY = mouseY; 
    endX = mouseX;
    obstacle newObby = new obstacle(abs(endX-initX),abs(endY-initY),abs(floor((initX+endX)/2)),abs(floor((initY+endY)/2)));
    obstacles.add(newObby);
    initX = initY = endX = endY = clickNumber = 0;
  }
}
public class dna {
  //Each index of the gene is the force that acts on the particle at that specific second
  PVector[] genes = new PVector[lifespan];
  //Constructor, copies imported genes, or makes an random set
  dna(PVector[] copyGenes) {
    if(copyGenes != null) {
      this.genes = copyGenes;
    }
    //Random set basically creates a random direction vector, and sets the magnititude to the greatest force allowed
    else {
      for(int i = 0; i < lifespan; i++) {
        this.genes[i] = PVector.random2D();
        this.genes[i].setMag(maxForce);
      }
    }
  }
  
  //Force at a certain index of the dna / time
  public PVector getForce(int time) {
    return this.genes[time];
  }
  
  //If we want to change a specific index of the gene array
  public void editGene(int index, PVector splice) {
    genes[index] = splice;
  }
  
  //Randomly create random mutations
  public rocket mutateDna(int mutationRate) {
    for(int i = 0; i < lifespan; i++) {
      int randi = PApplet.parseInt(random(100)); 
      if(randi <=mutationRate) {
        PVector randomBoy = PVector.random2D();
        randomBoy.setMag(maxForce);
        this.genes[i] = randomBoy;
      }
    }
    rocket mutatedRocket = new rocket(this);
    return mutatedRocket;
  }
  
}
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
  public void drawObstacle() {
    pushMatrix();
    fill(255);
    rect(popsicle.x,popsicle.y,obstacleWid,obstacleHt);
    popMatrix();
  }
  
  public PVector returnPos() {
    return popsicle;
  }
  
  public int ObWid() {
    return obstacleWid;
  }
  
  public int ObHt() {
    return obstacleHt;
  }
    
}
class population {
  int numberOfParticles = 0;
  rocket[] popper;
  
  //constructor
  population(int numberofparticles) {
    popper = new rocket[numberofparticles];
    this.numberOfParticles = numberofparticles;
    createPop();
  }
  
  //fills up the array with actualy rockets
  public void createPop() {
    for(int p = 0; p < this.numberOfParticles; p++){
        popper[p] = new rocket(null);
    }
  }
  
  //returns the rocket at a certain index of the population
  public rocket returnRock(int index) {
    return popper[index];
  }
  
  public void repopulate(int mutationRate) {
    rocket[] tempPop = new rocket[numberOfParticles];
    int[] parentsIndex = this.determineParents();
    for(int g = 0; g < numOfParticles; g++) {
      //get combined dna
      dna Xbreed = popper[parentsIndex[0]].crossBreed(popper[parentsIndex[1]]);
      //mutate the dna and get a new rocket
      rocket answer = Xbreed.mutateDna(mutationRate);
      tempPop[g]  = answer;
    }
    popper = tempPop;
  }
   
  //determines which parents we want to reproduce with 
  public int[] determineParents() {
    int[] answer = new int[2];
    float sum = 0;
    for(int i = 0; i < this.numberOfParticles; i++) {
      sum += this.popper[i].returnFitness();
    }
    for(int s = 0; s < 2; s++) {
      float r = random(sum);
      int index = 0;
      while(r>0) {
        r -= this.popper[index].fitness;
        index++;
      }
      answer[s] = index - 1;
    }  
    return answer;
  }

}
class rocket {
  //Rocket movement variables
  PVector pos = new PVector(width/1.2f, height - 50);
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
    red = PApplet.parseInt(random(255));
    blue = PApplet.parseInt(random(255));
    green = PApplet.parseInt(random(255));
  }
  
  //Returns the force that is currently acting on the rocket
  public PVector getForce(int time) {
    return Dna.getForce(time);
  }
  
  //check for a collision between a rocket and an obstacle
  public void checkCollision(obstacle obby) {
    int x = floor(obby.returnPos().x);
    int y = floor(obby.returnPos().y);
    int wid = obby.ObWid();
    int ht = obby.ObHt();
    
    if(this.pos.x < x + wid/2 && this.pos.x > x - wid/2 && this.pos.y > y - ht/2 && this.pos.y < y + ht/2) 
      this.stuck = true;
  }
  
  //Updates position, velocity, acceleration of the particle, and checks that it has not collided with obstacles, or with the screen edges.
  public void update(ArrayList<obstacle> obs, target targo) {
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
  public boolean atTarget() {
    return this.atTarget;
  }
  
  //Displays the rocket
  public void show() {
      pushMatrix(); 
      fill(this.red,this.blue,this.green);
      translate(this.pos.x,this.pos.y);
      rotate(this.vel.heading()); //Points towards it's current velocity
      rectMode(CENTER);
      rect(0,0,40,10);
      popMatrix();
  }
  
  //Applies vector forces
  public void applyForce(PVector force) {
    this.acel.add(force);
  }
  
  //Calculates fitness of the rocket
  public float calculateFitness(PVector targ, int targWid) {
    
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
  public dna crossBreed(rocket parent2) {
    
    //Combines two rockets where each 'gene' is randomly taken from rocket 1 or rocket 2
    
    PVector[] newGenes = new PVector[lifespan];
    for(int i = 0; i < lifespan; i++) {
      int ra = PApplet.parseInt(random(100));
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
  public dna getDna() {
    return this.Dna;
  }
  
  //Returns the best fitness of the rocket
  public float returnFitness() {
    return this.fitness;
  }
   
}
class target {
  int targetWidth = 0;
  PVector target;
  
  //constructor
  target(int wid, int x, int y) {
    this.targetWidth = wid;
    this.target = new PVector(x,y);
  }
  
  public PVector targPos() {
    return this.target;
  }
  
  public int targWid() {
    return targetWidth;
  }
  
  //draws the target
  public void drawTarget() {
    background(0);
    pushMatrix();
    fill(255);
    ellipse(target.x,target.y,targetWidth,targetWidth);
    popMatrix();
  }

  
}



  public void settings() {  size(700,800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "smartRockets" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
