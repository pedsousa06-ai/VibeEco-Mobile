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

    /**
     * Configurações de integração com a API VibeEco (Node.js).
     * 10.0.2.2 é o alias padrão do Android Emulator para o localhost do computador host.
     * Caso teste em aparelho físico na mesma rede Wi-Fi, substitua pelo IP da máquina (ex: http://192.168.1.X:8080/v1/senai/vibeEco/).
     */
    const val API_BASE_URL = "http://10.0.2.2:8080/v1/senai/vibeEco/"

    /** ID de usuário de desenvolvimento/demonstração (igual ao front web). */
    const val DEV_USER_ID = 1

    const val NETWORK_TIMEOUT_SECONDS = 15L
}
