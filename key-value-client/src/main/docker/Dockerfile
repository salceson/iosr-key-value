FROM frolvlad/alpine-oraclejdk8:slim
ADD key-value-client.jar app.jar
ENV CLIENT_ARGS = ""
ENTRYPOINT [ "sh", "-c", "java -jar app.jar $CLIENT_ARGS" ]
