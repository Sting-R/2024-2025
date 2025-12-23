public class B73RevisedBlueprintForHouse { // Blueprint (class) for the house
  // attributes for the house
  String color;
  String size;

  // this is called a constructor, and is used to create the object,
  // this is where you can input parameters, if-else statements, etc.
  public B73RevisedBlueprintForHouse(String color, String size) {
    this.color = color;
    this.size = size;
    // notice how there is a "this." before the variable name
    // This is so the program knows that you are referring to the variable in this
    // object, and not another variable that could be floating around
  }
}
