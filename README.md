#ASOIAF NEXUS

Welcome to the official GitHub repository for ASOIAF Nexus — a community-driven platform designed to enhance the competitive and organizational experience for A Song of Ice and Fire: The Miniatures Game.
---
What is Nexus?
ASOIAF Nexus is built as a modular, modern, and open-source alternative to existing tools in the community. It provides support for:
-  Player statistics and profiles  
-  Faction tracking and performance insights  
-  Tournament management and rankings  
-  Future-ready extensions to improve event organization and player engagement

We believe in transparency, community ownership, and open development — while ensuring that core contributions remain secure and high quality.

This is the backend of ASOIAF Nexus. For the full experience, visit:

Frontend/UI
 [ASOIAF-Nexus-UI](https://github.com/ASOIAF-NEXUS/ASOIAF-Nexus-UI)

Contribution Policy
This repository is public and visible to all for full transparency.
However:
- Only approved collaborators can push to `main`
- We do not accept unsolicited pull requests at this time
-  Forking is allowed for experimentation, exploration, and transparency

We welcome suggestions via issues or discussions, but to maintain coherence and focus, the active development is managed by a small core team.


 Who Maintains This?

This project is led by community members passionate about improving the ASOIAF competitive scene.  
For questions or feedback, feel free to reach out through our Discord : https://discord.gg/R9BFP4BdPT 
Reach out for reaper64 in discord if you are interested in joining

A public version of the Nexus platform will be launched soon at:  
[https://asoiaf.nexus](https://asoiaf.nexus)**

Stay tuned!

License

This project is licensed under the GNU General Public License v2.0.  
You are free to fork, use, and build upon the code, but any modifications or redistributions must also be licensed under the GPL-2.0.

For more details, see the [LICENSE](./LICENSE) file.


For more technical information:


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
