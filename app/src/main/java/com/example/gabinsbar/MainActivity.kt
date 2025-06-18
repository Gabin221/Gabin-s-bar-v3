package com.example.gabinsbar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var verticalRecyclerView: RecyclerView
    private lateinit var queue: RequestQueue

    private val categoryMap = mutableMapOf<String, Category>()

    private val siropsUrl = "use/your/api/Api_gabinsbar/recupererSirops.php"
    private val classiquesUrl = "use/your/api/Api_gabinsbar/recupererClassiques.php"
    private val extravagantsUrl = "use/your/api/Api_gabinsbar/recupererExtravagants.php"
    private val bieresUrl = "use/your/api/Api_gabinsbar/recupererBieres.php"
    private val cafesUrl = "use/your/api/Api_gabinsbar/recupererCafes.php"
    private val thesUrl = "use/your/api/Api_gabinsbar/recupererThes.php"
    private val softsUrl = "use/your/api/Api_gabinsbar/recupererSofts.php"

    private val categoryOrder = listOf("Suggestions", "Sirops", "Bières", "Classiques", "Extravagants", "Softs", "Cafés", "Thés")

    private var loadedCount = 0
    private val totalCategories = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verticalRecyclerView = findViewById(R.id.verticalRecyclerView)
        verticalRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCategory(siropsUrl, "Sirops", "kcal")
        loadCategory(classiquesUrl, "Classiques", "%")
        loadCategory(extravagantsUrl, "Extravagants", "%")
        loadCategory(bieresUrl, "Bières", "%")
        loadCategory(cafesUrl, "Cafés", "")
        loadCategory(thesUrl, "Thés", "")
        loadCategory(softsUrl, "Softs", "kcal")
    }

    private fun loadCategory(url: String, title: String, suffix: String, retryCount: Int = 0) {
        if (!::queue.isInitialized) {
            queue = Volley.newRequestQueue(this)
        }

        val request = StringRequest(Request.Method.GET, url, { response ->
            println("DEBUG loadCategory $title response: $response")
            val elements = parseElements(response, suffix)
            println("DEBUG $title elements size: ${elements.size}")
            categoryMap[title] = Category(title, elements)
            loadedCount++
            maybeUpdateRecycler()
        }, { error ->
            if (retryCount < 2) {
                println("DEBUG Erreur $title, retry $retryCount: ${error.message}")
                Handler(Looper.getMainLooper()).postDelayed({
                    loadCategory(url, title, suffix, retryCount + 1)
                }, 500)  // délai de 500 ms avant de relancer
            } else {
                Toast.makeText(this, "Erreur $title : ${error.message}", Toast.LENGTH_LONG).show()
                loadedCount++
                maybeUpdateRecycler()
            }
        })

        queue.add(request)
    }

    private fun parseElements(response: String, infoSuffix: String): List<Element> {
        return response.split("|").mapNotNull { itemStr ->
            val parts = itemStr.split(";")
            if (parts.isNotEmpty()) {
                Element(
                    name = parts.getOrNull(0) ?: "Inconnu",
                    imageUrl = parts.getOrNull(1) ?: "",
                    extraInfo = "${parts.getOrNull(2) ?: ""} $infoSuffix"
                )
            } else null
        }
    }

    fun refreshSuggestions() {
        val suggestions = categoryOrder
            .filter { it != "Suggestions" }
            .mapNotNull { categoryMap[it]?.elements?.randomOrNull() }

        categoryMap["Suggestions"] = Category("Suggestions", suggestions)

        val orderedCategories = categoryOrder.mapNotNull { categoryMap[it] }
        (verticalRecyclerView.adapter as? MasterCategoryAdapter)?.updateCategories(orderedCategories)
    }

    private fun maybeUpdateRecycler() {
        if (loadedCount == totalCategories) {
            categoryMap.forEach { (title, category) ->
                println("DEBUG Category: $title - elements size: ${category.elements.size}")
            }
            // Générer les suggestions dans le même ordre que categoryOrder
            val suggestions = categoryOrder
                .filter { it != "Suggestions" }
                .mapNotNull { categoryMap[it]?.elements?.randomOrNull() }

            categoryMap["Suggestions"] = Category("Suggestions", suggestions)

            // Construire la liste finale dans le bon ordre
            val orderedCategories = categoryOrder.mapNotNull { categoryMap[it] }

            verticalRecyclerView.adapter = MasterCategoryAdapter(orderedCategories)
        }
    }
}
