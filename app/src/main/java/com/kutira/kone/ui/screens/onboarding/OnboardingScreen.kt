package com.kutira.kone.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutira.kone.ui.theme.KutiraGradientEnd
import com.kutira.kone.ui.theme.KutiraGradientStart
import com.kutira.kone.ui.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

private data class Page(val title: String, val body: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        Page(
            "Hyper-local scraps",
            "Tailors list leftover fabrics; artisans nearby discover them in minutes, not miles."
        ),
        Page(
            "Swap or buy",
            "Give beautiful materials a second life—trade skills, stories, or a fair price."
        ),
        Page(
            "Design with Gemini",
            "Turn odd-shaped remnants into pouches, patchwork, cushions, and more with AI prompts."
        )
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(KutiraGradientStart.copy(alpha = 0.9f), KutiraGradientEnd.copy(alpha = 0.95f))
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "Welcome to Kutira-Kone",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))
            HorizontalPager(state = pagerState) { page ->
                val item = pages[page]
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            item.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(item.body, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Start)
                    }
                }
            }
        }

        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val active = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(10.dp)
                            .width(if (active) 28.dp else 10.dp)
                            .background(
                                color = if (active) Color.White else Color.White.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
                    } else {
                        viewModel.complete()
                        onFinished()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                val label = if (pagerState.currentPage < pages.lastIndex) "Next" else "Get started"
                Text(label)
            }
        }
    }
}
