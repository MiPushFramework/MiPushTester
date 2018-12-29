package moe.yuuta.mipushtester

import android.os.Bundle

import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
    @Override
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NavigationUI.setupActionBarWithNavController(this, Navigation.findNavController(this, R.id.nav_host))
    }

    @Override
    override fun onSupportNavigateUp(): Boolean =
        Navigation.findNavController(this, R.id.nav_host).navigateUp()
}
