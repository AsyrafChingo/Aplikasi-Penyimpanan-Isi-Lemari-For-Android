package com.dicoding.assosiate.lemariku

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.dicoding.assosiate.lemariku.Fragment.Home
import com.dicoding.assosiate.lemariku.Fragment.Tambah
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_main.*

class MainActivity : AppCompatActivity() {

    var costumpagerAdapter: CostumpagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        costumpagerAdapter = CostumpagerAdapter(supportFragmentManager)
        costumpagerAdapter!!.addFragment(Home(),"Lemariku")
        costumpagerAdapter!!.addFragment(Tambah(),"Tambah")
        costumViewPager.adapter = costumpagerAdapter
        costumTabLayout.setupWithViewPager(costumViewPager)


    }
}
