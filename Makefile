setup:
	gradle wrapper --gradle-version 7.4.2

clean:
	./gradlew clean

build:
	ROLLBAR_TOKEN=fakeToken ./gradlew clean build

start:
	ROLLBAR_TOKEN=fakeToken ./gradlew bootRun --args='--spring.profiles.active=dev'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

install:
	./gradlew installDist

start-dist:
	ROLLBAR_TOKEN=fakeToken ./build/install/app/bin/app

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	ROLLBAR_TOKEN=fakeToken ./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

generate-migrations:
	gradle diffChangeLog

db-migrate:
	./gradlew update

.PHONY: build