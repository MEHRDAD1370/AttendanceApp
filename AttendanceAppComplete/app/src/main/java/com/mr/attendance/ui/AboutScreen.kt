package com.mr.attendance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun AboutScreen(onBack:()->Unit){Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("درباره برنامه",style=MaterialTheme.typography.headlineMedium);Spacer(Modifier.height(20.dp));Text("اپلیکیشن شخصی حضور و غیاب و محاسبه اضافه‌کار",style=MaterialTheme.typography.titleMedium);Spacer(Modifier.height(14.dp));Text("برنامه نویس: M.R");Spacer(Modifier.height(24.dp));Text("نسخه 1.0");Spacer(Modifier.height(30.dp));Button(onClick=onBack){Text("بازگشت")}}}
