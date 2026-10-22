package com.example.app_sin_ideas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "http://184.195.231.68:5000/api"
    private val cliente = OkHttpClient()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGetInfo = findViewById<Button>(R.id.btnGetInfo)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)

        // Petición GET
        btnGetInfo.setOnClickListener {
            val request = Request.Builder()
                .url("$BASE_URL/usuario")
                .build()

            cliente.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    actualizarUI(tvResultado, "Error de conexión: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val respuesta = response.body?.string()
                    actualizarUI(tvResultado, "Datos recibidos (GET):\n$respuesta")
                }
            })
        }

        // Petición POST
        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()
            val jsonMap = mapOf("login" to usuario, "password" to password)
            val jsonString = gson.toJson(jsonMap)
            val body = jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url("$BASE_URL/login")
                .post(body)
                .build()

            cliente.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    actualizarUI(tvResultado, "Error de conexión: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val respuestaString = response.body?.string()

                    if (respuestaString != null) {
                        try {
                            val jsonObject = org.json.JSONObject(respuestaString)

                            // Extraemos solo los valores limpios
                            val estado = jsonObject.getString("estado").uppercase()
                            val mensaje = jsonObject.getString("mensaje")

                            actualizarUI(tvResultado, "Resultado: $estado\n$mensaje")
                        } catch (e: Exception) {
                            actualizarUI(tvResultado, "Error al leer JSON: ${e.message}")
                        }
                    }
                }
            })
        }
    }


    private fun actualizarUI(textView: TextView, mensaje: String) {
        runOnUiThread {
            textView.text = mensaje
        }
    }
}