public class B2Operators {
  public static void main(String[] args) {
    // This next section is going to go over what 'operators' are, and what they do!
    // There are various operators, but the 4 main types are
    // - Arithmetic: adding, subtracting, multiplying, dividing, etc.
    // - Assignment: assigning a value to a variable
    // - Comparison: comparing two values
    // - Logical: if this AND this, then that, (hope that made sense, basically it's
    // just "AND" or "OR")

    // Examples

    // ARITHMETIC (+, -, *, /, %)

    int x = 5 + 10; // the "+" is an example of an arithmatic operator
    // however you can also use it to concatenate strings
    String testString = "Hello" + " World!"; // this will store testString as "Hello World!"
    // There is also another more unknown operator, the modulos operator
    // the modulos operator returns the remainder of a division problem
    int remainder = 10 % 3; // this will store remainder as 1

    // ASSIGNMENT (=, +=, -=, *=, /=, %=)

    int y = 5; // the "=" is an example of an assignment operator
    // the "+=" adds a value to a variable
    y += 5; // this will add 5 to y, so y will now be 10

    // COMPARISON (==, !=, >, <, >=, <=)

    // the next part contains if statements, which we will go over in the next
    // section
    // so for now just focus on the operators
    if (x == y) { // the "==" is an example of a comparison operator
      System.out.println("x is equal to y!");
    }

    // LOGICAL (&&, ||)

    if (x == 5 && y == 5) { // the "&&" is an example of a logical operator, representing "AND"
      System.out.println("x and y are both equal to 5!");
    }
    if (x == 5 || y == 5) { // the "||" is an example of a logical operator, representing "OR"
      System.out.println("either x or y is equal to 5!");
    }
  }
}
