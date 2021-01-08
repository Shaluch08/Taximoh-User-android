package com.uveous.loopfoonpay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.navigation.NavigationView
import java.lang.reflect.Array.newInstance

class TravelDashboard : AppCompatActivity(){

    var navigationPosition: Int = 0
    lateinit var drawerLayout : DrawerLayout;
    lateinit var toolbar : Toolbar
    lateinit var navigationView: NavigationView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.travel_dashboard)

        initView()
    }

    private fun initView(){
        toolbar=findViewById(R.id.toolbar)
        drawerLayout=findViewById(R.id.drawerLayout)
        navigationView=findViewById(R.id.navigationView1)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        setUpDrawerLayout()

        //Load Inbox fragment first
        navigationPosition = R.id.trip
        navigateToFragment(TravelFragment.newInstance())
        navigationView.setCheckedItem(navigationPosition)


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.trip -> {
                    val i=Intent(this,Trip::class.java)
                    startActivity(i)
                }
              /*  R.id.navItemSent -> {
                    toolbar.title = getString(R.string.sent)
                    navigationPosition = R.id.navItemSent
                    navigateToFragment(SentFragment.newInstance())
                }
                R.id.navItemDraft -> {
                    toolbar.title = getString(R.string.draft)
                    navigationPosition = R.id.navItemDraft
                    navigateToFragment(DraftFragment.newInstance())
                }
                R.id.navItemTrash -> {
                    toolbar.title = getString(R.string.trash)
                    navigationPosition = R.id.navItemTrash
                    navigateToFragment(TrashFragment.newInstance())
                }
                R.id.navItemSettings -> {
                    toolbar.title = getString(R.string.settings)
                    navigationPosition = R.id.navItemSettings
                    navigateToFragment(SettingsFragment.newInstance())
                }*/
            }
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            drawerLayout.closeDrawers()
            true
        }

        //Change navigation header information
        changeNavigationHeaderInfo()



    }


    private fun changeNavigationHeaderInfo() {
        val headerView = navigationView.getHeaderView(0)
        headerView.setOnClickListener(View.OnClickListener {
            val i=Intent(this,ProfileDetail::class.java)
            startActivity(i)
        })
      //  headerView.textEmail.text = "lokeshdesai@android4dev.com"
    }

    private fun setUpDrawerLayout() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun navigateToFragment(fragmentToNavigate: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragmentToNavigate)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        }

        if (navigationPosition == R.id.trip) {
            finish()
        } else {
            //Navigate to Inbox Fragment
            navigationPosition = R.id.trip
         //   navigateToFragment(InboxFragment.newInstance())
        //    navigationView.setCheckedItem(navigationPosition)
            toolbar.title = "Travel"
        }
    }

}
