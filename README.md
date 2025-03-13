# ASOIAF Nexus

API for the ASOIAF TMG community

## Running Locally

**Requirements**

 * java JDK 17+

```bash
./gradlew run
```

This will start the service locally on port 8080

## Endpoints

Until we install swagger (or something like it) this will detail the expectations of the available endpoints

### `GET /api/v1/cards`

Returns all of the available unit data

## Deploying Nexus

Currently, Nexus is deployed to Google Cloud Platform with the following configured:
 * Artifact Registry for saving build docker images of the service
 * Google Kubernetes Engine for running the service

Access to Nexus is provided through Cloudflare. A tunnel has been established by following [this guide](https://developers.cloudflare.com/cloudflare-one/tutorials/many-cfd-one-tunnel/) to route `backend.asoiaf.nexus` to the nexus k8s service.