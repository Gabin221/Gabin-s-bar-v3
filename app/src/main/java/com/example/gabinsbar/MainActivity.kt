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
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var verticalRecyclerView: RecyclerView
    private lateinit var queue: RequestQueue

    private lateinit var AccountFab: FloatingActionButton
    private lateinit var CartFab: FloatingActionButton

    private val categoryMap = mutableMapOf<String, Category>()

    private val siropsUrl = "https://gabinserrurot.fr/Api_gabinsbar/recupererSirops.php"
    private val classiquesUrl = "https://gabinserrurot.fr/Api_gabinsbar/recupererClassiques.php"
    private val extravagantsUrl = "https://gabinserrurot.fr/Api_gabinsbar/recupererExtravagants.php"
    private val bieresUrl = "https://gabinserrurot.fr/Api_gabinsbar/recupererBieres.php"
    private val cafesUrl = "https://gabinserrurot.fr/Api_gabinsbar/recupererCafes.php"
    private val thesUrl = "https://gabinserrurot.fr/Api_gabinsbar/recupererThes.php"
    private val softsUrl = "https://gabinserrurot.fr/Api_gabinsbar/recupererSofts.php"

    private val categoryOrder = listOf("Suggestions", "Sirops", "Bières", "Classiques", "Extravagants", "Softs", "Cafés", "Thés")

    private var loadedCount = 0
    private val totalCategories = 6
    private lateinit var pseudo_utilisateur: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verticalRecyclerView = findViewById(R.id.verticalRecyclerView)
        verticalRecyclerView.layoutManager = LinearLayoutManager(this)
        AccountFab = findViewById(R.id.AccountFab)
        CartFab = findViewById(R.id.CartFab)
        pseudo_utilisateur = findViewById(R.id.pseudo_utilisateur)


        if (SessionManager.pseudo != "") {
            pseudo_utilisateur.text = SessionManager.pseudo
        }

        loadCategory(siropsUrl, "Sirops", "kcal")
        loadCategory(classiquesUrl, "Classiques", "%")
        loadCategory(extravagantsUrl, "Extravagants", "%")
        loadCategory(bieresUrl, "Bières", "%")
        loadCategory(cafesUrl, "Cafés", "")
        loadCategory(thesUrl, "Thés", "")
        loadCategory(softsUrl, "Softs", "kcal")

        CartFab.setOnClickListener {
            val items = PanierManager.getPanier()
            if (items.isEmpty()) {
                Toast.makeText(this, "Panier vide", Toast.LENGTH_SHORT).show()
            } else {
                val message = items.joinToString(separator = "\n - ") { it.name }
                AlertDialog.Builder(this)
                    .setTitle("Panier")
                    .setMessage(message)
                    .setPositiveButton("Commander") { _, _ ->
                        envoyerCommande()
                    }
                    .setNegativeButton("Fermer", null)
                    .show()
            }
        }

        AccountFab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
            val pseudoEditText = view.findViewById<EditText>(R.id.pseudoEditText)
            val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)

            val pseudo = pseudoEditText.text.toString()
            val password = passwordEditText.text.toString()

            builder.setView(view)
                .setTitle("Connexion")
                .setPositiveButton("Se connecter") { _, _ ->
                    if (pseudo.isNotEmpty() && password.isNotEmpty()) {
                        //val hashedPassword = hashSHA256(password)
                        val hashedPassword = password

                        sendLoginRequest(pseudo, hashedPassword)
                    } else {
                        Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Annuler") { _, _ ->
                    Toast.makeText(this, "Annulation de la connexion.", Toast.LENGTH_SHORT).show()
                }
                builder.setNeutralButton("Se déconnecter") { _, _ ->
                    Toast.makeText(this, "Déconnexion effectuée", Toast.LENGTH_SHORT).show()
                    // Code pour déconnecter l'utilisateur
                    pseudo_utilisateur.text = "Vous n'êtes pas connecté."
                    SessionManager.isLoggedIn = false
                    SessionManager.pseudo = ""
                }

            val dialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(ContextCompat.getColor(this, android.R.color.system_surface_dark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
        }
    }

    private fun loadCategory(url: String, title: String, suffix: String, retryCount: Int = 0) {
        if (!::queue.isInitialized) {
            queue = Volley.newRequestQueue(this)
        }

        val request = StringRequest(Request.Method.GET, url, { response ->
            val elements = parseElements(response, suffix)
            categoryMap[title] = Category(title, elements)
            loadedCount++
            maybeUpdateRecycler()
        }, { error ->
            if (retryCount < 2) {
                Handler(Looper.getMainLooper()).postDelayed({
                    loadCategory(url, title, suffix, retryCount + 1)
                }, 500)
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
            // Générer les suggestions dans le même ordre que categoryOrder
            val suggestions = categoryOrder
                .filter { it != "Suggestions" }
                .mapNotNull { categoryMap[it]?.elements?.randomOrNull() }

            categoryMap["Suggestions"] = Category("Suggestions", suggestions)

            // Construire la liste finale dans le bon ordre
            val orderedCategories = categoryOrder.mapNotNull { categoryMap[it] }

            verticalRecyclerView.adapter = MasterCategoryAdapter(orderedCategories) { element ->
                PanierManager.ajouter(element)
                Toast.makeText(this, "${element.name} ajouté au panier", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun sendLoginRequest(pseudo: String, hashedPassword: String) {
        val url = "https://gabinserrurot.fr/Api_gabinsbar/connexionUtilisateur.php"
        val queue = Volley.newRequestQueue(this)

        val urlWithParams = "$url?pseudo=$pseudo&mot_de_passe=$hashedPassword"

        val stringRequest = StringRequest(
            Request.Method.GET, urlWithParams,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    val message = jsonResponse.getString("message")

                    if (status == "success") {
                        Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show()
                        SessionManager.isLoggedIn = true
                        SessionManager.pseudo = pseudo

                        pseudo_utilisateur.text = "Bienvenue ${SessionManager.pseudo} !"
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Erreur de format de réponse.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Erreur: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    private fun envoyerCommande() {
        if (SessionManager.isLoggedIn) {
            try {
                val commande = PanierManager.getPanier().joinToString(separator = "\n    - ") { it.name }
                val texte = "${SessionManager.pseudo} aimerait bien:\n    - $commande"
                val texteEncode = URLEncoder.encode(texte, "UTF-8")
                val url = "https://api.telegram.org/bot7120496261:AAEjIPlWcFiWfTMuacnPJRVMmtklvZEe0YM/sendMessage?chat_id=6718593235&text=${texteEncode}"

                val params = JSONObject()

                val request = JsonObjectRequest(
                    Request.Method.POST, url, params,
                    { response ->
                        response.toString()
                    },
                    { error ->
                        error.message
                    }
                )

                Volley.newRequestQueue(this).add(request)

                val message = "La commande a bien été envoyée"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this, "Erreur lors de l'envoi de la commande: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Vous devez vous connecter pour passer commande", Toast.LENGTH_SHORT).show()
        }
    }

}
