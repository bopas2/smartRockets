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
  PVector getForce(int time) {
    return this.genes[time];
  }
  
  //If we want to change a specific index of the gene array
  void editGene(int index, PVector splice) {
    genes[index] = splice;
  }
  
  //Randomly create random mutations
  rocket mutateDna(int mutationRate) {
    for(int i = 0; i < lifespan; i++) {
      int randi = int(random(100)); 
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