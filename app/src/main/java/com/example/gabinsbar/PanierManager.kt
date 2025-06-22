package com.example.gabinsbar

object PanierManager {
    private val panier = mutableListOf<Element>()

    fun ajouter(element: Element) {
        panier.add(element)
    }

    fun supprimer(element: Element) {
        panier.remove(element)
    }

    fun vider() {
        panier.clear()
    }

    fun getPanier(): List<Element> = panier.toList()
}
