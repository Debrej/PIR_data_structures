# PIR 12 : Data stucture

## Implementation avec Hazelcast

Pour mettre en place le dataProvider et dataReader on se place dans le dossier hazelcast puis on rentre la commande :
```
mvn clean install
```

Ensuite, on ouvre deux terminaux dans hazelcast. On entre les commandes suivantes :

```
java -jar SharedDataProvider/target/asyncPutVerticle.jar SharedDataProvider/members.txt <interface> SharedDataProvider/keys.txt
-cluster

java -jar SharedDataReader/target/asyncReadVerticle.jar SharedDataReader/members.txt <interface> SharedDataReader/keys.txt <nombre de Reader> -cluster
```

Les adresses des membres doivent être sous cette forme, une par ligne :

```
X.X.X.X
X.X.X.X
X.X.X.X
```

L'interface est l'adresse réseau de la machine sur laquelle le verticle est exécuté.

Les clés de recherches doivent être sous cette forme, une par ligne :
```
xxxx
xx
x

```
Pour modifier le nombre de fichiers dans le cluster, il suffit de modifier le nombre de clés dans le fichier SharedDateProvider/keys.txt.
Pour cela, dans DataSharedProvider, utiliser la commande :
```
java createKeys <nombre de fichiers> 

```
Pour DataSharedReader, laisser la clé "a" à chercher.
Penser à bien changer le fichier placé dans le cluster.

```
Exemple de test :
pour la colone localhost, 1 fichier, image, Uniforme, Non Compressé, 20775, 1 :
	-> On modifie le nom du fichier dans SharedDataProvider/PutFile.java : private String NAME_FILE="image20775.jpeg"
	-> java createKeys 1
	-> java -jar SharedDataProvider/target/asyncPutVerticle.jar SharedDataProvider/members.txt 127.0.0.1 SharedDataProvider/keys.txt
-cluster
	-> java -jar SharedDataReader/target/asyncPutVerticle.jar SharedDataReader/members.txt 127.0.0.1 SharedDataReader/keys.txt 1 -cluster
	-> On ouvre le fichier temps_lecture.txt
	-> On copie-colle le tout dans le tableur
	-> On sélectionne separated by Other ":"
	-> ctrl+h pour remplacer les "." par des ","
	-> On récupère la moyenne 
	-> On copie-spécial_colle dans la case AJ8 (cocher number et pas formula)
```

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
