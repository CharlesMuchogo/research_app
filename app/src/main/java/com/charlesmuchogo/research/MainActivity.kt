package com.charlesmuchogo.research

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.CircularProgressIndicator
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.charlesmuchogo.research.domain.viewmodels.AuthenticationViewModel
import com.charlesmuchogo.research.presentation.authentication.LoginPage
import com.charlesmuchogo.research.presentation.bottomBar.HomePage
import com.charlesmuchogo.research.presentation.common.CenteredColumn
import com.charlesmuchogo.research.presentation.utils.ProvideAppNavigator
import com.charlesmuchogo.research.presentation.utils.ResultStatus
import com.charlesmuchogo.research.ui.theme.SmartTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTestTheme {
                val authenticationViewModel = hiltViewModel<AuthenticationViewModel>()
                val profileStatus = authenticationViewModel.profileStatus.collectAsStateWithLifecycle().value
                when(profileStatus.status){
                    ResultStatus.INITIAL,
                    ResultStatus.LOADING -> {
                        CenteredColumn {
                            CircularProgressIndicator()
                        }
                    }
                    ResultStatus.ERROR -> {
                        Navigator(
                            screen = LoginPage(),
                            content = { navigator ->
                                ProvideAppNavigator(
                                    navigator = navigator,
                                    content = { FadeTransition(navigator = navigator) }
                                )
                            }
                        )
                    }
                    ResultStatus.SUCCESS -> {
                        Navigator(
                            screen = if (profileStatus.data != null) HomePage() else LoginPage(),
                            content = { navigator ->
                                ProvideAppNavigator(
                                    navigator = navigator,
                                    content = { FadeTransition(navigator = navigator) }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

