# Gabin's Bar v3

🍹 **Gabin's Bar v3** est une application Android associée à un backend PHP/MySQL, développée pour faciliter la prise de commande dans un bar.  
Elle offre une interface intuitive et un système de panier connecté à un serveur.

---

## 🚀 Fonctionnalités principales

- **Catalogue interactif** : boissons affichées par catégories avec scroll horizontal
- **Panier dynamique** : ajout, suppression individuelle, vidage, validation de commande
- **Connexion / Déconnexion / Création de compte**
- **AlertDialogs stylisées pour un rendu harmonisé**
- **Envoi des commandes via un bot Telegram**
- **Backend PHP : récupération des données et gestion des utilisateurs**

---

## 🛠️ Technologies

- **Kotlin** (Android)
- **RecyclerView** avec scroll horizontal et vertical
- **Material Design** (FloatingActionButton, AlertDialog, etc.)
- **Volley** (communication avec l'API)
- **PHP + MySQL** (API simple pour récupérer les données et gérer les utilisateurs)
- **Telegram Bot API** (envoi des commandes)

---

## 📂 Structure générale

### Côté **Android**
- `app/src/main/` : code Kotlin, layouts XML, ressources (icônes, couleurs, styles)
- `app/build.gradle` : configuration de l'application

### Côté **API**
- `Api_gabinsbar/connexionUtilisateur.php` : vérification des identifiants utilisateur
- `Api_gabinsbar/creer_compte.php` : création d'un nouveau compte
- `Api_gabinsbar/recuperer*.php` : points d'API pour récupérer les différentes catégories de boissons (bières, cafés, sirops, softs, etc.)

---

## ⚙️ Installation

### Android

1️⃣ Clone le projet :  
```bash
git clone https://github.com/ton-pseudo/Gabin-s-bar-v3.git
