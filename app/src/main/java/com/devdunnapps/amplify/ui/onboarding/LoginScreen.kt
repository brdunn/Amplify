package com.devdunnapps.amplify.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginFlowViewModel, onNavigateToServerSelection: () -> Unit) {
    val user by viewModel.user.collectAsState()
    val twoFactorAuthRequired by viewModel.twoFactorAuthRequired.collectAsState()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var twoFactorToken by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val submitEnabled = username.isNotEmpty()
            && password.isNotEmpty()
            && if (twoFactorAuthRequired) twoFactorToken.isNotEmpty() else true

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.displayMedium
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = stringResource(R.string.username)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                isError = user is Resource.Error,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )

            var isPasswordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(R.string.password)) },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (twoFactorAuthRequired) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (submitEnabled) {
                            focusManager.clearFocus()
                            viewModel.login(username, password, twoFactorToken)
                            onNavigateToServerSelection()
                        }
                    }
                ),
                trailingIcon = {
                    val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = null
                        )
                    }
                },
                isError = user is Resource.Error,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )

            if (twoFactorAuthRequired) {
                OutlinedTextField(
                    value = twoFactorToken,
                    onValueChange = { twoFactorToken = it },
                    label = { Text(stringResource(R.string.two_factor_token)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions {
                        if (submitEnabled) {
                            focusManager.clearFocus()
                            viewModel.login(username, password, twoFactorToken)
                            onNavigateToServerSelection()
                        }
                    },
                    isError = user is Resource.Error,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.login(username, password, twoFactorToken)
                    onNavigateToServerSelection()
                },
                enabled = submitEnabled
            ) {
                Text(text = stringResource(R.string.login))
            }
        }
    }
}
