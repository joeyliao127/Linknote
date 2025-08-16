include ../../Docker/.env

JAR_NAME := target/$(PROJECT_NAME).jar
.PHONY: build restart

build:
	@echo "👉 Building Spring Boot JAR..."
	./mvnw clean package -DskipTests
	@echo "✅ Build complete."

up:
	@echo "🐳 Starting container..."
	docker-compose -f ../../Docker/docker-compose-app.yaml restart springboot
	@echo "✅ Docker container running."

re: build up

restart-docker:
	@echo "♻️ Restarting container with new JAR..."
	docker cp $(JAR_NAME) springboot:/app/app.jar
	docker restart springboot

logs:
	docker logs -f springboot