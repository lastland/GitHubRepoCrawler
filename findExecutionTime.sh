#!/bin/bash

if [ $# -lt 2 ]
then
<<<<<<< HEAD
	echo "findExecutionTime.sh <folder> <pattern>"
	exit -1
=======
    echo "findExecutionTime.sh <folder> <pattern>"
    exit -1
>>>>>>> 6c4665d0d0b9881598c24b213cb4c732478ebbe3
fi

find $1 -name results*`hostname` > result-files.txt

rm file-with-test-result-list.txt
while read line; do
<<<<<<< HEAD
     	#echo "Inspecting: $line"
	grep $2 $line
	if [[ $? -eq 0 ]]
	then
		echo $line >> file-with-test-result-list.txt
	fi
done < result-files.txt
=======
    #echo "Inspecting: $line"
    grep $2 $line
    if [[ $? -eq 0 ]]
    then
        echo $line >> file-with-test-result-list.txt
    fi
done < result-files.txt

>>>>>>> 6c4665d0d0b9881598c24b213cb4c732478ebbe3
