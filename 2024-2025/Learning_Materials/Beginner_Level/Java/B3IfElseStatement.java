public class B3IfElseStatement {

  public static void main(String[] args) {

    String text; // This is an empty variable, that we will use later

    // If statements are used to check if a condition is true or false
    // They are basically the bread and butter of programming, and are used in
    // almost everything

    // This is an if statement, and it checks if 5 is greater than 2
    if (5 > 2) { 
      text = "value is greater than 2"; // If the condition is true, then this code will run
      // notice how this line is inside the curly braces, this means that anything inside 
      // the curly braces will run if the condition is true
    }

    // If-Else statements provide an alternative if the condition is false
    if (text == "your mom") {
      System.out.println("Your mom is the best and I would never say anything bad :)");
    } else {
      System.out.println(text); // If the condition is false, then this code will run
    }

    // If-Else-If statements provide multiple alternatives if the condition is false
    if (text == "your mom") {
      System.out.println("your mom is the best and I would never say anything bad :)");
    } else if (text == "value is greater than 2") {
      System.out.println("Your doing good so far! (or are you???)");//If this condition is true, then this code will run
    } else  if (4903 < 0){
      System.out.println("This will never run, because the previous condition is true");
    } else { // you can still add an extra else statement at the end
      System.out.println(text);
    }

  }
}