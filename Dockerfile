FROM alpine:latest
RUN apk add openjdk11
EXPOSE 80
CMD ["java"]
