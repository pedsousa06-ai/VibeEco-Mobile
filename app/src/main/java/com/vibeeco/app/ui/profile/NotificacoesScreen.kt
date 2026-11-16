package com.vibeeco.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.CheckCheck
import com.composables.icons.lucide.Lucide

private val ABAS = listOf(
    "todas" to "Todas",
    "missao" to "Missões",
    "desafio" to "Desafios",
    "conquista" to "Conquistas",
)

private fun daAba(id: String, todas: List<Notificacao>): List<Notificacao> =
    if (id == "todas") todas else todas.filter { it.tipo.name.lowercase() == id }

@Composable
fun NotificacoesScreen(onVoltar: () -> Unit, onIrPara: (TipoAtividade) -> Unit, modifier: Modifier = Modifier) {
    var aba by rememberSaveable { mutableStateOf("todas") }
    var abertaId by rememberSaveable { mutableStateOf<String?>(null) }

    val todas = NotificacoesStore.itens
    val naoLidas = NotificacoesStore.naoLidas
    val visiveis = daAba(aba, todas)
    val contagens = ABAS.associate { (id, _) -> id to daAba(id, todas).count { !it.lida } }
    val atual = abertaId?.let { id -> todas.firstOrNull { it.id == id } }

    Column(modifier.fillMaxSize().background(PC.Pagina)) {
        BarraTopo("Notificações", onVoltar)

        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Segmentos(ABAS, aba, { aba = it }, contagens = contagens, contagemDestaque = true)

            val habilitado = naoLidas > 0
            Row(
                modifier = Modifier
                    .alpha(if (habilitado) 1f else 0.55f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (habilitado) Color(0xFFE3F5EA) else Color(0xFFF1F5F2))
                    .border(1.dp, if (habilitado) Color(0xFFBFE3CB) else PC.Borda, RoundedCornerShape(10.dp))
                    .clickable(enabled = habilitado, role = Role.Button) { NotificacoesStore.marcarTodasComoLidas() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Lucide.CheckCheck, null, tint = if (habilitado) Color(0xFF0F5A2C) else Color(0xFF8AA393), modifier = Modifier.size(16.dp))
                Text(
                    "Marcar todas como lidas",
                    color = if (habilitado) Color(0xFF0F5A2C) else Color(0xFF8AA393),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(visiveis, key = { it.id }) { n ->
                ItemNotificacao(n) { abertaId = n.id; NotificacoesStore.marcarComoLida(n.id) }
            }
            if (visiveis.isEmpty()) {
                item { EstadoVazio(Lucide.Bell, "Nada por aqui", "Quando houver novidades nesta categoria, elas aparecem aqui.") }
            }
        }
    }

    atual?.let { n ->
        DetalheSheet(
            tipo = n.tipo,
            rotulo = n.tipo.rotulo,
            titulo = n.titulo,
            quando = tempoRelativo(n.minutosAtras),
            onFechar = { abertaId = null },
            acoes = {
                BotaoGradiente("Fechar", Modifier.weight(1f), suave = true) { abertaId = null }
                BotaoGradiente(n.tipo.destinoRotulo, Modifier.weight(1f)) { abertaId = null; onIrPara(n.tipo) }
            },
        ) {
            Text(n.texto, color = Color(0xFF3D4F44), fontSize = 15.sp, lineHeight = 23.sp)
            ListaDetalhes(n.detalhes)
        }
    }
}

@Composable
private fun ItemNotificacao(n: Notificacao, onClick: () -> Unit) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(if (n.lida) Color.White else Color(0xFFDCF8E6))
            .border(1.dp, if (n.lida) PC.Borda else Color(0xFF22C55E), forma)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconeTipo(n.tipo, 44.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(n.titulo, color = PC.Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(n.texto, color = Color(0xFF4B5D52), fontSize = 14.sp, lineHeight = 19.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tempoRelativo(n.minutosAtras), color = PC.TextoSuave, fontSize = 12.sp)
                if (!n.lida) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(PC.Verde600))
                    Text("Não lida", color = PC.Verde700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}