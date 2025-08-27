docker context use desktop-linux
DOCKER_HOST=$(docker context inspect desktop-linux --format '{{.Endpoints.docker.Host}}')
export DOCKER_HOST
./mvnw spring-boot:build-image -Pdebug -DskipTests -Dspring-boot.build-image.imageName=reviewer:latest