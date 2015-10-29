#!/bin/bash

if [ $# -lt 2 ]
then
    echo "findExecutionTime.sh <folder> <pattern>"
    exit -1
fi

find $1 -name results*`hostname` > result-files.txt

rm file-with-test-result-list.txt
while read line; do
    #echo "Inspecting: $line"
    grep $2 $line
    if [[ $? -eq 0 ]]
    then
        echo $line >> file-with-test-result-list.txt
    fi
done < result-files.txt

