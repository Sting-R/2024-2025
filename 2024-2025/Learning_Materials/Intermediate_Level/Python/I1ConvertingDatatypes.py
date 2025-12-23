# Sometimes you need to convert data from one type to another to
# perform certain operations or to manipulate the data in a certain way.

# for example, let's say you want to add these to variables together
intVariable = 2
strVariable = " is the number of fingers I have"
# print(intVariable + strVariable) # this will give you an error
# as you can see, you can't add an integer and a string together

# so you need to convert the integer to a string
addedVariable = str(intVariable) + strVariable

print(addedVariable)

# you can do this with any data type
# int() converts to an integer
# str() converts to a string
# double() converts to a double
# bool() converts to a boolean

# just make sure it is possible to convert the data type
# print(int(strVariable)) # this will give you an error
# as you can't convert letters to a number

# regarding doubles to integers, it will round down when converted
var = 7.9
print(int(var))  # this will give you 7
