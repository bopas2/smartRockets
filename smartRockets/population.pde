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
  void createPop() {
    for(int p = 0; p < this.numberOfParticles; p++){
        popper[p] = new rocket(null);
    }
  }
  
  //returns the rocket at a certain index of the population
  rocket returnRock(int index) {
    return popper[index];
  }
  
  void repopulate(int mutationRate) {
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
  int[] determineParents() {
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