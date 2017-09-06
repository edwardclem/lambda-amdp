import csv
from itertools import combinations

#generates HIT from list of URLs linking to folders containing tasks

#for each HIT, generate all possible combinations of demonstrations (3 choose 5)

outfile ="../../../data/blue_room/HIT_blue.csv"

root_url = "http://cs.brown.edu/~ngopalan/groundingImages/room_argmax"
# tasks = ["GetTheBlockNearTheRedBlock","GoNearTheLargestBlock", "GoNearTheSmallestBlock",
#         "goToBiggestGreenRoom", "goToBiggestRedroom" ,"goToSmallestGreenRoom/",
#         "goToSmallestRedRoom", "goToTheBiggestRoom", "goToTheSmallestRoom"]

tasks = ['goToBlueRoom', 'GoToTheBlueChair']

rows = []

#generate list of dictionaries:
for task in tasks:
    #each combination of 3 indices
    for comb in combinations(range(1,6),3):
        row = {}
        for i, index in enumerate(comb):
            row["before{}".format(i + 1)] = "{}/{}/{}/pre{}.png".format(root_url, task, index, index)
            row["after{}".format(i + 1)] = "{}/{}/{}/post{}.png".format(root_url, task, index, index)
        rows.append(row)

with open(outfile, 'w') as csvfile:
    fieldnames=['before1', 'after1', 'before2', 'after2', 'before3', 'after3']
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

    writer.writeheader()
    writer.writerows(rows)
