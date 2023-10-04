package com.ntg.stepcounter.nav

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ntg.stepcounter.screens.HomeScreen
import com.ntg.stepcounter.screens.ProfileScreen
import com.ntg.stepcounter.screens.SettingsScreen
import com.ntg.stepcounter.screens.SocialListScreen
import com.ntg.stepcounter.screens.SocialScreen
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.HomeScreen.name,
    stepViewModel: StepViewModel,
    userDataViewModel: UserDataViewModel,
    socialNetworkViewModel: SocialNetworkViewModel,
    onDestinationChangedListener: (NavController, NavDestination, Bundle?) -> Unit
) {

    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        onDestinationChangedListener(
            controller,
            destination,
            arguments
        )
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(Screens.HomeScreen.name) {
            HomeScreen(navController, stepViewModel)
        }

        composable(Screens.ProfileScreen.name) {
            ProfileScreen(navController, stepViewModel, userDataViewModel, socialNetworkViewModel)
        }

        composable(Screens.SocialScreen.name+ "?id={id}",
            arguments = listOf(navArgument("id")
            {
                type = NavType.IntType
                defaultValue = -1
            })) {
            SocialScreen(navController, socialNetworkViewModel, it.arguments?.getInt("id"))
        }

        composable(Screens.SocialListScreen.name) {
            SocialListScreen(navController, socialNetworkViewModel)
        }

        composable(Screens.SettingsScreen.name) {
            SettingsScreen(navController)
        }

    }

}