package com.example.ejerciciogps

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ejerciciogps.Constantes.KEY_NAME
import com.example.ejerciciogps.databinding.ActivityMainBinding
import com.example.ejerciciogps.databinding.ActivityPersistenciaBinding

class PersistenciaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersistenciaBinding

    //variables de tipo sharedpreferences y editor
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPersistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //inicializar sharedPreferences
        initializeSharedPreferences()
        loadData()
        binding.btnGuardar.setOnClickListener {
            savePersistenciaData()
        }
    }

    private fun initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("Persistencia", MODE_PRIVATE)
        //abrir archivo editable
        editor = sharedPreferences.edit()
    }

    private fun savePersistenciaData() {

      val nombreCompleto = binding.EtNombreCompleto.text.toString()

        editor.apply{
            putString(KEY_NAME, nombreCompleto)
        }.apply()
    }
    private fun loadData(){
        binding.txtNombre.text=
            sharedPreferences.getString(KEY_NAME, "VACIO")
    }
}