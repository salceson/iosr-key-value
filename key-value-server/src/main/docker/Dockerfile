FROM frolvlad/alpine-oraclejdk8:slim
ADD key-value-server.jar app.jar
ENV SERVER_ARGS = ""
ENTRYPOINT [ "sh", "-c", "java -jar app.jar $SERVER_ARGS" ]
