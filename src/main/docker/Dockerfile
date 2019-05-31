FROM centos

RUN yum install -y java-11-openjdk-devel

VOLUME /tmp

ADD recipe-0.0.1-SNAPSHOT.jar myapp.jar

RUN sh -c 'touch myapp.jar'

RUN sh -c 'ls -ltr'

ENTRYPOINT ["java", "-jar", "myapp.jar"]