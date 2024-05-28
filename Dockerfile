FROM amazoncorretto:17-alpine-jdk

COPY ./build/libs/listywave.jar listywave.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-Dspring.profiles.active=prod,oauth,storage", "-jar", "listywave.jar"]
