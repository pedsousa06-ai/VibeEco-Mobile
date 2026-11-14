package com.vibeeco.app.ui.community

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Medal
import com.composables.icons.lucide.MessageSquare
import com.composables.icons.lucide.Tag
import com.composables.icons.lucide.Trophy
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.X
import java.text.NumberFormat
import java.util.Locale

/* ------------------------------------------------------------------ */
/*  CORES (equivalentes ao CSS do desktop)                             */
/* ------------------------------------------------------------------ */
private object FC {
    val Fundo = Color(0xFFF4F8F5)          // var(--fundo)
    val Pagina = Color(0xFFF7FAF8)
    val Texto = Color(0xFF12261A)          // var(--texto)
    val TextoSuave = Color(0xFF5B6E62)     // var(--texto-suave)
    val Borda = Color(0xFFDDE7E0)          // var(--borda)
    val Verde600 = Color(0xFF16A34A)
    val Verde900 = Color(0xFF14603A)
    val Verde100 = Color(0xFFDCFCE7)
    val AvatarGradiente = listOf(Color(0xFF22C55E), Color(0xFF0F7A3B))
    val SeloFundo = Color(0xFFFEF3C7)
    val SeloTexto = Color(0xFF8A5A00)
    val Trofeu = Color(0xFFD97706)
    val ErroBorda = Color(0xFFF7CACA)
    val ErroTexto = Color(0xFFB91C1C)
    val ErroFundo = Color(0xFFFEF1F1)
}

private const val LIMITE_TEXTO = 500
private const val LIMITE_FOTO_BYTES = 5L * 1024 * 1024

// TODO: ranking e desafios ainda não têm endpoint no back-end (igual ao Feed.vue)
private val RANKING_SEMANA = listOf(
    RankingSemanaItem(1, "Carlos Mendes", "CM", 4200, "🥇"),
    RankingSemanaItem(2, "Fernanda Lima", "FL", 3850, "🥈"),
    RankingSemanaItem(3, "Rafael Costa", "RC", 3600, "🥉"),
    RankingSemanaItem(4, "Ana Beatriz", "AB", 3100, "#4"),
    RankingSemanaItem(5, "Lucas Nunes", "LN", 2750, "#5"),
)
private val DESAFIOS_ATIVOS = listOf(
    DesafioAtivo(1, "Semana sem Carro", "234 participantes · 4 dias"),
    DesafioAtivo(2, "Desafio da Água", "412 participantes · 1 dia"),
    DesafioAtivo(3, "Adote uma Área Verde", "156 participantes · 8 dias"),
)

/* ------------------------------------------------------------------ */
/*  TELA (com estado)                                                  */
/* ------------------------------------------------------------------ */
@Composable
fun FeedScreen(
    usuarioNome: String,
    usuarioNivel: String,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(),
) {
    var modalAberto by rememberSaveable { mutableStateOf(false) }
    val iniciais = remember(usuarioNome) { iniciaisDe(usuarioNome) }

    FeedContent(
        estado = viewModel.estado,
        usuarioIniciais = iniciais,
        ranking = RANKING_SEMANA,
        desafios = DESAFIOS_ATIVOS,
        onAbrirModal = {
            viewModel.limparErroPublicar()
            modalAberto = true
        },
        onTentarNovamente = { viewModel.carregar() },
        modifier = modifier,
    )

    if (modalAberto) {
        NovaPublicacaoModal(
            usuarioNome = usuarioNome,
            usuarioIniciais = iniciais,
            usuarioNivel = usuarioNivel,
            categorias = viewModel.categorias,
            enviando = viewModel.enviando,
            erro = viewModel.erroPublicar,
            onFechar = { modalAberto = false },
            onPublicar = { texto, categoriaId, foto ->
                viewModel.publicar(texto, categoriaId, foto, onSucesso = { modalAberto = false })
            },
        )
    }
}

private fun iniciaisDe(nome: String): String =
    nome.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "VC" }

/* ------------------------------------------------------------------ */
/*  CONTEÚDO (sem estado, bom para preview)                            */
/* ------------------------------------------------------------------ */
@Composable
fun FeedContent(
    estado: FeedState,
    usuarioIniciais: String,
    ranking: List<RankingSemanaItem>,
    desafios: List<DesafioAtivo>,
    onAbrirModal: () -> Unit,
    onTentarNovamente: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FC.Pagina),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.statusBarsPadding().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Lucide.Users, null, tint = FC.Verde600, modifier = Modifier.size(24.dp))
                Text("Comunidade", color = FC.Texto, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        // CAIXA DE NOVA PUBLICAÇÃO
        item { CaixaCompor(usuarioIniciais, onAbrirModal) }

        when (estado) {
            is FeedState.Carregando -> item { Aviso("Carregando publicações...") }

            is FeedState.Erro -> item {
                Aviso(
                    texto = estado.mensagem,
                    erro = true,
                    acao = { BotaoVerde("Tentar de novo", onClick = onTentarNovamente) },
                )
            }

            is FeedState.Sucesso -> {
                if (estado.posts.isEmpty()) {
                    item { Aviso("Ainda não há publicações. Seja o primeiro a compartilhar!") }
                } else {
                    items(estado.posts, key = { it.id }) { post -> CardPost(post) }
                }
            }
        }

        // LATERAL (no celular vem depois dos posts, como no breakpoint do desktop)
        item { BlocoRanking(ranking) }
        item { BlocoDesafios(desafios) }
    }
}

/* ---------- COMPONENTES BASE ---------- */
@Composable
private fun Avatar(iniciais: String, tamanho: Dp, fonte: Int = 13) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(Brush.linearGradient(FC.AvatarGradiente)),
        contentAlignment = Alignment.Center,
    ) {
        Text(iniciais, color = Color.White, fontSize = fonte.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Card(modifier: Modifier = Modifier, conteudo: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, FC.Borda, RoundedCornerShape(16.dp)),
    ) { conteudo() }
}

private fun Modifier.dashedBorder(cor: Color, raio: Dp): Modifier = drawBehind {
    drawRoundRect(
        color = cor,
        cornerRadius = CornerRadius(raio.toPx()),
        style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))),
    )
}

@Composable
private fun BotaoVerde(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    altura: Dp = 40.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(altura)
            .alpha(if (habilitado) 1f else 0.6f)
            .clip(RoundedCornerShape(10.dp))
            .background(FC.Verde900)
            .clickable(enabled = habilitado, role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(texto, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

/* ---------- CAIXA DE PUBLICAÇÃO ---------- */
@Composable
private fun CaixaCompor(iniciais: String, onAbrirModal: () -> Unit) {
    Card {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatar(iniciais, 38.dp)
            // No celular o botão "Publicar" some e o campo todo abre o modal (como no CSS)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FC.Fundo)
                    .border(1.dp, FC.Borda, RoundedCornerShape(10.dp))
                    .clickable(role = Role.Button, onClickLabel = "Nova publicação", onClick = onAbrirModal)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    "Compartilhe sua conquista ou experiência...",
                    color = FC.TextoSuave,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/* ---------- AVISOS (carregando / erro / vazio) ---------- */
@Composable
private fun Aviso(
    texto: String,
    erro: Boolean = false,
    acao: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .dashedBorder(if (erro) FC.ErroBorda else FC.Borda, 16.dp)
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            texto,
            color = if (erro) FC.ErroTexto else FC.TextoSuave,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        acao?.invoke()
    }
}

/* ---------- POST ---------- */
@Composable
private fun CardPost(post: Post) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Avatar(post.iniciais, 38.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.autor, color = FC.Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(post.tempo, color = FC.TextoSuave, fontSize = 12.sp)
                }
                if (!post.categoria.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(FC.SeloFundo)
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Lucide.Medal, null, tint = FC.SeloTexto, modifier = Modifier.size(13.dp))
                        Text(post.categoria, color = FC.SeloTexto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Text(post.texto, color = FC.Texto, fontSize = 14.sp, lineHeight = 22.sp)

            if (!post.imagem.isNullOrBlank()) {
                AsyncImage(
                    model = post.imagem,
                    contentDescription = "Imagem da publicação de ${post.autor}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FC.Fundo),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Contagem(Lucide.Heart, post.curtidas, "curtidas")
                Contagem(Lucide.MessageSquare, post.comentarios, "comentários")
            }
        }
    }
}

@Composable
private fun Contagem(icone: androidx.compose.ui.graphics.vector.ImageVector, valor: Int, descricao: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icone, contentDescription = descricao, tint = FC.TextoSuave, modifier = Modifier.size(16.dp))
        Text("$valor", color = FC.TextoSuave, fontSize = 13.sp)
    }
}

/* ---------- LATERAL: RANKING E DESAFIOS ---------- */
@Composable
private fun BlocoRanking(ranking: List<RankingSemanaItem>) {
    Card {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Ranking da Semana", color = FC.Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Icon(Lucide.Trophy, null, tint = FC.Trofeu, modifier = Modifier.size(16.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ranking.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            item.medalha,
                            modifier = Modifier.width(22.dp),
                            textAlign = TextAlign.Center,
                            color = FC.TextoSuave,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Avatar(item.iniciais, 30.dp, fonte = 11)
                        Column {
                            Text(item.nome, color = FC.Texto, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "${NumberFormat.getInstance(Locale("pt", "BR")).format(item.xp)} XP",
                                color = FC.TextoSuave,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BlocoDesafios(desafios: List<DesafioAtivo>) {
    Card {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                "Desafios Ativos",
                color = FC.Texto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                desafios.forEach { d ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FC.Fundo)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Text(d.titulo, color = FC.Texto, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        Text(d.detalhe, color = FC.TextoSuave, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/*  MODAL: NOVA PUBLICAÇÃO (NovaPublicacaoModal.vue)                   */
/* ------------------------------------------------------------------ */
@Composable
private fun NovaPublicacaoModal(
    usuarioNome: String,
    usuarioIniciais: String,
    usuarioNivel: String,
    categorias: List<Categoria>,
    enviando: Boolean,
    erro: String,
    onFechar: () -> Unit,
    onPublicar: (texto: String, categoriaId: String?, foto: Uri?) -> Unit,
) {
    val context = LocalContext.current
    var texto by rememberSaveable { mutableStateOf("") }
    var categoriaId by rememberSaveable { mutableStateOf<String?>(null) }
    var foto by rememberSaveable { mutableStateOf<Uri?>(null) }
    var erroLocal by remember { mutableStateOf("") }
    var menuCategoria by remember { mutableStateOf(false) }
    val foco = remember { FocusRequester() }

    LaunchedEffect(Unit) { foco.requestFocus() }

    val seletorFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        erroLocal = ""
        if (uri == null) return@rememberLauncherForActivityResult
        val tipo = context.contentResolver.getType(uri).orEmpty()
        val tamanho = tamanhoDe(context, uri)
        when {
            !tipo.startsWith("image/") -> erroLocal = "Escolha um arquivo de imagem."
            tamanho > LIMITE_FOTO_BYTES -> erroLocal = "A imagem deve ter no máximo 5 MB"
            else -> foto = uri
        }
    }

    fun enviar() {
        if (texto.isBlank()) {
            erroLocal = "Escreva algo antes de publicar."
            return
        }
        onPublicar(texto.trim(), categoriaId, foto)
    }

    Dialog(
        onDismissRequest = onFechar,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Topo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Nova Publicação", color = FC.Texto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(role = Role.Button, onClickLabel = "Fechar", onClick = onFechar),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Lucide.X, contentDescription = "Fechar", tint = FC.TextoSuave, modifier = Modifier.size(18.dp))
                    }
                }

                // Autor
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Avatar(usuarioIniciais, 36.dp)
                    Column {
                        Text(usuarioNome, color = FC.Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            usuarioNivel,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clip(CircleShape)
                                .background(FC.Verde100)
                                .padding(horizontal = 8.dp, vertical = 1.dp),
                            color = Color(0xFF0F6B34),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                // Texto
                OutlinedTextField(
                    value = texto,
                    onValueChange = { if (it.length <= LIMITE_TEXTO) texto = it },
                    modifier = Modifier.fillMaxWidth().focusRequester(foco),
                    minLines = 7,
                    maxLines = 12,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text("Compartilhe sua conquista, experiência ou dica sustentável...", color = FC.TextoSuave, fontSize = 14.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FC.Verde600,
                        unfocusedBorderColor = FC.Borda,
                        focusedContainerColor = FC.Fundo,
                        unfocusedContainerColor = FC.Fundo,
                        focusedTextColor = FC.Texto,
                        unfocusedTextColor = FC.Texto,
                        cursorColor = FC.Verde600,
                    ),
                )

                // Prévia da foto
                foto?.let { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Prévia da foto escolhida",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xA6000000))
                                .clickable(role = Role.Button, onClickLabel = "Remover foto") { foto = null },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Lucide.X, contentDescription = "Remover foto", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Erro
                val mensagemErro = erroLocal.ifEmpty { erro }
                if (mensagemErro.isNotEmpty()) {
                    Text(
                        mensagemErro,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(FC.ErroFundo)
                            .border(1.dp, FC.ErroBorda, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        color = FC.ErroTexto,
                        fontSize = 13.sp,
                    )
                }

                // Rodapé
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Ferramenta(icone = Lucide.Image, texto = "Foto", onClick = { seletorFoto.launch("image/*") })

                        // Só aparece se houver categorias (igual ao v-if do Vue)
                        if (categorias.isNotEmpty()) {
                            Box {
                                Ferramenta(
                                    icone = Lucide.Tag,
                                    texto = categorias.firstOrNull { it.id == categoriaId }?.nome ?: "Categoria",
                                    onClick = { menuCategoria = true },
                                )
                                DropdownMenu(expanded = menuCategoria, onDismissRequest = { menuCategoria = false }) {
                                    DropdownMenuItem(
                                        text = { Text("Categoria") },
                                        onClick = { categoriaId = null; menuCategoria = false },
                                    )
                                    categorias.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c.nome) },
                                            onClick = { categoriaId = c.id; menuCategoria = false },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    BotaoVerde(
                        texto = if (enviando) "Publicando..." else "Publicar",
                        habilitado = !enviando,
                        altura = 38.dp,
                        onClick = ::enviar,
                    )
                }
            }
        }
    }
}

@Composable
private fun Ferramenta(
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(FC.Fundo)
            .border(1.dp, FC.Borda, RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icone, null, tint = FC.Texto, modifier = Modifier.size(16.dp))
        Text(texto, color = FC.Texto, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

private fun tamanhoDe(context: Context, uri: Uri): Long =
    context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { c ->
        if (c.moveToFirst() && !c.isNull(0)) c.getLong(0) else -1L
    } ?: -1L

/* ------------------------------------------------------------------ */
/*  PREVIEW                                                            */
/* ------------------------------------------------------------------ */
@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun FeedPreview() {
    FeedContent(
        estado = FeedState.Sucesso(
            listOf(
                Post("1", "Carlos Mendes", "CM", "há 2 h", "Conquista", "Completei a Semana sem Carro! 🚲", null, 24, 6),
                Post("2", "Fernanda Lima", "FL", "há 5 h", null, "Dica rápida: separar o lixo orgânico ajuda muito.", null, 18, 3),
            )
        ),
        usuarioIniciais = "VC",
        ranking = RANKING_SEMANA,
        desafios = DESAFIOS_ATIVOS,
        onAbrirModal = {},
        onTentarNovamente = {},
    )
}
