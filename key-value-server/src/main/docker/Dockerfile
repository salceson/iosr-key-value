FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp/logs
ADD key-value-server.jar app.jar
ENV SERVER_ARGS = ""
ENTRYPOINT [ "sh", "-c", "java -jar app.jar /tmp/logs $SERVER_ARGS" ]
