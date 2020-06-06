totalTS = 0
totalTJ = 0
count = 0

fileName = input("Input File Name?: ")
with open(fileName) as f:
    for line in f:
        ts,tj = line.split()
        totalTS += int(ts)
        totalTJ += int(tj)
        count += 1

print("TS AVG: ", totalTS/1000000.0/count, "ms")
print("TJ AVG: ", totalTJ/1000000.0/count, "ms")

