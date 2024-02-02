#!/usr/bin/env bash

REPOSITORY=/home/ec2-user/app
cd $REPOSITORY

APP_NAME=tricountapi

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ] #2
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi


nohup java -jar /home/ec2-user/app/build/libs/tricountapi-0.0.1-SNAPSHOT.jar 1>/dev/null 2>&1 &
echo "run /home/ec2-user/app/build/libs/tricountapi-0.0.1-SNAPSHOT.jar"