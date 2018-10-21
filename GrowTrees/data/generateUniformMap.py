size = 400
stoneA = ["S"]*size
dirtA = ["D"]*size
grassA = ["G"]*size
airA = ["A"]*size


with open("map.txt", 'w') as f:
    num = 0
    f.write("400\n400\n")
    while num < 30:
        for i in range(size):
            f.write(",".join(stoneA) + "\n")
        num += 1
    while num < 45:
        for i in range(size):
            f.write(",".join(dirtA) + "\n")
        num += 1
    
    for i in range(size):
        f.write(",".join(grassA) + "\n")
    num += 1

    while num < 60:
        for i in range(size):
            f.write(",".join(airA) + "\n")
        num += 1