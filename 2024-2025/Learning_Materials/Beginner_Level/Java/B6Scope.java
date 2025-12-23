public class B6Scope {
  static void assignMethod() {
    int numInFunction = 5;
  }

  static int returnMethod() {
    int numInFunction = 5;
    return numInFunction;
  }

  public static void main(String[] args) {

    // in this lesson we will be talking about scope. Scope is the area in which
    // a variable is (visible) accessible. For example, if you declare (create) a
    // variable inside a if-else statement, it will only be accessible inside that
    // if-else statement.

    boolean bool = true;
    if (bool) {
      int numInIfStatement = 5; // creates the variable x and sets it to 5 in the If statement
      System.out.println(numInIfStatement); // this will work because x is accessible here
    } // the Curly brackects represent the end of the if statements scope

    System.pit.println(numInIfStatement); // this will not work because x is not accessible here

    // The same goes for Functions
    myMethod();
    System.out.println(numInFunction); // this will not work because numInFunction is not accessible here

    // However, there are two simple ways to get around this. The first is to
    // declare the variable outside of the function or if statement.
    int y = 5; // variable is outside of if statement
    if (bool) {
      y -= 5; // modfies the variable y
    }
    System.out.println(y); // this will work because y is accessible here
    // The second way is to return the variable from the function
    System.out.print(returnMethod()); // this will work because the function returns the variable

    // This also goes for objects and classes, which we will cover next
  }
}
