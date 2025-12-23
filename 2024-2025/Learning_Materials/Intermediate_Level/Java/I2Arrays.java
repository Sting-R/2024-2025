public class I2Arrays {
  public static void main(String[] args) {
    //quick note: /* */ are multiline comments
    /* 
    Now we are going to learn about array on python

    array, in short, are a variable that holds multiple values (called elements)

    In order to create a array, you would type square brackets 
    */ 

    String[] names = {"Justin", "Henry", "Regulus", "Brandon"};
    // to create a array, put square brackets after the variable
    // then when you put in your values, put curly brackets and separate each value (element) with a comma

    // each element is assigned a number, called an index. the first element is 0, the second is 1, and so on

    // to access a value, you would type the array name, then the index number in square brackets
    System.out.println(names[1]); // this will print out "Henry"

    // you can also reassign values using the index
    names[3] = "Gunner";  // this will change the "Brandon" to "Gunner"
    
    // Finally, you can also have a array inside of array
    // This is called a 2d array, or a multi dimensional array
    // useful for storing stuff in a grid format

    // to do this, add two square brackets after the variable
    String[][] grid = {
      {"x", "0", "x"},
      {"0", "x", "0"},
      {"x", "0", "x"}
    };
    

  }
}
