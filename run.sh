#!/bin/bash
export INSTANCE_CONNECTION_NAME=$1
export DATABASE_NAME=$2
export DATABASE_USERNAME=$3
export DATABASE_PASSWORD=$4
./mvnw spring-boot:run
