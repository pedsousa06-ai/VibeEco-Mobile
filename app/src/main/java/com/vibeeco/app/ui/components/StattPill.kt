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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.theme.*

enum class StatPillType { XP, Coin, Life, Streak, Rank }

@Composable
fun StatPill(
    icon: ImageVector,
    value: String,
    type: StatPillType = StatPillType.XP,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (type) {
        StatPillType.XP     -> GoldXPContainer         to GoldXPText
        StatPillType.Coin   -> CoinMCContainer         to CoinMCText
        StatPillType.Life   -> RedContainer            to RedDangerDark
        StatPillType.Streak -> FlameOrangeContainer    to FlameOrange
        StatPillType.Rank   -> PurpleChallengeContainer to PurpleChallenge
    }

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = value,
            color = fg,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp
        )
    }
}
