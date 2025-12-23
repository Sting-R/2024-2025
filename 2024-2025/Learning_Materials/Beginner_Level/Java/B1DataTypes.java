public class B1DataTypes { // ignore for now
  public static void main(String[] args) { // ignore for now
    // Hello! Ths is the file for all the various data types in java! Here are
    // the main ones that we will be focusing on right now
    int myNum = 5; // Integer (whole number)
    double myDoubleNum = 5.99; // Decimal point number
    float myFloatNum = 5.99f; // Decimal point number (needs to have the f at the end)
    char myLetter = 'D'; // Character
    boolean myBool = true; // Boolean
    String myText = "Hello"; // String

    // By now, you should notice that each of the data types follow a pattern. They
    // have a name, equals sign, then a value. This forms what I like to call the
    // "variable equation".
    // data_type variable_name = value;

    // Now, let's try printing out the values of these variables. To do this, we
    // will use the pre-made method System.out.println(). This method will print
    // out whatever is inside the parentheses. For example:

    // We can print values
    System.out.println("Is 2+2=4?");
    // Or we can print out variables
    System.out.println(myBool);

    // We can also create variables with nothing in them and then assign them later
    int myNum2;
    myNum2 = 10; // Notice how we don't use the data type when we assign a value to a variable
                 // that's already been created
    System.out.println(myNum2);

    // Now, why don't you try some of yourself? create a few variables, and print
    // them out. Have fun with it!

  } // no code can be past this curly brace
}
