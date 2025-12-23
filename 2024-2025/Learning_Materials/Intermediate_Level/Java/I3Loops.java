public class I3Loops {

  public static void main(String[] args) {

    // loops are how we can easily repeat code
    
    //  ------- While loop ------- //
    
    //the while loop repeats whenever the condition is true.
    // you will need to be very careful, since this can lead to infinite loops

    int count = 0;
    while (count < 5) { // while count is less than 5
      System.out.println(count); // print count
      count++; // adds one to clap
    }

    // Example of an infinite loop
    // while (count < 6) {
    //   System.out.println("Hello World!");
    // }

    //  ------- For loop ------- //

    // the for loop is a bit more complicated, but is safer than the while loop
    // the for loop has three parts, the initialization, the condition, and the increment
    for (int i = 0; i < 5; i++) { // i is initialized to 0, and is incremented by 1 every loop
      System.out.println("take two: " + i); // prints i
    }

    // Something fun you can do with for loops is also iterate (loop) through arrays
    String[] names = {"John", "Bob", "Alice"};
    for (int i = 0; i < names.length; i++) { // i is initialized to 0, and is incremented by 1 every loop
      System.out.println(names[i]); // prints the name at index i
    }

  }
}