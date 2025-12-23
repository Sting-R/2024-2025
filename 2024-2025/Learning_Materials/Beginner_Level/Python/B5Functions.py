# If you have a section of code that you need to use multiple times,
# you can put it in a function and call that function
# whenever you need to use that code.

# To create the function, you need to use the keyword "def"
# followed by the name of the function and then a set of parentheses.
def testfunction():
    print("This is a function being called!")


# to have the code in the function run, you must "call" it
testfunction()

# you can create parameters in the function,
# which are variables that you can pass into the function when you call it


def testfunction2(x):
    print(x)


testfunction2("This is a function being called with a variable!")
testfunction2("Here it is being called again!")

# you can have as many variables as you want, as long as you seperate them with a comma
# you can also have functions return a value


def testfunction3(x, y, z):
    return x / y * z


print(testfunction3(10, 5, 2))

# there are also a few built in functions in python you can use, which don't require you to define them

# Take, for example, the input() function
username = input("What is your name? ")
# This will display "What is your name?"
# Then, it will wait for the user to type something and press enter
# it will then store whatever the user typed into the variable "usersname"
print("Hello, " + username + "!")
