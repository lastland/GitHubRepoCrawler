

import os, re


class TimeClassifier:
    
    #Counters
    lessThanOne = 0;
    moreThanOne = 0;
    moreThanTen = 0;
    moreThanOneMinute = 0;
    moreThanTenMinutes = 0;

    #Lists
    moreThanTenList = [];
    moreThanOneMinuteList = [];
    moreThanTenMinutesList = [];

    def count(self, time, test, file):
    	
    	averageTime = float(time)
    	if (averageTime >= 10E9):
    		self.moreThanOne+=1
    		if (averageTime >= 10E10):    		
	    		self.moreThanTen+=1
	    		self.moreThanTenList.append(file+"@test="+test)
	    		if (averageTime >= (60 * 10E9)):
	    			self.moreThanOneMinute+=1
	    			if (averageTime >= (10 * 60 * 10E9)):
	    				self.moreThanTenMinutes+=1
    	else:
    		self.lessThanOne+=1

    def report(self):
    	print("The analyzed tests have the following average execution times:\n")
    	print("time < 1s: " + str(self.lessThanOne))
    	print("time >= 1s: " + str(self.moreThanOne))
    	print("time >= 10s: " + str(self.moreThanTen))
    	print("time >= 1m: " + str(self.moreThanOneMinute))
    	print("time >= 10m: " + str(self.moreThanTenMinutes))

    	print("\n\nMore than 10s:")
    	for test in self.moreThanTenList:
    		print(test)


classifier = TimeClassifier();
for dirname, dirnames, filenames in os.walk('.'):
    # # print path to all subdirectories first.
    # for subdirname in dirnames:
    #     print(os.path.join(dirname, subdirname))


    for filename in filenames:
    	if (re.search("^results[^@]+@[^@]+", filename)):
        	with open(os.path.join(dirname, filename)) as f:
    			for line in f:
        			# print(line);
        			time_prefix = "running time:"
        			time = line[line.find(time_prefix) + len(time_prefix):len(line)]

        			try:
        				classifier.count(time, line, filename)
        			except ValueError:
        				print("Could not convert " + time + "\n")
        				print(line +"\n========\n")
    
classifier.report();

    # # Advanced usage:
    # # editing the 'dirnames' list will stop os.walk() from recursing into there.
    # if '.git' in dirnames:
    #     # don't go into any .git directories.
    #     dirnames.remove('.git')


        