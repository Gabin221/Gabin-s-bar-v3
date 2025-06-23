# Gabin's Bar v3

🍹 Gabin's Bar v3 est une application Android connectée à un backend PHP/MySQL. Elle permet de gérer un bar : prise de commandes, gestion du panier et communication avec un bot Telegram.

## 🚀 Fonctionnalités principales

    Catalogue interactif : boissons par catégories avec scroll horizontal

    Panier dynamique : ajout, suppression, vidage, validation de commande

    Connexion / Déconnexion / Création de compte

    AlertDialogs stylisées

    Envoi des commandes via un bot Telegram

    API PHP pour la récupération des données et la gestion des utilisateurs

## 🛠️ Technologies

    Kotlin (Android)

    RecyclerView, Material Design

    Volley pour les appels API

    PHP + MySQL

    Telegram Bot API

## 📂 Structure générale
Côté Android

    app/src/main/ : code Kotlin, layouts XML, ressources

    app/build.gradle : configuration de l'application

Côté API

    Api_gabinsbar/connexionUtilisateur.php : connexion utilisateur

    Api_gabinsbar/creer_compte.php : création de compte

    Api_gabinsbar/recuperer*.php : endpoints pour les catégories de boissons

## ⚙️ Installation
Android

1️⃣ Clone le projet :
```bash
git clone https://github.com/ton-pseudo/Gabin-s-bar-v3.git
```

2️⃣ Ouvre dans Android Studio

3️⃣ Build & Run sur appareil ou émulateur
API

1️⃣ Déploie Api_gabinsbar/ sur ton serveur web compatible PHP

2️⃣ Configure param2.inc.php pour l’accès MySQL

3️⃣ Vérifie le bon fonctionnement via un navigateur ou Postman
🌐 Exemple d'API

    GET /Api_gabinsbar/recupererBieres.php

    POST /Api_gabinsbar/connexionUtilisateur.php (pseudo, password)

    POST /Api_gabinsbar/creer_compte.php (pseudo, password)

## 💡 Sécurité

    ⚠️ Prototype : à sécuriser avant toute utilisation réelle

        Active HTTPS

        Sécurise les entrées (requêtes préparées)

        Mets en place une authentification robuste

## 👤 Auteur

Gabin Serrurot  
Projet personnel pour l'apprentissage et la démonstration technique.  
📃 Licence

Open source, librement modifiable et réutilisable.
