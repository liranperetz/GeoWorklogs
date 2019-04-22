package com.bringg.ui

import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionManager
import com.bringg.R
import com.bringg.data.local.entity.WorkAddress
import com.bringg.geo.Utils
import com.bringg.vm.WorkAddressVM
import kotlinx.android.synthetic.main.fragment_configuration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FragmentConfiguration : Fragment() {

    private lateinit var workAddressVM: WorkAddressVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_configuration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create view model
        workAddressVM = ViewModelProviders.of(this).get(WorkAddressVM::class.java)

        // get work addresses to display it in work_address_content layout
        workAddressVM.getWorkAddress().observe(this, Observer<List<WorkAddress>> {
            // clear all views
            work_address_content.removeAllViews()
            // iterate all over the entries and display them in work_address_content layout
            it.forEach { entry ->
                work_address_content.addView(TextView(context).apply {
                    text = entry.toString()
                })
            }
        })

        // set click listener to save button
        save.setOnClickListener {
            // update views while save in progress
            updateViews(true)

            GlobalScope.launch {
                if (context != null) {
                    // get typed address from the edit text
                    val typedAddress = getTypedAddress()
                    // convert user typed address to addresses objects
                    val address = Utils.toAddressObject(context!!, typedAddress)

                    // check if address object found
                    if (address != null) {
                        Log.d(TAG, "Address object converted successfully")
                        // save the address
                        saveAddress(address, typedAddress)
                    } else {
                        Log.d(TAG, "Address: $typedAddress not found")
                        Toast.makeText(context, "No results, please check your address", Toast.LENGTH_SHORT).show()
                    }

                    // invoke on save done method on main thread
                    GlobalScope.launch(Dispatchers.Main) {
                        // update views
                        updateViews(false)
                    }
                }

            }
        }
    }

    /**
     * Update view by save state,
     * if [saveInProgress] is true all view will disabled and progress bar will be displayed otherwise the opposite
     *
     * @param saveInProgress
     */
    private fun updateViews(saveInProgress: Boolean) {
        if (view != null) {
            TransitionManager.beginDelayedTransition(view as ViewGroup)
            if (saveInProgress) {
                progress.visibility = View.VISIBLE
                save.isEnabled = false
                work_address.isEnabled = false
            } else {
                progress.visibility = View.GONE
                save.isEnabled = true
                work_address.isEnabled = true
            }
        }
    }

    /**
     * Get types address from edit text
     *
     * @return address string
     */
    private fun getTypedAddress(): String = work_address.text?.toString() ?: ""

    /**
     * Save address to local storage
     */
    private fun saveAddress(address: Address, typedAddress: String) = runBlocking {
        workAddressVM.save(
            WorkAddress(
                lat = address.latitude,
                lon = address.longitude,
                typedAddress = typedAddress
            )
        )
    }

    companion object {
        private const val TAG = "FragmentConfiguration"
    }


}