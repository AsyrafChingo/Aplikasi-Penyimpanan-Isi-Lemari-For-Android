package com.dicoding.assosiate.lemariku.Fragment


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Size
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.dicoding.assosiate.lemariku.Class.ClassLemariku
import com.dicoding.assosiate.lemariku.CostumpagerAdapter
import com.dicoding.assosiate.lemariku.CustomAdapter
import com.dicoding.assosiate.lemariku.DatabaseHelper

import com.dicoding.assosiate.lemariku.R
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Home : Fragment() {
    var rvCategory : RecyclerView? = null
    var listPakaian = ArrayList<ClassLemariku>()

    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)


        rvCategory = view.findViewById(R.id.rv_category) as RecyclerView
        rvCategory!!.setHasFixedSize(true)

        // UNTUK SWIPE DOWN FOR REFRESH
        val swipeRefreshLayout =view.findViewById<SwipeRefreshLayout>(R.id.swipeLayout)
        mHandler = Handler()
        swipeRefreshLayout.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                // Hide swipe to refresh icon animation
                showRecyclerList()
                swipeRefreshLayout.isRefreshing = false
            }

            // eksekusi berapa lama waktu tunggunya
            mHandler.postDelayed(
                    mRunnable,
                    (1000).toLong()
            )
        }
        showRecyclerList()
        return view
    }



    override fun onResume() {
        super.onResume()
        showRecyclerList()
    }

    fun showRecyclerList()
    {
        listPakaian.clear()
        var databaseHelper = DatabaseHelper(context!!)
        rvCategory!!.layoutManager = LinearLayoutManager(context!!,LinearLayout.VERTICAL , false)
        listPakaian.addAll(databaseHelper!!.selectAllPakaian())
        val adapter = CustomAdapter(context!!,listPakaian)
        rvCategory!!.adapter = adapter

    }

}
