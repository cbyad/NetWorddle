# NetWorddle 


Procédure pour lancer le jeux
====
1.    Une cible __ant__ est fournie pour la compilation et l'exécution des programmes de maniere simple
**donc il faut installer ant**

2.  Se mettre  à la racine du projet ou se trouve le fichier __build.xml__ 

3.   Ensuite suivre les instructions suivantes :
### Serveur

        ant server -Darg0=n -Darg1=m -Darg2=time
avec

__n__ : le nombre de ligne de la grille

__m__ : le nombre de colonne de la grille

__time__ : le temps de jeu 


ou 

            ant server -Darg0=n -Darg1=m -Darg2=time -Darg3=dict


__dict__ : le chemin vers le dictionnaire 

### Client en mode console
            ant client


### Client graphique

            ant game
            
            
            