public class B71ClassesAndObjects { // look, a class!

  // Hey! Your almost at the end of the beginner level. Don't get too excited
  // though
  // As this part is a pretty confusing part of coding, and should have your full
  // attention

  // Classes and Objects
  // Classes are a way to group functions and variables together
  // Objects are instances of classes

  // it's okay if that sounds super confusing. It's cause it is, but hopefully the
  // example we're about to go over will help you understand

  public static void main(String[] args) {
    // To help make this a bit simpler, let's use an example
    // Let's say your building a neighborhood

    // you would need blueprints for the houses, which in our case, represent
    // classes (go to B72BlueprintForHouse.java to see the class)

    // you would also need to build the houses, which in our case, represent objects
    // the syntax (wording) for creating a class is as follows:

    // class name = new class();
    B72BlueprintForHouse house1 = new B72BlueprintForHouse();

    // you can also access or modify the attibutes of the house (the object)
    System.out.println("House 1 is the color " + house1.color); // this will print out "red"
    house1.color = "blue"; // this will change the color of the house to blue
    System.out.println("House 1 is now the color " + house1.color); // this will print out "blue"

    // However, this in itself is not all that useful, it would be useful though if
    // we could change the attibutes of the house
    // When we create the class, which we can do! (See the file
    // B73RevisedBlueprintForHouse.java)

    // Now, we can create the house with the attributes we want
    B73RevisedBlueprintForHouse house2 = new B73RevisedBlueprintForHouse("green", "large");
    B73RevisedBlueprintForHouse house3 = new B73RevisedBlueprintForHouse("yellow", "small");

    System.out.println("House 2 is " + house2.color + " color and size " + house2.size + "\nHouse 3 is " + house3.color
        + " color and size " + house3.size);

    // you can also create a function in classes
    // let's say you created a basic function to revert a house the the industry
    // norm (see B74AnotherRevisedBlueprint.java)

    B74AnotherRevisedBlueprint house4 = new B74AnotherRevisedBlueprint("orange", "medium");
    System.out.println("House 4 is " + house4.color + " color and size " + house4.size);

    house4.changeToBasicHouse(); // this is where we call the function!
    // notice how you need to have the object name, then a period, then the function

    System.out.println("House 4 is now " + house4.color + " color and size " + house4.size + ", the industry norm!");

    // Finally, let is return to the "public" keyword. This is called an access
    // modifier, and it determines who can access the class or function
    // public: anyone can access the class or function
    // default: only classes or functions in the same package can access the class

    // no worries if the public is a bit confusing, we will cover it more in the
    // intermediate level

  }
}
