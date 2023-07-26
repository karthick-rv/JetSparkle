package com.example.jetsparkle

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetsparkle.animation.AnimationScreen
import com.example.jetsparkle.download_manager.DownloadManagerScreen
import com.example.jetsparkle.media.VideoPlayerScreen
import com.example.jetsparkle.paging.ui.PagingScreen
import com.example.jetsparkle.ui.theme.JetSparkleTheme
import com.example.jetsparkle.work_manager.WorkManagerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetSparkleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainPage()
                }
            }
        }
    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainPage() {
        val navController = rememberNavController()

        val bottomNavigationItems = listOf(
            BottomNavigationScreens.Paging,
            BottomNavigationScreens.WorkManager,
            BottomNavigationScreens.DownloadManager,
            BottomNavigationScreens.Media,
            BottomNavigationScreens.Animation
        )

        Scaffold(bottomBar = {
            JetSparkleBottomNav(navController, bottomNavigationItems)
        },) {
            NavHost(navController = navController, startDestination = BottomNavigationScreens.Paging.route){
                composable(BottomNavigationScreens.Paging.route){ PagingScreen()}
                composable(BottomNavigationScreens.WorkManager.route){ WorkManagerScreen()}
                composable(BottomNavigationScreens.DownloadManager.route){ DownloadManagerScreen()}
                composable(BottomNavigationScreens.Media.route){ VideoPlayerScreen()}
                composable(BottomNavigationScreens.Animation.route){ AnimationScreen()}

            }
        }
    }


    @Composable
    fun JetSparkleBottomNav(
        navController: NavHostController,
        bottomNavigationItems: List<BottomNavigationScreens>
    ) {
        BottomNavigation {
            val currentRoute = currentRoute(navController)
            bottomNavigationItems.forEach {screen ->
                BottomNavigationItem(icon = { Icon(imageVector = screen.icon, contentDescription = screen.route)},
                    label = { Text(stringResource(id = screen.resourceId)) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        // This if check gives us a "singleTop" behavior where we do not create a
                        // second instance of the composable if we are already on that destination
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route)
                        }
                    })
            }
        }
    }

    @Composable
    private fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }


    sealed class BottomNavigationScreens(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
        object Paging : BottomNavigationScreens("Paging", R.string.paging_route, Icons.Filled.Home)
        object WorkManager : BottomNavigationScreens("WorkManager", R.string.work_manager_route, Icons.Filled.Build)
        object DownloadManager : BottomNavigationScreens("DownloadManager", R.string.download_manager_route, Icons.Filled.AccountBox)
        object Media : BottomNavigationScreens("Media", R.string.media_route, Icons.Filled.PlayArrow)
        object Animation : BottomNavigationScreens("Animation", R.string.animation_route, Icons.Filled.AddCircle)
    }

}

