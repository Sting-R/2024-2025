// SKIP TO LINE 7 

// to declare (create) a function, you must use the following syntax:
public class I0JavaCheatsheet {
  static void function_name() {
      // code goes here
  }
  static boolean test_function() {
    System.out.println("Hello World!");
    return true;
  }
  static int add(int x, int y) {
    return x + y;
  }
  public int myInt(int x) {
    return x;
  }
  // this is the main function, which is called whenever you press run in the top right
  public static void main(String[] args) { 
    // Hello! This is a cheatsheet for the Python programmers are trasitioning to
    // Java
    // I will be going over the basics of Java, and how they compare to Python
    // Feel free to mess around and try out what you learn here!

    // ---------- Comments ---------- //

    // This is a single line comment
    /*
     * This is a multi-line comment
     */

    // ---------- Variables ---------- //

    // to create variables, you must specify the type of variable
    String text = "Hello World!"; // This is a String variable, called text, which holds the value "hello world"
    int number = 5; // This is an integer variable, called number, which holds the value 5

    // ---------- Print statements ---------- //

    // Print statements are a tad bit more complicated than in Python
    System.out.println("Hello World!"); // This is the basic print statement, which sets a new line to print
    System.out.print("Hello World!"); // This is the same as the above, but does not set a new line to print

    // ---------- Indedentation and curly braces ---------- //

    // Indentation doesn't show the scope of a things in java, in fact white space is ignored
    // To show the scope of something, java uses curly braces
    if (true) { // This is the start of the scope of the if statement
System.out.println("This is true!");
    } // end of the scope of if statement 
    
    
    // this is a ton of white space, but it doesn't matter
    
    
    
    else {
                                  System.out.println("This is false!");
                                  // This is still in the scope 
    } 

    // However, with that said, it is still good practice to indent your code, as it 
    // makes it easier to read and understand

    // ---------- Logic Operators (and, or, not) ---------- //

    // instead of using and, or, and not, java uses &&, ||, and !
    if (true && false) { // and
      System.out.println("This is true!");
    } else if (true || false) { // or
      System.out.println("This is false!");
    } else if (!true) { // not
      System.out.println("This is false!");
    }
    // another note, Java doesn't use elif, it uses else if

    // ---------- Functions ---------- //
    
    // functions are pretty different in java. 
    // to start, they have to be created before the main function (see line 12)

    // to see the syntax to create a function, see line 5
    
    // the public/static part determines whether the function needs to be used in a
    // object or not, we will go over this in more detail later (use static for now)

    // void determines whether the function returns a value or not. In this case
    // void means it doesn't, however if you want to return a string (see line 8)

    // if you want to take in parameters, you will also need to specify the variable type (see line 12)
    int total = add(5, 10); // this will return 15

    // ---------- Classes and Objects ---------- //

    // your best bet for this one is to just go through the files in the coures for classes, but the cliff notes version is...

    // Every class be the name of the file (see line 4)
    // Instead of using the function __init__, you create a constructor function using the class name
    // to create a new object, you use the following syntax
    I0JavaCheatsheet myObjectName = new I0JavaCheatsheet(); // this creates a new object called myObject
    // functions created with the "public" keyword need objects to be called,(see line 15)
    // while functions created with the "static" keyword do not 
    System.out.println(myObjectName.myInt(5)); // this will return 5

  }
}
