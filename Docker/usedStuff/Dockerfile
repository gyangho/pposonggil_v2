FROM gonaging/used-stuff:1.0

# ARG JAR_FILE=target/*.jar
ARG JAR_FILE=build/libs/usedStuff-0.0.1-SNAPSHOT.jar app.jar

WORKDIR /usedStuff

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/usedStuff/app.jar"]