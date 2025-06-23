package com.example.gabinsbar

import SessionManager
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gabinsbar.PanierManager.vider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.security.MessageDigest

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

        val accountFab: View = findViewById(R.id.AccountFab)
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_pulse)
        accountFab.startAnimation(pulseAnimation)

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
            val items = PanierManager.getPanier().toMutableList()

            if (items.isEmpty()) {
                Toast.makeText(this, "Panier vide", Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(this)
                val view = LayoutInflater.from(this).inflate(R.layout.dialog_panier, null)
                val listView = view.findViewById<ListView>(R.id.panierListView)
                val btnCommander = view.findViewById<TextView>(R.id.dialogCommanderBtn)
                val btnVider = view.findViewById<TextView>(R.id.dialogViderBtn)

                val itemNames = items.map { it.name }.toMutableList()
                val adapter = ArrayAdapter(this, R.layout.list_item, R.id.text_view, itemNames)
                listView.adapter = adapter

                builder.setView(view)

                val dialog = builder.create()
                dialog.show()

                listView.setOnItemClickListener { _, _, position, _ ->
                    val elementSupprime = items[position]
                    PanierManager.supprimer(elementSupprime)
                    items.removeAt(position)
                    itemNames.removeAt(position)
                    adapter.notifyDataSetChanged()

                    Toast.makeText(this, "${elementSupprime.name} supprimé du panier", Toast.LENGTH_SHORT).show()

                    if (items.isEmpty()) {
                        dialog.dismiss()
                        Toast.makeText(this, "Panier vidé", Toast.LENGTH_SHORT).show()
                    }
                }
                btnCommander.setOnClickListener {
                    envoyerCommande()
                    vider()
                    dialog.dismiss()
                }
                btnVider.setOnClickListener {
                    vider()
                    dialog.dismiss()
                }
            }
        }

        AccountFab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
            val pseudoEditText = view.findViewById<EditText>(R.id.pseudoEditText)
            val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
            val dialogLoginBtn = view.findViewById<TextView>(R.id.dialogLoginBtn)
            val dialogCancelBtn = view.findViewById<TextView>(R.id.dialogCancelBtn)
            val dialogDisconnectBtn = view.findViewById<TextView>(R.id.dialogDisconnectBtn)
            val dialogCreateAccountBtn = view.findViewById<TextView>(R.id.dialogCreateAccountBtn)

            builder.setView(view)

            val dialog = builder.create()
            dialog.show()

            dialogLoginBtn.setOnClickListener {
                val pseudo = pseudoEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (SessionManager.isLoggedIn) {
                    Toast.makeText(this, "Vous êtes déjà connecté.", Toast.LENGTH_SHORT).show()
                } else {
                    if (pseudo.isNotEmpty() && password.isNotEmpty()) {
                        val hashedPassword = hashSHA256(password)
                        sendLoginRequest(pseudo, hashedPassword)
                        accountFab.clearAnimation()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialogCancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialogDisconnectBtn.setOnClickListener {
                if(SessionManager.isLoggedIn){
                    Toast.makeText(this, "Déconnexion effectuée", Toast.LENGTH_SHORT).show()
                    pseudo_utilisateur.text = ""
                    SessionManager.isLoggedIn = false
                    SessionManager.pseudo = ""
                    accountFab.startAnimation(pulseAnimation)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Vous n'étiez pas connecté.", Toast.LENGTH_SHORT).show()
                }
            }
            dialogCreateAccountBtn.setOnClickListener {
                val pseudo = pseudoEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (pseudo.isNotEmpty() && password.isNotEmpty()) {
                    val hashedPassword = hashSHA256(password)
                    sendCreateAccountRequest(pseudo, hashedPassword)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sendCreateAccountRequest(pseudo: String, hashedPassword: String) {
        val url = "https://gabinserrurot.fr/Api_gabinsbar/creer_compte.php"
        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
                Toast.makeText(this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["pseudo"] = pseudo
                params["password"] = hashedPassword
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }


    private fun hashSHA256(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
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
                val success = PanierManager.ajouter(element)
                if (success) {
                    Toast.makeText(this, "${element.name} ajouté au panier", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "${element.name} est déjà dans le panier", Toast.LENGTH_SHORT).show()
                }

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
                val url = "use/your/api&text=${texteEncode}"

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
