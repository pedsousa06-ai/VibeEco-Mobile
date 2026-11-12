# VibeEco (Android • Kotlin • Jetpack Compose)

## Abrir
File > Open > pasta **VibeEco** (a que tem `settings.gradle.kts`) > aguarde o Gradle Sync.
Para ver as telas sem emulador: abra `ui/auth/AuthPreviews.kt` e use **Split/Design**.

## Contas de demonstração
| Uso | Login | Senha |
|---|---|---|
| Conta normal | `ana.beatriz@ufmg.br` ou matrícula `2023001234` | `Vibe@1234` |
| Primeiro acesso (senha provisória) | `novo.aluno@ufmg.br` ou matrícula `2024005678` | `Temp@1234` |

Recuperar senha: use um dos e-mails acima. O app mostra o código na tela (simulação de e-mail).

## Fluxo
Splash → Onboarding (1ª vez) → Login → Início / Desafios / Ranking / Aprender / Perfil
Login → Esqueci minha senha → Código → Redefinir senha → Login
Login (senha provisória) → Criar nova senha → Início
Perfil → Alterar senha | Sair

## Estrutura (app/src/main/java/com/vibeeco/app)
- data/        Models.kt (conteúdo), Store.kt (persistência local), AppViewModel.kt (regras)
- navigation/  VibeNavHost.kt (rotas + barra inferior)
- ui/auth      Splash, Onboarding, Login, Esqueci a senha, Código, Nova senha
- ui/home, challenges, ranking, learn, profile
- ui/components, ui/theme
