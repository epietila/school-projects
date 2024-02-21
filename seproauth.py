import argon2
import json

USRCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
SPECIALS = "!@#$%^&*()_+-=[]}{;':\",./<>?\\|`~"


def hash_password(password, hasher):
    hashed_password = hasher.hash(password)
    return hashed_password


# Check if input password matches stored password
def check_password(input_password, hashed_password, hasher):
    try:
        hasher.verify(hashed_password, input_password)
        return True
    except argon2.exceptions.VerifyMismatchError:
        return False


# Store new credentials
def store_credentials(username, password):
  try:
    with open("accinfo.json", "r") as file:
      data = json.load(file)
  except FileNotFoundError:
    print("Internal error.")
    return

  # Add or update the new credentials
  data[username] = password

  # Write the updated data back to the JSON file
  with open("accinfo.json", "w") as file:
    json.dump(data, file)
    

def signup(hasher):
  # Create username
  username = ""
  while True:
    usr = input("Enter username: ").lower() 
    if any(c not in USRCHARS for c in usr):
      print("Invalid username.")
    elif check_user(usr, True) == "exists":
      print("Username already exists.")
    else:
      username = usr
      break

  # Create password
  password = ""
  while True:
    # I would actually use getpass but I don't want to 
    # download it too for this exercise
    pwd = input("Enter password: ")
    if len(pwd) < 8:
      print("Password must be at least 8 characters long.")
    elif len(pwd) > 64:
      print("Password must be no more than 64 characters long.")
    elif not any(c in SPECIALS for c in pwd):
      print("Password must contain at least one special character.")
    else:
      password = pwd
      break

  # Hash the password and store new user
  store_credentials(username, hash_password(password, hasher))


# Check if user exists. Return password if so, else return empty string
def check_user(username, new_user=False):
  f = open('accinfo.json', 'r')
  data = json.load(f)
  hash = ""
  try:
    for user in data.keys():
      if user == username:
        if not new_user:
          hash = data[user]
        else:
          hash ="exists"
    f.close()      
    return hash
  except Exception as e:
    print(e)
    f.close()
    return ""


# Attempt to login
def login(hasher):
  username = input("Enter username: ").lower()

  # Check if user exists
  hash = check_user(username)
  if hash != "":
    # I would actually use getpass but I don't want to 
    # download it too for this exercise
    input_password = input("Enter password: ")
    # Check if password matches
    if check_password(input_password, hash, hasher):
      print("Authentication successful!")
      return True
  
  print("Incorrect username or password.")
  return False
    

def main():
    
    hasher = argon2.PasswordHasher()

    fails = 0
    while True:
        choice = input("Login (L) or sign up (S)? ").lower()
        if choice == "s":
            signup(hasher)
        elif choice == "l":
            if not login(hasher):
              fails += 1
              if fails == 3:
                print("Too many failed login attempts.")
                return  
        elif choice == "q":
            return

if __name__ == "__main__":
    main()
