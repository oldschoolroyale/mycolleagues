package com.brm.mycolleagues.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.brm.mycolleagues.R
import com.brm.mycolleagues.ui.activity.vm.AccountViewModel
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_account.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class AccountActivity : AppCompatActivity() {
    private val accountViewModel by viewModels<AccountViewModel>()

    private val uploadObserver = Observer<BaseModel<String>>{
        when(it.status){
            Status.LOADING ->{
                showMessage("loading...")
            }
            Status.SUCCESS ->{
                showMessage("success...")
            }
            Status.ERROR ->{
                showMessage("error..." + it.response?.error_text)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        fab.setOnClickListener {
            val intent1 = Intent()
            intent1.type = "image/*"
            intent1.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent1, "SELECT_IMAGE"), GALLERY_PICK)
        }

        accountViewModel.upload_status.observe(this, uploadObserver)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK) {

            val imageUri: Uri? = data!!.data


            CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(this)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                val resultFile = File(resultUri.path!!)
                val filePart = MultipartBody.Part.createFormData("file", "oldschool.jpg", RequestBody.create("image/*".toMediaTypeOrNull(),
                resultFile))
                accountViewModel.upload(filePart)
                showMessage("sending...")

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage(result.error.toString())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
    private fun showMessage(message: String){
        Log.d("oldschool", message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    companion object{
        private const val GALLERY_PICK = 1
    }
}