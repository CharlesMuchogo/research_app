package com.charlesmuchogo.research.presentation.testpage

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.charlesmuchogo.research.domain.dto.results.UploadTestResultsDTO
import com.charlesmuchogo.research.domain.models.TextFieldState
import com.charlesmuchogo.research.domain.viewmodels.TestResultsViewModel
import com.charlesmuchogo.research.presentation.common.AppButton
import com.charlesmuchogo.research.presentation.common.AppDropDown
import com.charlesmuchogo.research.presentation.common.AppLoginButtonContent
import com.charlesmuchogo.research.presentation.common.CenteredColumn
import com.charlesmuchogo.research.presentation.common.TestProgress
import com.charlesmuchogo.research.presentation.utils.ImagePicker
import com.charlesmuchogo.research.presentation.utils.ResultStatus
import com.charlesmuchogo.research.presentation.utils.convertMillisecondsToTimeTaken

class SingleTestPage : Screen {
    @Composable
    override fun Content() {
        CenteredColumn {
            Text(text = "Single Test Page")
        }
    }
}

@Composable
fun SingleTestScreen(modifier: Modifier = Modifier) {
    val testResultsViewModel = hiltViewModel<TestResultsViewModel>()
    val context = LocalContext.current
    val imagePicker = ImagePicker(context)
    val clinicsStatus = testResultsViewModel.getClinicsStatus.collectAsStateWithLifecycle().value
    val uploadResultsStatus =
        testResultsViewModel.uploadResultsStatus.collectAsStateWithLifecycle().value
    val userImage = testResultsViewModel.userImage.collectAsStateWithLifecycle().value
    val selectedClinic = testResultsViewModel.selectedClinic.collectAsStateWithLifecycle().value
    val ongoingTestStatus =
        testResultsViewModel.ongoingTestStatus.collectAsStateWithLifecycle().value

    val percentage = ((ongoingTestStatus.data?.timeSpent?: 0L ).toFloat() / 1_200_000.toFloat() ) * 100

    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    val color =  MaterialTheme.colorScheme.onBackground


    imagePicker.RegisterPicker(onImagePicked = { image ->
        testResultsViewModel.updateUserImage(image)
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                TestProgress(
                    content = convertMillisecondsToTimeTaken(
                        ongoingTestStatus.data?.timeSpent ?: 0L
                    ),
                    counterColor = MaterialTheme.colorScheme.onBackground,
                    radius = 30.dp,
                    mainColor = MaterialTheme.colorScheme.primary,
                    percentage = percentage,
                    onClick = {
                        ongoingTestStatus.data?.let {
                            testResultsViewModel.completeTestTimer(it)
                        } ?: testResultsViewModel.startTest()
                    }
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(vertical = 24.dp)
                            .drawBehind {
                                drawRoundRect(
                                    color = color,
                                    style = stroke
                                )
                            }
                            .clickable(
                                onClick = { imagePicker.captureImage() },
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null
                            )
                    ) {
                        if (userImage != null) {
                            val bitmap =
                                BitmapFactory.decodeByteArray(userImage, 0, userImage.size)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Captured test image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Take photo of the test")
                            }
                        }

                    }
                }
            }

            item {
                clinicsStatus.data?.let { clinics ->
                    AppDropDown(
                        options = clinics,
                        label = { Text(text = "Select a clinic") },
                        selectedOption = TextFieldState(
                            text = selectedClinic?.name ?: "Select a clinic",
                            isSelected = selectedClinic != null,
                            error = null
                        ),
                        onOptionSelected = {
                            testResultsViewModel.updateSelectedClinic(it)
                        }) {
                        Text(
                            "${it.name} - ${it.address}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            item {
                uploadResultsStatus.message?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            item {
                uploadResultsStatus.message?.let {
                    Text(
                        text = it,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                    )
                }

                uploadResultsStatus.data?.message?.let {
                    Text(
                        text = it,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            item {
                AppButton(onClick = {
                    userImage?.let {
                        testResultsViewModel.updateResults(
                            UploadTestResultsDTO(
                                image = it,
                                careOption = selectedClinic?.name
                            )
                        )
                    }
                }) {
                    when (uploadResultsStatus.status) {
                        ResultStatus.LOADING -> {
                            AppLoginButtonContent(message = "Submitting...")
                        }

                        ResultStatus.INITIAL,
                        ResultStatus.SUCCESS,
                        ResultStatus.ERROR -> {
                            Text(text = "Submit results")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
