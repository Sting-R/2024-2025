# Now we are going to learn about lists in python

# Lists, in short, are a variable that holds multiple values

# In order to create a list, you would type square brackets
mylist = []  # this is an empty list

# You can also create a list with elements (values) in it
# Seperate each element with a comma
numList = [1, 2, 3, 4, 5, 6, 7, 8, 9]  # This is a list of integers

mixedList = [1, "Hello", 3.14, True]  # This is a list of mixed data types

# you can also add/remove things to a list after it's been created
mylist.append(1)  # this will add the number 1 to the end of the list
mylist.append(2)  # this will add the number 2 to the end of the list
mylist.append(3)  # this will add the number 3 to the end of the list
mylist.remove(2)  # this will remove the number 2 from the list

# something to note is that the elements (data in the list) are indexed
# this means that each element has a number associated with it
# the first element is 0, the second is 1, the third is 2, etc.
# you can access the elements by using the index
print(mylist[0])  # this will print the first element in the list

# you can also change the elements in the list
mylist[0] = 5  # this will change the first element in the list to 5

# you can also insert elements into the list at specific points
mylist.insert(1, 2)  # this will insert the number 2 at index 1

# finally, you can also have lists inside of lists
# this is called a 2D list, or multi-dimensional list
# this is useful for storing data in a grid format
ticTacToe = [["X", "O", "X"], ["O", "X", "O"], ["X", "O", "X"]]
