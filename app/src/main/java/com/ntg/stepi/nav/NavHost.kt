package com.ntg.stepi.nav

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
import com.ntg.stepi.components.PrivacyPolicyScreen
import com.ntg.stepi.components.TermAndConditionsScreen
import com.ntg.stepi.screens.AccountScreen
import com.ntg.stepi.screens.AddFosScreen
import com.ntg.stepi.screens.DeadVersionScreen
import com.ntg.stepi.screens.EditPhoneNumberScreen
import com.ntg.stepi.screens.EmployeeScreen
import com.ntg.stepi.screens.FieldOfStudyDetailsScreen
import com.ntg.stepi.screens.FieldStudiesScreen
import com.ntg.stepi.screens.HomeScreen
import com.ntg.stepi.screens.LoginScreen
import com.ntg.stepi.screens.MessagesBoxScreen
import com.ntg.stepi.screens.PermissionsScreen
import com.ntg.stepi.screens.PhoneNumberScreen
import com.ntg.stepi.screens.ProfRegisterScreen
import com.ntg.stepi.screens.ProfileScreen
import com.ntg.stepi.screens.RegisterScreen
import com.ntg.stepi.screens.RoleScreen
import com.ntg.stepi.screens.SeeMoreScreen
import com.ntg.stepi.screens.SettingsScreen
import com.ntg.stepi.screens.SignInScreen
import com.ntg.stepi.screens.SocialListScreen
import com.ntg.stepi.screens.SocialScreen
import com.ntg.stepi.screens.DataChallengesScreen
import com.ntg.stepi.screens.JobScreen
import com.ntg.stepi.screens.OtherRegister
import com.ntg.stepi.screens.SelectLanguageScreen
import com.ntg.stepi.screens.UpdateScreen
import com.ntg.stepi.screens.UserClapsScreen
import com.ntg.stepi.screens.UserProfileScreen
import com.ntg.stepi.util.extension.orFalse
import com.ntg.stepi.vm.LoginViewModel
import com.ntg.stepi.vm.MessageViewModel
import com.ntg.stepi.vm.SocialNetworkViewModel
import com.ntg.stepi.vm.StepViewModel
import com.ntg.stepi.vm.UserDataViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.HomeScreen.name,
    stepViewModel: StepViewModel,
    userDataViewModel: UserDataViewModel,
    socialNetworkViewModel: SocialNetworkViewModel,
    loginViewModel: LoginViewModel,
    messageViewModel: MessageViewModel,
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
            LoginScreen(navController, loginViewModel, userDataViewModel)
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
            Screens.EmployeeScreen.name + "?phone={phone}&edit={edit}",
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
            EmployeeScreen(
                navController,
                loginViewModel,
                userDataViewModel,
                it.arguments?.getString("phone"),
                it.arguments?.getBoolean("edit").orFalse()
            )
        }


        composable(
            Screens.OtherRegisterScreen.name + "?phone={phone}&edit={edit}",
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
            OtherRegister(
                navController,
                loginViewModel,
                userDataViewModel,
                it.arguments?.getString("phone"),
                it.arguments?.getBoolean("edit").orFalse()
            )
        }


        composable(
            Screens.RoleScreen.name + "?phone={phone}",
            arguments = listOf(
                navArgument("phone")
                {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            RoleScreen(
                navController,
                it.arguments?.getString("phone").orEmpty(),
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

        composable(Screens.JobScreen.name) {
            JobScreen(navController, loginViewModel)
        }

        composable(Screens.MessagesBoxScreen.name+ "?uid={uid}",
            arguments = listOf(navArgument("uid")
            {
                type = NavType.StringType
                defaultValue = ""
            })) {
            MessagesBoxScreen(navController, messageViewModel,it.arguments?.getString("uid").orEmpty())
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
            PermissionsScreen(navController, userDataViewModel)
        }

        composable(Screens.TermAndConditionsScreen.name) {
            TermAndConditionsScreen()
        }

        composable(Screens.PrivacyPolicyScreen.name) {
            PrivacyPolicyScreen()
        }

        composable(Screens.UpdateScreen.name) {
            UpdateScreen(navController,userDataViewModel)
        }

        composable(Screens.DeadVersionScreen.name) {
            DeadVersionScreen(navController)
        }

        composable(Screens.SelectLanguageScreen.name) {
            SelectLanguageScreen(navController,userDataViewModel)
        }

        composable(Screens.AddFosScreen.name) {
            AddFosScreen(navController, loginViewModel)
        }

        composable(
            Screens.DataChallengesScreen.name + "?uid={uid}",
            arguments = listOf(
                navArgument("uid")
                {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) {
            DataChallengesScreen(
                navController,
                stepViewModel,
                it.arguments?.getString("uid").orEmpty(),
            )
        }


    }

}