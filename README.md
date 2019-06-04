# PIR 12 : Data stucture

## Implementation avec Hazelcast

Pour mettre en place le dataProvider et dataReader on se place dans le dossier hazelcast puis on rentre la commande :
```
mvn clean install
```

Ensuite, on ouvre deux terminaux, un dans SharedDataProvider et un dans SharedDataReader. On entre les commandes suivantes :

```
hazelcast/SharedDataProvider$ java -jar target/asyncPutVerticle.jar "<fichier des membres>" "<interface>" -cluster

hazelcast/SharedDataReader$ java -jar target/asyncReaderVerticle.jar "<fichier des membres>" "<interface>" -cluster
```

Les adresses des membres doivent être sous cette forme, une par ligne :

```
X.X.X.X
X.X.X.X
X.X.X.X
```

L'interface est l'adresse réseau de la machine sur laquelle le verticle est exécuté.

Enfin, on ouvre son navigateur à l'adresse _localhost:8080_ pour voir les échanges de données.

## Vie du projet

### branchage
- Toujours du code stable, qui tourne, sur la branche main
- faire des branche pour chaque features

### Ligne de  developpement
1. Avoir des maps distribué
2. des local map sur chaque noeud qui compresse décompresse
3. les plus utilisé = compressé a la volé, moins utilisé = compressé local
4. 2e etape distribuer l'algo de compression pour qu'il indique le noeud sur lequel il est. La séquence plus utilisé par certain noeud est sur certain noeud

#### Pour l'encodage :
Prendre code java d'un encodeur style huffman. Dans l'encodeur cf la table, réfléchir comment lié la table et les données partagé. (pour splitter les données partagé).

#### Checkpoints Clement:
[How to Work With Multiple Verticles and Communication in Vert.x](https://medium.com/@hakdogan/working-with-multiple-verticles-and-communication-between-them-in-vert-x-2ed07e8e6425)
Ca marche!!!

### Prochaine réu
Lundi 3 juin à 14h

### Réu de labo.
8 a 9 jeudi etudiant 4tc
8 a 10 vendredi doctorant