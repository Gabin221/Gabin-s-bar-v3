package com.example.gabinsbar

data class Category(
    val title: String,
    val elements: List<Element>
)

data class Element(
    val name: String,
    val imageUrl: String,
    val extraInfo: String
)

