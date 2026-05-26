package com.itec.donelio.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarioSemanal(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val sdfMonth = SimpleDateFormat("MMM", Locale.getDefault())
    val sdfDay = SimpleDateFormat("d", Locale.getDefault())

    val days = remember {
        val list = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        for (i in 0..14) {
            list.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(days) { dateMillis ->
            val calSelected = Calendar.getInstance().apply { timeInMillis = selectedDate }
            val calCurrent = Calendar.getInstance().apply { timeInMillis = dateMillis }
            val isSelected = calSelected.get(Calendar.YEAR) == calCurrent.get(Calendar.YEAR) &&
                             calSelected.get(Calendar.DAY_OF_YEAR) == calCurrent.get(Calendar.DAY_OF_YEAR)
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) AgriVerde else Color.White)
                    .clickable { onDateSelected(dateMillis) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = sdfMonth.format(Date(dateMillis)).uppercase(Locale.getDefault()),
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else TextoSecundario
                    )
                    Text(
                        text = sdfDay.format(Date(dateMillis)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextoPrincipal
                    )
                }
            }
        }
    }
}
