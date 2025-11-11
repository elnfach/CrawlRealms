package com.elnfach.auth.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elnfach.ui.R
import com.elnfach.ui.theme.CrawlRealmsStaticTheme

@Composable
fun LoginScreen(
    navController: NavController,
) {
    LoginContent()
}

@Composable
fun LoginContent()
{
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            Modifier.padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Username")
            TextField(username.value, {it -> username.value = it})
            Text(stringResource(R.string.password))
            TextField(password.value, {it -> password.value = it})

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                }
            ) {
                Text(stringResource(R.string.login))
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview()
{
    CrawlRealmsStaticTheme {
        LoginContent()
    }
}