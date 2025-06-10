package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import com.jahi.pipelinetest.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jahi.pipelinetest.ui.theme.Green500
import com.jahi.pipelinetest.ui.theme.SurfaceDark
import com.jahi.pipelinetest.ui.theme.OutlineDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var showYear by remember { mutableStateOf(viewModel.showYearCountdown) }
    var currentAge by remember { mutableStateOf(viewModel.currentAge.toString()) }
    var targetAge by remember { mutableStateOf(viewModel.targetAge.toString()) }
    var elevenLabsApiKey by remember { mutableStateOf(viewModel.elevenLabsApiKey) }


    BackHandler { onDismiss() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_desc)
                    )
                    }
                }
            )
        },
        bottomBar = {
            Row(modifier = Modifier.padding(dimensionResource(R.dimen.padding_default))) {
                Button(
                    onClick = {
                        viewModel.showYearCountdown = showYear
                        viewModel.currentAge = currentAge.toIntOrNull() ?: viewModel.currentAge
                        viewModel.targetAge = targetAge.toIntOrNull() ?: viewModel.targetAge
                        viewModel.elevenLabsApiKey = elevenLabsApiKey

                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green500,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) { Text(stringResource(R.string.action_save)) }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // General Settings
                Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_default))) {
                    RowCheckbox(stringResource(R.string.show_year_countdown), showYear) { showYear = it }
                    DarkTextField(
                        value = currentAge,
                        onValueChange = { currentAge = it },
                        label = { Text(stringResource(R.string.label_current_age)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DarkTextField(
                        value = targetAge,
                        onValueChange = { targetAge = it },
                        label = { Text(stringResource(R.string.label_target_age)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DarkTextField(
                        value = elevenLabsApiKey,
                        onValueChange = { elevenLabsApiKey = it },
                        label = { Text(stringResource(R.string.label_api_key)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(R.dimen.padding_small))
                    )
                }
        }
    }
}

@Composable
private fun RowCheckbox(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onChecked: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_xsmall))
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { if (enabled) onChecked(it) },
            enabled = enabled
        )
        Text(text = label)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        enabled = enabled,
        label = label,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedBorderColor = Green500,
            unfocusedBorderColor = OutlineDark, // Use OutlineDark instead of Slate700 for accessibility
            disabledBorderColor = OutlineDark,  // Use OutlineDark instead of Slate700 for accessibility
            cursorColor = Green500
        )
    )
}

