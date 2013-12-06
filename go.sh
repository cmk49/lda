
rm -rf output
mkdir output
for i in {1..7}
do
	echo $i
	mkdir output/$i
	java -cp lib/commons-math3-3.2.jar:lib/opencloud.jar:bin/ Main 100 1000 1.0 1.0 output/$i/ > output/$i/likelihood.txt & 
done

