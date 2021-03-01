package com.alfanshter.waktani.ui.home

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.alfanshter.waktani.R
import com.alfanshter.waktani.session.SessionManager
import com.alfanshter.waktani.viewmodel.SayurViewModels
import com.alfanshter.waktani.viewmodel.UsersViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_insert.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.IOException

class InsertActivity : AppCompatActivity() {
    private lateinit var sayurViewModels: SayurViewModels
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    lateinit var radiosatuan: RadioButton
    var kategori_produk: String? = null
    var languages = arrayOf("Buah", "Sayuran")
    var statusspinner = arrayOf("Order", "PreOrder")
    val NEW_SPINNER_ID = 1
    var spinnerkategori = ""
    var spinnerstatus = ""
    var image_uri: Uri? = null
    private var myUrl = ""
    private var storageReference: StorageReference? = null

    //kamera
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    //firebaseAuth
    lateinit var auth: FirebaseAuth
    var UserID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)
        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        UserID = auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference.child("Petani").child(sessionManager.getToken().toString()).child("produk")

        progressDialog = ProgressDialog(this)
        sayurViewModels = ViewModelProviders.of(this@InsertActivity).get(SayurViewModels::class.java)
        spinnerkategori()
        spinnerstatus()

        btnUpload.setOnClickListener {
            insertsayur()
        }
        btn_foto.setOnClickListener {
         openCamera()
        }

        btn_galery.setOnClickListener {
            pickImageFromGallery()
        }


    }



    fun insertsayur() {
        val nama_produk = edt_nama.text.toString().trim()
        val deskripsi = edt_deskripsi.text.toString().trim()
        val manfaat = edt_manfaat.text.toString().trim()
        val penyimpanan = edt_penyimpanan.text.toString().trim()
        val harga = edt_harga.text.toString().trim()
        val stok_produk = edt_stok.text.toString().trim()
        val waktu_preorder = edt_waktu.text.toString().trim()

        if (!TextUtils.isEmpty(nama_produk) ||
            !TextUtils.isEmpty(deskripsi) || !TextUtils.isEmpty(manfaat) || !TextUtils.isEmpty(
                penyimpanan
            ) || !TextUtils.isEmpty(harga) || !TextUtils.isEmpty(stok_produk)
        ){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Upload Makanan")
            progressDialog.setMessage("Tunggu , sedang update")
            progressDialog.show()
            val bmp = MediaStore.Images.Media.getBitmap(contentResolver, image_uri)
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val data = baos.toByteArray()
            val fileref =
                storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileref.putBytes(data)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw  it
                        progressDialog.dismiss()
                    }
                }
                return@Continuation fileref.downloadUrl
            }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()

                } else {
                    progressDialog.dismiss()
                    toast("upload gagal")
                }
            })

        }else{
            toast("Kolom ada yang tidak terisi")
        }

    }

    fun spinnerkategori() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, languages
        )
        spinner_kategori.adapter = adapter
        val intSelectkategori: Int = radio_satuan!!.checkedRadioButtonId
        radiosatuan = findViewById(intSelectkategori)
        kategori_produk = radiosatuan.text.toString()

        spinner_kategori.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                spinnerkategori = languages[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

        }
    }

    fun spinnerstatus() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, statusspinner
        )
        spinner_status.adapter = adapter
        val intSelectkategori: Int = radio_satuan!!.checkedRadioButtonId
        radiosatuan = findViewById(intSelectkategori)
        kategori_produk = radiosatuan.text.toString()

        spinner_status.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                spinnerstatus = statusspinner[position]
                if (position == 1) {
                    edt_waktu.visibility = View.VISIBLE
                } else {
                    edt_waktu.visibility = View.INVISIBLE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Picasso.get().load(image_uri).fit().into(gambar_makanan)
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                image_uri = data?.data

                Picasso.get().load(image_uri).fit().into(gambar_makanan)
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 25, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }


}