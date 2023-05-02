package com.example.chatingappver2.UI.Fragment.ViewProfile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.ShowFullScreenImg.ShowImgFullScreenActivity
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_view_profile.*
import kotlinx.android.synthetic.main.fragment_view_profile.view.*

class FragmentViewProfile : Fragment() {
    private lateinit var dialog: Dialog
    private var urlImg: String? = null

    public override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_view_profile, viewGroup, false)
        dialog = progressDialog.progressDialog(requireContext())
        dialog!!.show()

        information()
        registerEventCliclListener(view)

        dialog!!.dismiss()
        return view
    }

    private fun registerEventCliclListener(view: View) {
        view.imtAvataProfile.setOnClickListener {
            val intent = Intent(requireContext(), ShowImgFullScreenActivity::class.java)
            intent.putExtra("urlImg", urlImg)
            requireContext().startActivity(intent)
        }
    }

    private fun information() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.reference.child("profile").child(currentUser!!.uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userProfile: UserProfile? = task.result.getValue(UserProfile::class.java)
                    urlImg = userProfile?.urlImgProfile
                    Glide.with(requireContext()).asBitmap().load(userProfile?.urlImgProfile)
                        .into(imtAvataProfile)
                    InforFullName.setText(userProfile?.fullname)
                    InforEmail.setText(userProfile?.email)
                    InforBirthDay.setText(userProfile?.dateOfBirth)
                }
            }
    }


}