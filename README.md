# NetWorddle 


Procédure pour lancer le jeux
====
1.    Une cible __ant__ est fournie pour la compilation et l'exécution des programmes de maniere simple
**donc il faut installer ant**

2.  Se mettre  à la racine du projet ou se trouve le fichier __build.xml__ 

3.   Ensuite suivre les instructions suivantes :
### Serveur

        ant server -Darg0=port -Darg1=n -Darg2=m -Darg3=time
avec

__port__ : le port du serveur 

__n__ : le nombre de ligne de la grille

__m__ : le nombre de colonne de la grille

__time__ : le temps de jeu en seconde


ou 

        ant server -Darg0=port -Darg1=n -Darg2=m -Darg3=time -Darg4=dict


__dict__ : le chemin vers le dictionnaire 

### Client en mode console
        ant client -Darg0=host -Darg1=port 
       
avec 

__host__ : l'adresse du serveur (**localhost** pour une machine locale)

__port__ : le port du serveur 

### Client graphique

        ant game
            
            
## License

Voir la [LICENSE](LICENSE.md)     (GNU/GPL)    