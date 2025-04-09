package com.example.violetaapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.violetaapp.R
import com.example.violetaapp.adapter.CommentAdapter
import com.example.violetaapp.data.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PlaceDetailActivity : Activity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var commentList: MutableList<Comment>
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var userId: String
    private lateinit var placeId: String
    private lateinit var placeName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Dados recebidos
        placeName = intent.getStringExtra("name") ?: "Sem nome"
        placeId = intent.getStringExtra("id") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val rating = intent.getDoubleExtra("rating", 0.0)

        val horario = listOf(
            "Domingo: ${intent.getStringExtra("sunday") ?: "-"}",
            "Segunda: ${intent.getStringExtra("monday") ?: "-"}",
            "Terça: ${intent.getStringExtra("tuesday") ?: "-"}",
            "Quarta: ${intent.getStringExtra("wednesday") ?: "-"}",
            "Quinta: ${intent.getStringExtra("thursday") ?: "-"}",
            "Sexta: ${intent.getStringExtra("friday") ?: "-"}",
            "Sábado: ${intent.getStringExtra("saturday") ?: "-"}"
        ).joinToString("\n")

        // Views
        findViewById<TextView>(R.id.placeName).text = placeName
        findViewById<TextView>(R.id.placeTipo).text = tipo
        findViewById<TextView>(R.id.placeAddress).text = address
        findViewById<TextView>(R.id.placeRating).text = "⭐ $rating"
        findViewById<TextView>(R.id.placeHorario).text = horario

        val commentInput = findViewById<EditText>(R.id.editComment)
        val btnComment = findViewById<Button>(R.id.btnComment)
        val btnFavorite = findViewById<ImageView>(R.id.btnFavorite)
        val recyclerComments = findViewById<RecyclerView>(R.id.recyclerComments)

        // Comentários
        commentList = mutableListOf()
        commentAdapter = CommentAdapter(commentList)
        recyclerComments.layoutManager = LinearLayoutManager(this)
        recyclerComments.adapter = commentAdapter

        loadComments()

        btnComment.setOnClickListener {
            val text = commentInput.text.toString().trim()
            if (text.isNotEmpty()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userName = currentUser?.displayName ?: "Anônimo"

                val data = hashMapOf(
                    "userId" to userId,
                    "userName" to userName,
                    "placeId" to placeId,
                    "placeName" to placeName,
                    "comment" to text,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                db.collection("comments").add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Comentário enviado!", Toast.LENGTH_SHORT).show()
                        commentInput.text.clear()
                        loadComments()
                    }
            }
        }

        btnFavorite.setOnClickListener {
            val docId = "${userId}_$placeId"
            val favoriteRef = db.collection("favorites").document(docId)

            favoriteRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Já é favorito → remover
                    favoriteRef.delete().addOnSuccessListener {
                        Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Ainda não é favorito → adicionar
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userName = currentUser?.displayName ?: "Usuário"

                    val data = hashMapOf(
                        "userId" to userId,
                        "userName" to userName,
                        "placeId" to placeId,
                        "placeName" to placeName,
                        "tipo" to tipo,
                        "address" to address,
                        "rating" to rating,
                        "sunday" to intent.getStringExtra("sunday"),
                        "monday" to intent.getStringExtra("monday"),
                        "tuesday" to intent.getStringExtra("tuesday"),
                        "wednesday" to intent.getStringExtra("wednesday"),
                        "thursday" to intent.getStringExtra("thursday"),
                        "friday" to intent.getStringExtra("friday"),
                        "saturday" to intent.getStringExtra("saturday")
                    )

                    favoriteRef.set(data).addOnSuccessListener {
                        Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadComments() {
        db.collection("comments")
            .whereEqualTo("placeId", placeId)
            .get()
            .addOnSuccessListener { result ->
                commentList.clear()
                for (doc in result) {
                    val userName = doc.getString("userName") ?: "Usuário"
                    val comment = doc.getString("comment") ?: ""
                    val timestamp = doc.getTimestamp("timestamp")?.toDate()
                    val formattedTime = timestamp?.let {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                    } ?: ""
                    commentList.add(Comment(userName, comment, formattedTime))
                }
                commentAdapter.notifyDataSetChanged()
            }
    }

}