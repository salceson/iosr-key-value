FROM frolvlad/alpine-oraclejdk8:slim
ADD key-value-verify.jar app.jar
ENV VERIFY_ARGS = ""
CMD [ "sh", "-c", "java -jar app.jar $VERIFY_ARGS" ]
