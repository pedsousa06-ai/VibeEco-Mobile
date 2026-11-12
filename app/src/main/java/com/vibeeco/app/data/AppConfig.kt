package com.vibeeco.app.data

object AppConfig {
    /**
     * true  = login SIMULADO: os campos já vêm preenchidos e qualquer dado entra no app.
     *         (se digitar a matrícula/e-mail de outra conta de demonstração, entra nela)
     * false = login de verdade, validando e-mail/matrícula e senha.
     */
    const val SIMULATED_LOGIN = true

    const val DEMO_EMAIL = "ana.beatriz@ufmg.br"
    const val DEMO_PASSWORD = "Vibe@1234"
}
