package com.charlesmuchogo.research

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.charlesmuchogo.research.domain.viewmodels.AuthenticationViewModel
import com.charlesmuchogo.research.presentation.authentication.LoginPage
import com.charlesmuchogo.research.presentation.authentication.MoreDetailsPage
import com.charlesmuchogo.research.presentation.bottomBar.HomePage
import com.charlesmuchogo.research.presentation.common.CenteredColumn
import com.charlesmuchogo.research.presentation.utils.ProvideAppNavigator
import com.charlesmuchogo.research.presentation.utils.RequestPermissions
import com.charlesmuchogo.research.presentation.utils.ResultStatus
import dagger.hilt.android.AndroidEntryPoint
import ui.theme.SmartTestTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            SmartTestTheme(dynamicColor = false, darkTheme = false) {
                RequestPermissions()
                val authenticationViewModel = hiltViewModel<AuthenticationViewModel>()
                val profileStatus =
                    authenticationViewModel.profileStatus.collectAsStateWithLifecycle().value
                when (profileStatus.status) {
                    ResultStatus.INITIAL,
                    ResultStatus.LOADING,
                    -> { }

                    ResultStatus.ERROR -> {
                        Navigator(
                            screen = LoginPage(),
                            content = { navigator ->
                                ProvideAppNavigator(
                                    navigator = navigator,
                                    content = { FadeTransition(navigator = navigator) },
                                )
                            },
                        )
                    }

                    ResultStatus.SUCCESS -> {
                        Navigator(
                            screen = if (profileStatus.data != null) {
                                if (profileStatus.data.age.isBlank() || profileStatus.data.educationLevel.isBlank()) {
                                    MoreDetailsPage()
                                } else {
                                    HomePage()
                                }
                            } else {
                                LoginPage()
                            },
                            content = { navigator ->
                                ProvideAppNavigator(
                                    navigator = navigator,
                                    content = { FadeTransition(navigator = navigator) },
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
