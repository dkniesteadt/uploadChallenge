Run Storage

To run Storage run the jar provided in this directory like below.
Declare this is a storage operation and what file you are loading.
I have provided example.txt below.

java -jar duganSampleCode.jar storage src/main/resources/example.txt

This will store everything in a csv file





Run Query

Just run the querys just like in the instructions. 
I wrote a simple scripts that calls the jar.

./query -s TITLE,REV,DATE -f DATE=2014-04-01
