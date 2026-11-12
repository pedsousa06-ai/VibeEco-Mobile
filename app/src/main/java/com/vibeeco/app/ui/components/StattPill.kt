package com.vibeeco.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.theme.BlueContainer
import com.vibeeco.app.ui.theme.BlueAccentDark
import com.vibeeco.app.ui.theme.YellowContainer
import com.vibeeco.app.ui.theme.YellowAccentDark
import com.vibeeco.app.ui.theme.RedContainer
import com.vibeeco.app.ui.theme.RedDangerDark
import com.vibeeco.app.ui.theme.GreenContainer
import com.vibeeco.app.ui.theme.GreenOnContainer
import com.vibeeco.app.ui.theme.PurpleContainer
import com.vibeeco.app.ui.theme.PurpleAccentDark

enum class StatPillType { XP, Coin, Life, Streak, Rank }

@Composable
fun StatPill(
    icon: ImageVector,
    value: String,
    type: StatPillType = StatPillType.XP,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (type) {
        StatPillType.XP     -> BlueContainer   to BlueAccentDark
        StatPillType.Coin   -> YellowContainer to YellowAccentDark
        StatPillType.Life   -> RedContainer    to RedDangerDark
        StatPillType.Streak -> GreenContainer  to GreenOnContainer
        StatPillType.Rank   -> PurpleContainer to PurpleAccentDark
    }

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = value,
            color = fg,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
        )
    }
}