#! /bin/bash

app="home-assistant-helper"

git pull

# 编译代码
mvn clean package -DskipTests
if [ "$(basename $(ls target/*.jar))" == "" ]; then
    dir="$(pwd)/target"
    echo "not find jar on ${dir}"
    ls -l ${dir}
    exit -1
fi

if [ "${IMAGE_NAME}" == "" ]; then
    IMAGE_NAME=$(basename $(pwd))
fi

echo "image is ${IMAGE_NAME}"

# 清理已运行的容器
containerId=$(docker ps -a | grep ${IMAGE_NAME} | awk '{print $1}')
echo "has run containerId is ${containerId}"

if [ "${containerId}" != "" ]; then
    docker rm -f ${containerId}
    echo "close ${containerId}"
fi

# 重新构建该镜像
docker rmi ${IMAGE_NAME}
docker build -t ${IMAGE_NAME} .

docker run -d \
    --restart=always \
    --name=${app} \
    -p 40004:40002 \
    -e TZ=Asia/Shanghai \
    -v /jdata/docker/${app}/config:/app/config \
    -v /jdata/docker/${app}/tmp:/tmp \
    -v /jdata/docker/${app}/logs:/app/logs \
    --add-host=vs.fun:10.1.1.11 \
    ${app} \

