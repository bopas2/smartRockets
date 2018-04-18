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
int mutationRate = 2;


target targ;
obstacle ob1;
obstacle ob2;
population popul;

//keep a list of all the obstacles we have
ArrayList<obstacle> obstacles = new ArrayList<obstacle>();

//Basic setup
void setup() {
    size(700,800);
    popul = new population(numOfParticles);  
    targ = new target(60,width/2,height/6);
    //ob1 = new obstacle(400,80,width/2,height/2);
    //ob2 = new obstacle(400,80,width,height/2);
    //obstacles.add(ob1);
    //obstacles.add(ob2);
}

//Endless loop
void draw() {
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
void mousePressed() {
  if(clickNumber == 0){
    initX = mouseX;
    System.out.println(initX);
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