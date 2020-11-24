package com.daferarevalo.bibliotecapp.ui.miperfil

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.databinding.FragmentInformacionPersonalBinding
import com.daferarevalo.bibliotecapp.server.Usuario
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream


class InformacionPersonalFragment : Fragment() {
    private lateinit var binding: FragmentInformacionPersonalBinding

    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacion_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInformacionPersonalBinding.bind(view)
        val user = FirebaseAuth.getInstance().currentUser

        binding.perfilImageView.setOnClickListener {
            cargarImagen()
        }

        user?.let {
            val uidUsuario = user.uid
            buscarEnFirebase(uidUsuario)
        }

        binding.actualizarPerfilButton.setOnClickListener {

            val nuevoNombre = binding.nombrePerfilEditText.text.toString()
            val nuevoCorreo = binding.correoPerfilEditText.text.toString()
            val nuevaContrasena = binding.contrasenaPerfilEditText.text.toString()

            user?.let {
                val uidUsuario = user.uid
                actualizarCorreoFirebase(user, nuevoCorreo)
                actualizarContrasenaFirebase(nuevaContrasena, user)
                actualizarDatabaseFirebase(nuevoNombre, nuevoCorreo, uidUsuario)
                //actualizarDatabaseFirebase(nuevoNombre,nuevoCorreo)
                saveImage(uidUsuario)
            }
        }
    }

    private fun saveImage(uidUsuario: String) {
        val storage = FirebaseStorage.getInstance()
        val photoRef: StorageReference = storage.reference.child("usuarios")

        binding.perfilImageView.isDrawingCacheEnabled = true
        binding.perfilImageView.buildDrawingCache()
        val bitmap: Bitmap = (binding.perfilImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
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
                    saveUser(downloadUri, uidUsuario)
                } else {

                }
            }
    }

    private fun saveUser(downloadUri: Uri?, uidUsuario: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("usuarios").child(uidUsuario)

        uidUsuario.let { myRef.child(uidUsuario).child("foto").setValue(downloadUri) }
    }


    private fun cargarImagen() {

        //val opciones = arrayListOf<String>("Tomar foto","Cargar imagen","Cancelar")
        val alertOpciones = AlertDialog.Builder(context)
        alertOpciones.setTitle("Seleccione un opción")
        alertOpciones.setPositiveButton("Tomar foto") { dialogInterface: DialogInterface, i: Int ->
            dispatchTakePictureIntent()
        }
        alertOpciones.setNegativeButton("Cargar imagen") { dialogInterface: DialogInterface, i: Int ->
            /* val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
             intent.setType("image/")
             band = true
             startActivityForResult(intent,10)*/
            Toast.makeText(context, "cargar foto", Toast.LENGTH_SHORT).show()
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
            binding.perfilImageView.setImageBitmap(imageBitmap)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun actualizarDatabaseFirebase(
        nuevoNombre: String,
        nuevoCorreo: String,
        uidUsuario: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val myUsuarioRef = database.getReference("usuarios")
        val childUpdates = HashMap<String, Any>()
        childUpdates["nombre"] = nuevoNombre
        childUpdates["correo"] = nuevoCorreo
        uidUsuario.let { myUsuarioRef.child(it).updateChildren(childUpdates) }
        Toast.makeText(context, "DataBase actualizada", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarContrasenaFirebase(
        nuevaContrasena: String,
        user: FirebaseUser?
    ) {
        if (nuevaContrasena.isNotBlank() || nuevaContrasena.isNotEmpty()) {

            user!!.updatePassword(nuevaContrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Contraseña Actualizada", Toast.LENGTH_SHORT).show()
                        //Log.d(TAG, "User password updated.")
                    }
                }
        }
    }

    private fun actualizarCorreoFirebase(
        user: FirebaseUser?,
        nuevoCorreo: String
    ) {
        user!!.updateEmail(nuevoCorreo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Actualizado", Toast.LENGTH_SHORT).show()
                    //Log.d(TAG, "User email address updated.")
                }
            }
    }


    private fun buscarEnFirebase(uidUsuario: String) {
        val database = FirebaseDatabase.getInstance()
        val myUsuariosRef = database.getReference("usuarios")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    val usuarioServer = data.getValue(Usuario::class.java)
                    if (usuarioServer?.id.equals(uidUsuario)) {
                        binding.nombrePerfilEditText.setText(usuarioServer?.nombre)
                        binding.correoPerfilEditText.setText(usuarioServer?.correo)

                        //correoActual = usuarioServer?.correo

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        myUsuariosRef.addValueEventListener(postListener)
    }
}