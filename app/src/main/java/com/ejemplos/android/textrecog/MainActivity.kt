package com.ejemplos.android.textrecog

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var imageBitmap: Bitmap?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snapBtn.setOnClickListener(View.OnClickListener {
            dispatchTakePictureIntent()
        })

        detectBtn.setOnClickListener(View.OnClickListener {
            detectTxt()
        })
    }

    val REQUEST_IMAGECAPTURE=1

    private fun dispatchTakePictureIntent(){
        val takePictureIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager)!=null){
            startActivityForResult(takePictureIntent,REQUEST_IMAGECAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== REQUEST_IMAGECAPTURE && resultCode== Activity.RESULT_OK){
            val extras= data!!.extras
            imageBitmap= extras!!.get("data") as Bitmap
            imageView!!.setImageBitmap(imageBitmap)
        }
    }

    private fun detectTxt(){
        val image= FirebaseVisionImage.fromBitmap(imageBitmap!!)
        val detector= FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image).addOnSuccessListener(OnSuccessListener<FirebaseVisionText> {
            firebaseVisionText ->
            processTxt(firebaseVisionText)
        }).addOnFailureListener(
            OnFailureListener {
                showToast("Vaya! Algo salió mal :/")
            }
        )
    }

    private fun processTxt(text: FirebaseVisionText){
        val blocks= text.textBlocks
        if (blocks.size==0){
            Toast.makeText(this,"No se detectó un texto",Toast.LENGTH_LONG).show()
            return
        }
        for (block in text.textBlocks){
            val txt= block.getText()
            txtView!!.textSize=16f
            txtView!!.setText(txt)
        }
    }

    private fun showToast(message: String){
        Toast.makeText(baseContext,message,Toast.LENGTH_SHORT).show()
    }
}