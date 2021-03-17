# Parcours NFC

https://github.com/Ainorte/Parcours-NFC/

## Auteurs

- Kamel BOUREK
- William POITEVIN
- Derek SAMSON
- Mohamed TRAORE

## Description
Parcours NFC est un jeu de parcours qui inclut plusieurs étapes de jeu, chaque étape est validée grâce à la lecture du tag NFC qui lui est associé.
En scannant le tag d'une étape, la position de l'étape suivante est alors indiquée. L'action est répetée jusqu'à l'arrivé au tag contenant le message de victoire.

## Actions
* Création du parcours (bouton avec icône crayon en haut à droite). Ajouter la description de l'étape qui s'ajoutera au moment de l'écriture du tag NFC.
* Supprimer toutes les étapes du parcours (bouton avec icône corbeille en haut à droite).
* Bouton flottant carte en bas : Ouvre GoogleMaps avec les informations de la prochaine étape.
* Lire un parcours : Approcher le téléphone d'un tag NFC afin de valider l'étape et avoir les informations de l'étape suivante (un marqueur de couleur rouge indiquant la position du tag suivant s'ajoute alors à la carte).
* L'application se lance automatiquement à la lecture d'un tag NFC écrit avec l'application Parcours NFC.
* On marque la position de chaque étape validée sur la carte.

## Contraintes
* Utilisation de l'API GoogleMaps.
* Utilisation des GooglePlay Services (Emulateur nécessitant cette option).
* Utilisation de RoomDataBase pour la persistance en base de données.
* Utilisation de tags NFC en lecture et écriture.

@MBDS 2021 Grace Hopper
