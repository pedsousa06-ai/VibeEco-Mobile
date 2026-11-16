package com.vibeeco.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Accessibility
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Shield
import com.composables.icons.lucide.User

private val TAMANHOS_FONTE = listOf("pequeno" to "Pequeno", "medio" to "Médio", "grande" to "Grande")

@Composable
fun ConfiguracoesScreen(
    onVoltar: () -> Unit,
    onAlterarSenha: () -> Unit,
    onAviso: (String) -> Unit,
    onSair: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val perfil = PerfilStore.perfil
    val cfg = configuracoes

    Column(modifier.fillMaxSize().background(PC.Pagina)) {
        BarraTopo("Configurações", onVoltar)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Cartao {
                CabecalhoCartao(Lucide.User, "Conta")
                LinhaDado("Nome", perfil.nome.ifBlank { "-" })
                LinhaDado("E-mail", perfil.email.ifBlank { "-" })
                LinhaDado("Matrícula", perfil.matricula.ifBlank { "-" })
                LinhaConfig {
                    Text("Idioma", color = PC.TextoSuave, fontSize = 14.sp, modifier = Modifier.width(100.dp))
                    SeletorIdioma(cfg.idioma) { cfg.idioma = it }
                }
            }
            Cartao {
                CabecalhoCartao(Lucide.Accessibility, "Acessibilidade")
                LinhaConfig {
                    TextoLinha("Tema escuro", "Troca o tema do site", Modifier.weight(1f))
                    Interruptor(cfg.tema == "escuro") { cfg.tema = if (it) "escuro" else "claro" }
                }
                LinhaConfig {
                    TextoLinha("Alto contraste", "Aumenta o contraste das cores", Modifier.weight(1f))
                    Interruptor(cfg.altoContraste) { cfg.altoContraste = it }
                }
                LinhaConfigColuna {
                    TextoLinha("Tamanho da fonte", "Ajuste o tamanho do texto", Modifier.fillMaxWidth())
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TAMANHOS_FONTE.forEach { (valor, rotulo) ->
                            val ativo = cfg.tamanhoFonte == valor
                            Text(
                                rotulo,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (ativo) PC.Verde900 else PC.Fundo)
                                    .clickable(role = Role.RadioButton) { cfg.tamanhoFonte = valor }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                color = if (ativo) Color.White else PC.TextoSuave,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
                LinhaConfig {
                    TextoLinha("Animações", "Ativa ou desativa animações na interface", Modifier.weight(1f))
                    Interruptor(cfg.animacoes) { cfg.animacoes = it }
                }
            }
            Cartao {
                CabecalhoCartao(Lucide.Shield, "Segurança")
                LinhaConfig(onClick = onAlterarSenha) {
                    TextoLinha("Alterar senha", "Última alteração: 30 dias atrás", Modifier.weight(1f))
                    Icon(Lucide.ChevronRight, null, tint = PC.TextoSuave, modifier = Modifier.size(18.dp))
                }
                listOf(
                    "Autenticação em dois fatores" to "Adicione uma camada extra de segurança",
                    "Sessões ativas" to "2 sessões ativas no momento",
                ).forEach { (titulo, descricao) ->
                    LinhaConfig(onClick = { onAviso("$titulo: disponível em breve.") }) {
                        TextoLinha(titulo, descricao, Modifier.weight(1f))
                        Icon(Lucide.ChevronRight, null, tint = PC.TextoSuave, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Cartao {
                CabecalhoCartao(Lucide.Eye, "Privacidade")
                LinhaConfig {
                    TextoLinha("Aparecer no ranking", "Seu nome ficará visível no ranking global", Modifier.weight(1f))
                    Interruptor(cfg.aparecerNoRanking) { cfg.aparecerNoRanking = it }
                }
                LinhaConfig {
                    TextoLinha("Atividades visíveis", "Outros usuários podem ver suas atividades", Modifier.weight(1f))
                    Interruptor(cfg.atividadesVisiveis) { cfg.atividadesVisiveis = it }
                }
                LinhaConfig {
                    TextoLinha("Perfil público", "Qualquer pessoa pode ver seu perfil", Modifier.weight(1f))
                    Interruptor(cfg.perfilPublico) { cfg.perfilPublico = it }
                }
            }
            val forma = RoundedCornerShape(12.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(forma)
                    .background(PC.PerigoFundo)
                    .border(1.dp, PC.PerigoBorda, forma)
                    .clickable(role = Role.Button, onClick = onSair),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Lucide.LogOut, null, tint = PC.Perigo, modifier = Modifier.size(18.dp))
                Text("Sair da conta", color = PC.Perigo, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun LinhaConfig(onClick: (() -> Unit)? = null, conteudo: @Composable RowScope.() -> Unit) {
    Column {
        DivisorLinha()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = conteudo,
        )
    }
}

@Composable
private fun LinhaConfigColuna(conteudo: @Composable ColumnScope.() -> Unit) {
    Column {
        DivisorLinha()
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Center,
            content = conteudo,
        )
    }
}

@Composable
private fun LinhaDado(rotulo: String, valor: String) {
    LinhaConfig {
        Text(rotulo, color = PC.TextoSuave, fontSize = 14.sp, modifier = Modifier.width(100.dp))
        Text(valor, color = PC.Texto, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TextoLinha(titulo: String, descricao: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(titulo, color = PC.Texto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(descricao, color = PC.TextoSuave, fontSize = 12.sp)
    }
}

@Composable
private fun SeletorIdioma(atual: String, onEscolher: (String) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    val opcoes = listOf(
        Triple("pt-BR", "Português (Brasil)", true),
        Triple("en", "English (Em breve)", false),
        Triple("es", "Español (Em breve)", false),
    )
    Box {
        Text(
            "${opcoes.firstOrNull { it.first == atual }?.second ?: "Português (Brasil)"}  ▾",
            modifier = Modifier.clickable(role = Role.DropdownList) { aberto = true }.padding(vertical = 6.dp),
            color = PC.Texto,
            fontSize = 14.sp,
        )
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEach { (valor, rotulo, habilitado) ->
                DropdownMenuItem(
                    text = { Text(rotulo) },
                    enabled = habilitado,
                    onClick = { onEscolher(valor); aberto = false },
                )
            }
        }
    }
}