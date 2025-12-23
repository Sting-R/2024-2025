# If statements are used to check if a condition is true or false
# They are basically the bread and butter of programming, and are used in
# almost everything
text = None

if 5 > 2:  # This is an if statement, and it checks if 5 is greater than 2
    # If the condition is true, then this code will run
    text = "value is greater than 2"
    # notice how this is indented, as opposed to
    # other lines we've created. This means that it anything indented
    # below the if statement is "inside" the if statement, and will run


# If-Else statements provide an alternative if the condition is false
if text == "your mom":
    print("Your mom is very nice and I would never say anything bad about her :)")
else:
    print(text)

# If-Else-If statements provide other alternatives if the condition is false
if text == "your mom":
    print("Your mom is very nice and I would never say anything bad about her :)")
elif text == "value is greater than 2":  # elif statement
    print("your doing good so far (or are you???)")
elif 48939018948 < 1:  # you can have more than one elif statement
    print("this will never run")
else:  # You can also still have an else statement
    print(text)
