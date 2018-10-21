import os
size = 100
stoneA = ["S"]*size
dirtA = ["D"]*size
grassA = ["G"]*size
airA = ["A"]*size

endl = os.linesep

with open("map.txt", 'w') as f:
    num = 0
    f.write("{}{}{}{}".format(size,endl, size,endl))
    while num < 3:
        for i in range(size):
            f.write(",".join(airA) + endl)
        num += 1
        print(num)
    while num < 4:
        for i in range(size):
            f.write(",".join(grassA) + endl)
        num += 1
        print(num)
    for i in range(size):
        f.write(",".join(dirtA) + endl)
    num += 1

    while num < 7:
        for i in range(size):
            f.write(",".join(stoneA) + endl)
            print(num)
        num += 1