package com.example.chatingappver2.UI.Fragment.HomeFragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.Model.currentContacts
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Adapter.adapterCurrentUser
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class FragmentHome : Fragment() {
    private val TAG: String = "FragmentHome"
    private var adapterCurrentContact: adapterCurrentUser? = null
    private val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var dialog: Dialog? = null
    private val newListUser: MutableList<currentContacts> = mutableListOf()
    private val oldListUser: MutableList<currentContacts> = mutableListOf()
    private lateinit var viewFragment: View


    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {

        viewFragment = inflater.inflate(R.layout.fragment_home, viewGroup, false)

        dialog = progressDialog.progressDialog(requireContext())

        dialog!!.show()
        hideNavBar()
        registerEvent()
        addAdapterToRecycle()
        notifyOnline()
        initRecycler()
        return viewFragment
    }
    private fun hideNavBar() {
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun notifyOnline() {
        notifyOnlineOnProfile()
        notifyOnlineForEveryone()
    }

    private fun notifyOnlineOnProfile() {
        database.reference.child("profile").child(currentUser.uid)
            .child("theyIsActive").setValue(true)
            .addOnCompleteListener { setOnlineOnProfile ->
                if (setOnlineOnProfile.isSuccessful) {
                    Log.d(TAG, "notifyOnlineOnProfile: Success")
                } else {
                    Log.e(TAG, "notifyOnlineOnProfile: Fail")
                }
            }
    }

    private fun notifyOnlineForEveryone() {
        database.reference.child("currentContacts").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (dataSnapshot in task.result.children) {
                        if (dataSnapshot.key != currentUser.uid) {
                            database.reference.child("currentContacts")
                                .child(dataSnapshot.key.toString())
                                .child(currentUser.uid).child("theyIsActive")
                                .setValue(true)
                                .addOnCompleteListener { setOnlineForEveryone ->
                                    if (setOnlineForEveryone.isSuccessful) {
                                        Log.d(TAG, "notifyOnlineForEveryone: Success")
                                    } else {
                                        Log.e(TAG, "notifyOnlineForEveryone: fail")
                                    }
                                }
                        }


                    }
                }
            }
    }

    private fun initRecycler() {
        database.reference.child("currentContacts").child(currentUser.uid)
            .addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val createCurrentUserOnData: currentContacts = createCurrentUserOnData(snapshot)
                    oldListUser.add(createCurrentUserOnData)
                    adapterCurrentContact?.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val createCurrentUserOnData = createCurrentUserOnData(snapshot)
                    var count = 0
                    for (currentcontacts: currentContacts in oldListUser) {
                        if (currentcontacts.idUser == createCurrentUserOnData.idUser) {
                            oldListUser[count] = createCurrentUserOnData
                            adapterCurrentContact?.notifyItemChanged(count)
                            break
                        }
                        count++
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })

        dialog!!.dismiss()
    }

    private fun createCurrentUserOnData(dataSnapshot: DataSnapshot): currentContacts {
        val map: Map<*, *> = dataSnapshot.value as Map<*, *>

        val fullname: String = map["fullname"].toString()
        val urlImgProfile: String = map["urlImgProfile"].toString()
        val dateOfBirth: String = map["dateOfBirth"].toString()
        val email: String = map["email"].toString()
        val idUser: String = map["idUser"].toString()
        val lastSentDate: String = map["lastSentDate"].toString()
        val theyIsActive: Boolean = map["theyIsActive"].toString().toBoolean()

        val idUserLastSentMsg: String = map["idUserLastSentMsg"].toString()
        val lastSentMsg: String = map["lastSentMsg"].toString()
        val notifyNewMsg: Boolean = map["notifyNewMsg"].toString().toBoolean()
        return currentContacts(
            dateOfBirth,
            email,
            fullname,
            idUser,
            theyIsActive,
            urlImgProfile,
            idUserLastSentMsg,
            lastSentDate,
            lastSentMsg,
            notifyNewMsg
        )
    }

    private fun addAdapterToRecycle() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.orientation)
        viewFragment.recyClerCurrentContact.addItemDecoration(dividerItemDecoration)

        viewFragment.recyClerCurrentContact.layoutManager = linearLayoutManager
        adapterCurrentContact = adapterCurrentUser(oldListUser, requireContext())
        viewFragment.recyClerCurrentContact.adapter = adapterCurrentContact

    }

    private fun resetAdapterAndAddRecycle(list: MutableList<currentContacts>) {
        adapterCurrentContact = adapterCurrentUser(list, requireContext())
        viewFragment.recyClerCurrentContact.adapter = adapterCurrentContact
    }

    fun registerEvent() {
        viewFragment.refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
        }
        viewFragment.searchCurrentContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                fillterData(editable.toString())
                if (editable.toString().length == 0) {
                    viewFragment.btnHideKeyBoard.visibility = View.INVISIBLE
                } else {
                    viewFragment.btnHideKeyBoard.visibility = View.VISIBLE
                }
            }
        })

        viewFragment.btnHideKeyBoard.setOnClickListener {
            if (searchCurrentContact.isFocused) {
                searchCurrentContact.setText("")
                hideKeyBoardMethod(requireActivity(), searchCurrentContact)
            }
        }

    }

    private fun refreshRecyclerView() {
        oldListUser.clear()
        newListUser.clear()
        initRecycler()
    }

    private fun hideKeyBoardMethod(activity: Activity, view: View) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        view.clearFocus()
    }


    private fun fillterData(value: String) {

        if (value.length == 0) {
            resetAdapterAndAddRecycle(oldListUser)
            return
        }
        newListUser.clear()

        for (currentcontacts in oldListUser) {
            val fullname: String = currentcontacts.fullname
            if (fullname.contains(value)) {
                newListUser.add(currentcontacts)
            }
        }

        if (newListUser.size != 0) {
            resetAdapterAndAddRecycle(newListUser)
        } else {
            resetAdapterAndAddRecycle(oldListUser)
        }
    }


}