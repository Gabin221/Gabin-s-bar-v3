package com.example.gabinsbar

object PanierManager {
    private val panier = mutableListOf<Element>()

    fun supprimer(element: Element) {
        panier.remove(element)
    }

    fun vider() {
        panier.clear()
    }

    fun getPanier(): List<Element> = panier.toList()

    fun ajouter(element: Element): Boolean {
        return if (panier.any { it.name == element.name }) {
            false // l'élément existe déjà, on ne l'ajoute pas
        } else {
            panier.add(element)
            true // élément ajouté avec succès
        }
    }

}
