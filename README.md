Localization service
============================

Contribution
============================
In case you are using Intellij IDEA please use Google Java format config file https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml

# Starting Cosmos DB Emulator

1. Download  and  install Cosmos DB emulator following steps:
https://docs.microsoft.com/en-us/azure/cosmos-db/local-emulator?tabs=ssl-netstd21
2. Run Emulator in MongoDB mode with following steps:
Go to package where emulator is stored and enable MongoDB endpoint
```bash
cd "C:\Program Files\Azure Cosmos DB Emulator"
.\CosmosDB.Emulator.exe /EnableMongoDbEndpoint
	```
3. Copy the Mongo Connection String, Primary Connection String, Primary key provided by the Azure Emulator for MongoDB by visiting
https://localhost:8081/_explorer/index.html
