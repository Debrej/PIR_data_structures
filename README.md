# PIR 12 : Data stucture

Pour mettre en place le dataProvider et dataReader on se place dans le dossier hazelcast puis on rentre la commande :
```
mvn clean install
```

Ensuite, on ouvre deux terminaux dans le dossier hazelcast. On entre les commandes suivantes :

```
hazelcast$ java -Dcompressing=<true or false> -Dtest=<true or false> -jar SharedDataProvider/target/asyncPutVerticle.jar <interface> <sleeptime (ms)> <nmbProvider> -cluster

hazelcast$ java -Ddecompressing=<true or false> -jar SharedDataReader/target/asyncReaderVerticle.jar <interface> <sleeptime(ms)> <nmbReader> -cluster
```

Les adresses des membres doivent être sous cette forme, dans le fichier StartFiles/members.txt, une par ligne :

```
X.X.X.X
X.X.X.X
X.X.X.X
```

L'interface est l'adresse réseau de la machine sur laquelle le verticle est exécuté.

Le fichier StartFiles/filenamesProvider.txt contient les clés et les noms du ou des fichiers placés dans le cluster. On peut utiliser la commande suivante pour les simuler :

```
hazelcast/StartFiles$ java -jar <nmbkeys> <namefile>
```

Le fichier StartFiles/filenamesReader.txt contient les clés et les noms des fichiers que le reader va tenter de lire dans le cluster. (Pour le reader, préférez lire un unique fichier pour éviter les collisions)

Les fichiers filenames*.txt doivent être sous cette forme :

```
x,<nomfichier>
xx,<nomfichier>
x,<nomfichier>
```

Les fichiers que l'on veut transmettre doivent se trouver dans le dossier hazelcast/imagesSources
Les fichiers récupérés seront placés dans le dossier hazelcast/images


#Exemple de test :
pour la colone localhost, 1 fichier, image, Uniforme, Non Compressé, 5ko, 1 reader, GET :
	- dans un terminal, dans le dossier StartFiles :

```
java createKeys 1 img5ko.jpeg
```

	- dans un terminal, dans le dossier hazelcast :

```
java -Dcompressing=false -Dtest=false -jar SharedDataProvider/target/asyncPutVerticle.jar 127.0.0.1 1000 1 -cluster
```

	- dans un terminal, dans le dossier hazelcast :

```
java -Ddecompressing=false -jar SharedDataReader/target/asyncReaderVerticle.jar 127.0.0.1 1000 1 -cluster
```

	- Lorsque l'on a assez de mesures, dans un terminal, dans le dossier TestResults :

```
java computeMoyenne
```

	- Les résultats se trouvent dans les fichiers TestsResults/MoyenneRead.txt et TestsResults/MoyennePut.txt


ATTENTION : Ne pas lancer les tests de put et get en même temps. Si on veut tester les put : mettre -Dtest=true / Si on veut tester les get : mettre -Dtest=false dans la ligne de code SharedDataProvider
