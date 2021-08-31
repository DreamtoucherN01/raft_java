
source ~/.bash_profile

output="output"
#if [ --d "$output"]; then
#  rm -rf "$output"
#else
#  echo "dir not exist"
if [ -e $output ]; then
  rm -rf $output
  mkdir $output
else
  mkdir $output
fi


mvn clean assembly:assembly
cp target/raft-jar-with-dependencies.jar output/
cp start.sh output/;