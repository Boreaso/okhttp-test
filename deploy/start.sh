pid=$(jps -l | grep okhttp-test | awk '{print $1}')
kill -9 $pid
nohup java -cp config -jar okhttp-test-1.0.jar &> /dev/null &
