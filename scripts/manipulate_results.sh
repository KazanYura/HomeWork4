mkdir ./results
for jsonFile in ./*.json;
do
	sed -i '$ s/.$//' $jsonFile
	value=`cat $jsonFile`
	{ echo -n '{'$value'}';} > ./results/${jsonFile%.json}.json;
	rm $jsonFile
done;
