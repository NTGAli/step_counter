package com.ntg.stepcounter.nav

import android.os.Bundle
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.ntg.stepcounter.components.PrivacyPolicyScreen
import com.ntg.stepcounter.components.TermAndConditionsScreen
import com.ntg.stepcounter.screens.AccountScreen
import com.ntg.stepcounter.screens.EditPhoneNumberScreen
import com.ntg.stepcounter.screens.FieldOfStudyDetailsScreen
import com.ntg.stepcounter.screens.FieldStudiesScreen
import com.ntg.stepcounter.screens.HomeScreen
import com.ntg.stepcounter.screens.LoginScreen
import com.ntg.stepcounter.screens.PermissionsScreen
import com.ntg.stepcounter.screens.PhoneNumberScreen
import com.ntg.stepcounter.screens.ProfRegisterScreen
import com.ntg.stepcounter.screens.ProfileScreen
import com.ntg.stepcounter.screens.RegisterScreen
import com.ntg.stepcounter.screens.SeeMoreScreen
import com.ntg.stepcounter.screens.SettingsScreen
import com.ntg.stepcounter.screens.SignInScreen
import com.ntg.stepcounter.screens.SocialListScreen
import com.ntg.stepcounter.screens.SocialScreen
import com.ntg.stepcounter.screens.UserClapsScreen
import com.ntg.stepcounter.screens.UserProfileScreen
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.vm.LoginViewModel
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
    loginViewModel: LoginViewModel,
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
//        enterTransition = { EnterTransition.None },
//        exitTransition = { ExitTransition.None }
    ) {

        composable(Screens.HomeScreen.name) {
            HomeScreen(navController, stepViewModel, userDataViewModel)
        }

        composable(Screens.ProfileScreen.name) {
            ProfileScreen(navController, stepViewModel, userDataViewModel, socialNetworkViewModel)
        }

        composable(
            Screens.SocialScreen.name + "?id={id}",
            arguments = listOf(navArgument("id")
            {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            SocialScreen(navController, socialNetworkViewModel, userDataViewModel, it.arguments?.getInt("id"))
        }

        composable(Screens.SocialListScreen.name) {
            SocialListScreen(navController, socialNetworkViewModel)
        }

        composable(Screens.SettingsScreen.name) {
            SettingsScreen(navController, userDataViewModel)
        }

        composable(Screens.AccountScreen.name) {
            AccountScreen(navController, userDataViewModel)
        }

        composable(Screens.PhoneNumberScreen.name) {
            PhoneNumberScreen(navController, userDataViewModel)
        }

        composable(Screens.LoginScreen.name) {
            LoginScreen(navController, loginViewModel)
        }

        composable(Screens.EditPhoneNumberScreen.name) {
            EditPhoneNumberScreen(navController, loginViewModel, userDataViewModel)
        }

        composable(
            Screens.SignInScreen.name + "?phone={phone}&state={state}",
            arguments = listOf(
                navArgument("phone")
                {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("state")
                {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) {
            SignInScreen(
                navController,
                loginViewModel,
                userDataViewModel,
                socialNetworkViewModel,
                stepViewModel,
                it.arguments?.getString("phone"),
                it.arguments?.getString("state")
            )
        }

        composable(
            Screens.RegisterScreen.name + "?phone={phone}&edit={edit}",
            arguments = listOf(
                navArgument("phone")
                {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("edit")
                {
                    type = NavType.BoolType
                    defaultValue = false
                })

        ) {
            RegisterScreen(
                navController,
                loginViewModel,
                userDataViewModel,
                it.arguments?.getString("phone"),
                it.arguments?.getBoolean("edit")
            )
        }


        composable(
            Screens.ProfRegisterScreen.name + "?phone={phone}&edit={edit}",
            arguments = listOf(navArgument("phone")
            {
                type = NavType.StringType
                defaultValue = ""
            },
                navArgument("edit")
                {
                    type = NavType.BoolType
                    defaultValue = false
                })
        ) {
            ProfRegisterScreen(
                navController,
                loginViewModel,
                userDataViewModel,
                it.arguments?.getString("phone"),
                it.arguments?.getBoolean("edit").orFalse(),
            )
        }


        composable(Screens.FieldStudiesScreen.name) {
            FieldStudiesScreen(navController, loginViewModel)
        }


        composable(
            Screens.UserProfileScreen.name + "?uid={uid}",
            arguments = listOf(navArgument("uid")
            {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            UserProfileScreen(
                navController,
                userDataViewModel,
                it.arguments?.getString("uid").orEmpty()
            )
        }


        composable(
            Screens.FieldOfStudyDetailsScreen.name + "?uid={uid}&rank={rank}",
            arguments = listOf(
                navArgument("uid")
                {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("rank")
                {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) {
            FieldOfStudyDetailsScreen(
                navController,
                userDataViewModel,
                it.arguments?.getString("uid").orEmpty(),
                it.arguments?.getString("rank").orEmpty()
            )
        }

        composable(
            Screens.SeeMoreScreen.name + "?type={type}",
            arguments = listOf(
                navArgument("type")
                {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) {
            SeeMoreScreen(
                navController,
                userDataViewModel,
                stepViewModel,
                it.arguments?.getString("type").orEmpty(),
            )
        }

        composable(Screens.UserClapsScreen.name) {
            UserClapsScreen(navController, userDataViewModel)
        }

        composable(Screens.PermissionScreen.name) {
            PermissionsScreen(navController)
        }

        composable(Screens.TermAndConditionsScreen.name) {
            TermAndConditionsScreen()
        }

        composable(Screens.PrivacyPolicyScreen.name) {
            PrivacyPolicyScreen()
        }


    }

}