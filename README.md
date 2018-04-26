# NetWorddle 


Procédure pour lancer le jeux
====
NB: une cible __ant__ est fournie pour la compilation et l'exécution des programmes de maniere simple
Donc il faut installer ant

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
            
            
            