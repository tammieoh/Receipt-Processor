## Receipt Processor
A simple application that processes receipts and calculates points of receipt based on receipt rules.

## Pre-Requisite Requirements
Install Java17, Gradle

## Getting Started 
1. Clone the repository: `git clone https://github.com/tammieoh/Receipt-Processor.git`
2. Cd into cloned repository and navigate to "receipt" directory.
3. Run `gradle clean build` to generate JAR.
5. Create docker image using `docker build -t [image-name] .`
6. Run `docker build -t 8080:8080 [image-name]`

## Technologies Used
- Java SpringBoot
