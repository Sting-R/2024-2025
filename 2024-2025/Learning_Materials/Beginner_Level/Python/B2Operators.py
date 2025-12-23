#  This next section is going to go over what 'operators' are, and what they do!
#      There are various operators, but the 4 main types are
#      - Arithmetic: adding, subtracting, multiplying, dividing, etc.
#      - Assignment: assigning a value to a variable
#      - Comparison: comparing two values
#      - Logical: if this AND this, then that, (hope that made sense, basically it's
#        just "AND" or "OR")

# Examples

# ARITHMETIC (+, -, *, /, %)

x = 5 + 4  # The "+" is an arithmetic operator
# however you can also use it to concatenate strings
testString = "Hello" + " World!"  # this will store testString as "Hello World!"
# There is also another more unknown operator, the modulos operator
# the modulos operator returns the remainder of a division problem
remainder = 10 % 3  # this will store remainder as 1

# ASSIGNMENT (=, +=, -=, *=, /=, %=)

y = 5  # The "=" is an assignment operator
# the "+=" adds a value to a variable
y += 5  # this will add 5 to y, so y will now be 10


# COMPARISION (==, !=, >, <, >=, <=)

# the next part contains "if statements", which are used to check if a condition is true\
# We will cover these in more detail later, but for now just focus on the operators
if x == 5:  # The "==" is a comparison operator
    print("x is equal to 5!")

# LOGICAL (and, or)

if x == 5 and x > 4:  # The "and" is a logical operator
    print("x is equal to 5 and greater than 4!")
if x == 5 or x > 4:  # The "or" is a logical operator
    print("x is equal to 5 or greater than 4!")
