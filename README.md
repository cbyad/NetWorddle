# NetWorddle 

## Architecture de communication 
le __Messager__ est un thread qui sert de canal de communication entre les joueurs du jeu (privé/public)
Il est initialisé par le __Server__ et transmis aux moteur de jeux __NetWorddleGame__ qui lui va se charger de demarrer la routine dans 
son constructeur. Initialement __Messager__ est mis en attente de notification afin de delivrer les messages qu'il possedent