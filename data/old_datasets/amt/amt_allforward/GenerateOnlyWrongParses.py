import sys
import re


currentFile = sys.argv[1]
if len(sys.argv) > 2:
    newFile = sys.argv[2]
else:
    newFile = "FileWithOnlyIncorrectParses.log"


# oldFileReader = open(currentFile)
newFileWriter = open(newFile, "w")

# next = oldFileReader.read()
with open(currentFile) as f:
    for line in f:
        # print(line)
        # listOfSubstring = line.split(" ")
        count =0
        for match in re.finditer('block0', line):
            count+=1
            if count>1:
                line = line[:match.start()+5] + '1' + line[match.end():]
                # print match.start()
                # print match.end()
                # for i in range(match.start(),match.end(),1):
                # listLine = list(line)
                # listLine[match.end()]='1'
                # list="".join(listLine)
        # print listOfSubstring

        # for word in listOfSubstring:
        #     if word=='block0':
        #         count+=1
        #         if(count>1):
        #             word = 'block1'
        #             print "was here"
        newFileWriter.write(line)


        # next = oldFileReader.read()
    # listOfSubstring = re.findall('block0', next)
    # listOfSubstring = line.split(" ")
    # print listOfSubstring
    # if len(listOfSubstring) > 2:
    #     listOfSubstring[2].replace("block0", "block1")
    #     print listOfSubstring[2]
    #     next = '_'.join(listOfSubstring)
    # newFileWriter.write(next)

newFileWriter.close()
# oldFileReader.close()