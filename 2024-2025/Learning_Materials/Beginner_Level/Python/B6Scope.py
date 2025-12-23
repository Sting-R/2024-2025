# in this lesson we will be talking about scope. Scope is the area in which
# a variable is (visible) accessible. For example, if you declare (create) a
# variable inside a function, it will only be accessible inside that
# function.


def testfunction():
    x = 10
    return x


# testfunction()
# print(
#     x
# )
# If the above code was ran, this will cause an error, because x is not accessible outside of the function

# there are two ways around this
# 1. you can declare the variable outside of the function
# 2. you can return the variable from the function


def anotherFunction():
    x = 5
    return x


print(anotherFunction())  # this will print 5

# This also goes for objects and classes, which we will cover later

# FINAL NOTE! Python and java differ in how they handle scope.
# In java, variables create in if else statements and loops are subject to scope,
# and won't be visible (accessible) outside of there

bool = True
if bool:
    x = 10
print(x)  # this will cause an error in java, but not here, in python
