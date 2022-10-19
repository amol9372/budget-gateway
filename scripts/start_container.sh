#!/bin/bash

echo "Killing all active gateway containers"

containers=$(docker ps | grep "gateway" | awk '{print$1}')
if [ -n "$containers" ]
then
   docker kill $containers
   docker rm $containers
fi

aws --version

echo "Pulling image and starting container"

docker login -u AWS -p $(aws ecr get-login-password --region us-east-1) 667631227859.dkr.ecr.us-east-1.amazonaws.com
docker pull 667631227859.dkr.ecr.us-east-1.amazonaws.com/gateway
#docker run -it --env-file /home/ec2-user/docker/env_files/budgetapp.env -p 8091:8091 -d 667631227859.dkr.ecr.us-east-1.amazonaws.com/gateway
docker run -it -v /home/ec2-user/docker/env_files:/app/data --env-file /home/ec2-user/docker/env_files/budgetapp.env -p 8091:8091 -d 667631227859.dkr.ecr.us-east-1.amazonaws.com/gateway