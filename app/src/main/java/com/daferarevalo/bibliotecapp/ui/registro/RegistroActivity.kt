package com.daferarevalo.bibliotecapp.ui.registro

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daferarevalo.bibliotecapp.databinding.ActivityRegistroBinding
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegistroBinding
    private val REQUEST_IMAGE_CAPTURE = 1

    companion object {
        private val TAG = RegistroActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.registroImageView.setOnClickListener {
            cargarImagen()
        }

        binding.registrarRegistroButton.setOnClickListener {
            val nombre = binding.nombreRegistroEditText.text.toString()
            val correo = binding.correoRegistroEditText.text.toString()
            val contrasena = binding.contrasenaRegistroEditText.text.toString()
            val repcontrasena = binding.repcontrasenaRegistroEditText.text.toString()

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show()
            } else if (contrasena != repcontrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                registroEnFirebase(correo, contrasena, nombre)
            }
        }
    }

    private fun saveImage(correo: String, nombre: String, uid: String?) {
        val storage = FirebaseStorage.getInstance()
        val photoRef: StorageReference = storage.reference.child("usuarios").child(uid.toString())

        binding.registroImageView.isDrawingCacheEnabled = true
        binding.registroImageView.buildDrawingCache()
        val bitmap: Bitmap = (binding.registroImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()

        val uploadTask: UploadTask = photoRef.putBytes(data)

        val urlTask: Task<Uri> =
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation photoRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri: Uri? = task.result
                    crearUsuarioEnBaseDatos(uid, nombre, correo, downloadUri.toString())
                    //saveUser(downloadUri)
                } else {

                }
            }
    }

    private fun cargarImagen() {
        //val opciones = arrayListOf<String>("Tomar foto","Cargar imagen","Cancelar")
        val alertOpciones = AlertDialog.Builder(this)
        alertOpciones.setTitle("Seleccione un opción")
        alertOpciones.setPositiveButton("Tomar foto") { dialogInterface: DialogInterface, i: Int ->
            dispatchTakePictureIntent()
        }
        alertOpciones.setNegativeButton("Cargar imagen") { dialogInterface: DialogInterface, i: Int ->
            /* val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
             intent.setType("image/")
             band = true
             startActivityForResult(intent,10)*/
            Toast.makeText(this, "cargar foto", Toast.LENGTH_SHORT).show()
        }
        alertOpciones.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*if (band==true){
                val path : Uri? = data?.getData()
                binding.perfilImage.setImageURI(path)
            } else {
                val imageBitmap:Bitmap= data?.extras?.get("data") as Bitmap
                binding.perfilImage.setImageBitmap(imageBitmap)
            }*/
            val imageBitmap: Bitmap = data?.extras?.get("data") as Bitmap
            binding.registroImageView.setImageBitmap(imageBitmap)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun registroEnFirebase(correo: String, contrasena: String, nombre: String) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val uid = auth.currentUser?.uid
                    saveImage(correo, nombre, uid)
                    //val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun crearUsuarioEnBaseDatos(
        uid: String?,
        nombre: String,
        correo: String,
        urlFoto: String,

        ) {
        val database = FirebaseDatabase.getInstance()
        val myUsersReference = database.getReference("usuarios")

        val usuario = Usuario(uid, nombre, correo, urlFoto)
        uid?.let { myUsersReference.child(uid).setValue(usuario) }

        goToLoginActivity()
    }

    private fun goToLoginActivity() {
        onBackPressed()
    }


}