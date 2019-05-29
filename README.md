# PIR 12 : Data stucture

Pour mettre en place le dataProvider et dataReader on se place dans le dossier hazelcast puis on rentre la commande :
```
mvn clean install
```

Ensuite, on ouvre deux terminaux, un dans SharedDataProvider et un dans SharedDataReader. On entre les commandes suivantes :

```
hazelcast/SharedDataProvider$ java -jar target/asyncPutVerticle.jar "<adresse>" "<interface>" -cluster

hazelcast/SharedDataReader$ java -jar target/asyncReaderVerticle.jar "<adresse>" "<interface>" -cluster
```

Enfin, on ouvre son navigateur à l'adresse _localhost:8080_ pour voir les échanges de données.