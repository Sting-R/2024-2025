# Hey! Your almost at the end of the beginner level. Don't get too excited though
# As this part is a pretty confusing part of coding, and should have your full attention

# Classes and Objects
# Classes are a way to group functions and variables together
# Objects are instances of classes

# To help make this a bit simpler, let's use an example
# Let's say your building a neighborhood

# you would need blueprints for the houses, which in our case, represent classes
class blueprintForHouse:
    color = "red"
    size = "medium"


# You can then build the actual houses, which in our case, represent objects
house1 = blueprintForHouse()
house2 = blueprintForHouse()
# Then you can use the variables in the objects for whatever you need
print("What is the color of house 1? ")
print("Why it's " + house1.color + " of course!")

# you can also change the variables in the objects
# or in other words, change the color or size of the house
house2.color = "blue"
print("What is the color of house 2? ")
print("Why it's " + house2.color + " of course!")

# There are also sometimes built in functions in the classes
# take, for example, the __init__ function, which allows you to adjust
# variables (or properties of the house) when you create the object (house)


class revisedBlueprintForHouse:
    def __init__(
        self, color, size
    ):  # ignore the self for now, just know it's necessary
        self.color = color
        self.size = size


newHouse1 = revisedBlueprintForHouse("green", "large")
newHouse2 = revisedBlueprintForHouse("yellow", "small")
print("house 1 is " + newHouse1.color + " and " + newHouse1.size)
print("house 2 is " + newHouse2.color + " and " + newHouse2.size)

# You can also have functions in classes
# Take, for example, a function that changes the color of the house


class anotherBlueprintForHouse:
    def __init__(self, color, size):
        self.color = color
        self.size = size

    def changeToBasicHouse(self):
        self.color = "purple"
        self.color = "small"

    # to digress for a sec, let us now look at the self keyword
    # the self keyword is used to refer to the object itself
    # for example, if you wanted to change the color of the house,
    # you would need to use the self keyword
    # as just saying "color = newColor" would just create a new variable,
    # or change a pre-existing variable outside of the class
    # but by using "self" the program knows that you are referring
    # to the variable inside the class


newerHouse3 = anotherBlueprintForHouse("blue", "medium")
newerHouse3.changeToBasicHouse()
print("house 3 is " + newerHouse3.color + " & " + newerHouse3.size + " size!")
