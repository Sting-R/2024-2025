public class I1ConvertingDatatypes {
  public static void main(String[] args) {
    //  Sometimes you need to convert data from one type to another to 
    //  perform certain operations or to manipulate the data in a certain way.

    //  for example, let's say you want to add these to variables together
    int number = 1;
    String text = "1";

    // you can make the end result a string
    String totalText = number + text; // the number variable is auto converted to a string
    System.out.println(totalText); // totalNum is now the String "11"
  
    //  or you can make the end result an int
    Integer totalNum = number + Integer.parseInt(text); 
    // Notice how we had to use the Integer.parseInt() method to convert a string to an int
    // unlike before, where the int was auto converted to a string, we have to manually convert the string to an int
    System.out.println(totalNum); // totalNum is now the int 2

    // each variable type has a function to convert it to a string
    // for example, the int variable type has a function called Integer.toString()

    // each variable also has a function to convert a string to that variable type
    // for example, the Float variable type has a function called Float.parseFloat()
    // Just make sure it's possible to convert the String (you can't turn letters into numbers!)

    // regarding doubles/floats, converting to integer or lower data type will remove the decimal entirely
    double decimalTest = 1.6;
    int integerTest = (int) decimalTest; // this is called casting
    // Notice how we specified the data type we wanted to convert to in the parenthesis
    System.out.println(integerTest); // integer is now 1
  
    // This is just the basics, if you'd like, do a bit of googling to learn more about converting data types
    // as there is a lot out there, and sometimes the conversions are automatic, and sometimes they are tricky
  }
}
