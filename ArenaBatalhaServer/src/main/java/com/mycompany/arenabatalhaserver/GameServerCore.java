package com.mycompany.arenabatalhaserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameServerCore extends Thread {

    // --- ENUMS E CLASSES DE DADOS ---
    
    // Nossos 6 tipos + um tipo "NULO"
    private enum Tipo {
        FOGO, AGUA, PLANTA, ELETRICO, TERRA, VOADOR, NENHUM
    }

    /**
     * Uma classe interna simples para guardar os dados de um Pokémon.
     */
    private static class PokemonData {
        String nome;
        Tipo tipo1;
        Tipo tipo2;

        public PokemonData(String nome, Tipo tipo1, Tipo tipo2) {
            this.nome = nome;
            this.tipo1 = tipo1;
            this.tipo2 = tipo2;
        }
        
        public PokemonData(String nome, Tipo tipo1) {
            this(nome, tipo1, Tipo.NENHUM); // Construtor para tipo puro
        }
    }

    // --- "BANCO DE DADOS" DOS POKÉMON ---
    // Um Map estático para guardar nossos 12 Pokémon
    private static final Map<String, PokemonData> POKEMON_DB = new HashMap<>();
    static {
        // Puros (8)
        POKEMON_DB.put("Charmander", new PokemonData("Charmander", Tipo.FOGO));
        POKEMON_DB.put("Vulpix", new PokemonData("Vulpix", Tipo.FOGO));
        POKEMON_DB.put("Squirtle", new PokemonData("Squirtle", Tipo.AGUA    ));
        POKEMON_DB.put("Staryu", new PokemonData("Staryu", Tipo.AGUA));
        POKEMON_DB.put("Treecko", new PokemonData("Treecko", Tipo.PLANTA));
        POKEMON_DB.put("Elekid", new PokemonData("Elekid", Tipo.ELETRICO));
        POKEMON_DB.put("Pikachu", new PokemonData("Pikachu", Tipo.ELETRICO));
        POKEMON_DB.put("Diglett", new PokemonData("Diglett", Tipo.TERRA));
        
        // Duplos (4)
        POKEMON_DB.put("Wooper", new PokemonData("Wooper", Tipo.AGUA, Tipo.TERRA));
        POKEMON_DB.put("Numel", new PokemonData("Numel", Tipo.FOGO, Tipo.TERRA));
        POKEMON_DB.put("Rowlet", new PokemonData("Rowlet", Tipo.PLANTA, Tipo.VOADOR));
        POKEMON_DB.put("Emolga", new PokemonData("Emolga", Tipo.ELETRICO, Tipo.VOADOR));
    }

    // --- Variáveis de Conexão e Estado ---
    private ServerConnection jogador1;
    private ServerConnection jogador2;

    private enum GameState {
        AGUARDANDO_JOGADORES, AGUARDANDO_ESCOLHAS, EM_JOGO, FIM_DE_JOGO
    }
    private GameState estadoJogo;
    private int turnoAtual;
    
    // Guarda os DADOS do Pokémon, não só o nome
    private PokemonData p1Data;
    private PokemonData p2Data;
    
    private boolean j1EhShiny, j2EhShiny;
    private static final Random rng = new Random();

    // Constantes de Regras
    private static final int HP_INICIAL = 80;
    private static final int DANO_BASE_ATAQUE = 12;
    private static final int VALOR_CURA = 20;
    private static final int MAX_CURAS = 3;
    private static final double CHANCE_CRITICO = 0.05;
    private static final double EVASIVA_INICIAL = 0.60;
    private static final double EVASIVA_REDUCAO = 0.10;
    private static final double EVASIVA_MINIMA = 0.05;

    // "Memória" da Batalha
    private int hpJogador1, hpJogador2;
    private int curasRestantesP1, curasRestantesP2;
    private boolean evasivaAtivaP1, evasivaAtivaP2;
    private double chanceEvasivaP1, chanceEvasivaP2;
    private boolean p1UsouEvasivaTurnoPassado, p2UsouEvasivaTurnoPassado;

    private ServerSocket server;
    private int porta;

    public GameServerCore(int porta) throws IOException {
        this.porta = porta;
        this.server = new ServerSocket(porta);
        this.estadoJogo = GameState.AGUARDANDO_JOGADORES;
        System.out.println("[GameServerCore] Servidor (COM TIPAGEM) rodando na porta: " + server.getLocalPort());
    }
    
    @Override
    public void run() {
        while (true) {
            if (estadoJogo == GameState.AGUARDANDO_JOGADORES) {
                try {
                    Socket socket = this.server.accept();
                    ServerConnection novoCliente = new ServerConnection(socket);
                    registrarNovoCliente(novoCliente);
                    (new GameServerHandler(novoCliente, this)).start();
                } catch (IOException ex) {
                    System.out.println("[GameServerCore] Erro ao aceitar conexão: " + ex.getMessage());
                }
            }
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }

    public synchronized void registrarNovoCliente(ServerConnection cliente) {
        if (jogador1 == null) {
            jogador1 = cliente;
            System.out.println("[GameServerCore] Jogador 1 conectou.");
            enviar(jogador1, "LOG:Você é o Jogador 1. Escolha seu Pokémon.");
        } else if (jogador2 == null) {
            jogador2 = cliente;
            System.out.println("[GameServerCore] Jogador 2 conectou.");
            enviar(jogador2, "LOG:Você é o Jogador 2. Escolha seu Pokémon.");
            
            this.estadoJogo = GameState.AGUARDANDO_ESCOLHAS;
            System.out.println("[GameServerCore] Ambos conectados. Aguardando escolhas...");
            
            enviar(jogador1, "OPONENTE_ENCONTRADO");
            enviar(jogador2, "OPONENTE_ENCONTRADO");
        } else {
            enviar(cliente, "LOG:O servidor está cheio. Tente mais tarde.");
            try { cliente.getSocket().close(); } catch (IOException e) {}
        }
    }

    public synchronized void processarAcao(ServerConnection remetente, String mensagem) {
        
        String[] partes = mensagem.split(":", 2);
        String tipo = partes[0].toUpperCase();
        String dados = (partes.length > 1) ? partes[1] : "";

        try {
            // --- ETAPA 1: Processar Escolha de Pokémon ---
            if (estadoJogo == GameState.AGUARDANDO_ESCOLHAS) {
                if (!tipo.equals("ESCOLHEU")) {
                    enviar(remetente, "LOG:Ação inválida. Escolha seu Pokémon primeiro.");
                    return;
                }
                
                String nomePokemon = dados; // dados = "Pikachu", "Treecko", etc.
                PokemonData pokemonEscolhido = POKEMON_DB.get(nomePokemon);
                
                if (pokemonEscolhido == null) {
                    enviar(remetente, "LOG:Pokémon '" + nomePokemon + "' não reconhecido. Tente de novo.");
                    return;
                }
                
                if (remetente == jogador1) {
                    p1Data = pokemonEscolhido;
                    j1EhShiny = rng.nextDouble() < 0.1; 
                    System.out.println("[GameServerCore] J1 escolheu: " + p1Data.nome);
                    enviar(remetente, "LOG:Você escolheu " + p1Data.nome + ". Aguardando oponente...");
                } else {
                    p2Data = pokemonEscolhido;
                    j2EhShiny = rng.nextDouble() < 0.1;
                    System.out.println("[GameServerCore] J2 escolheu: " + p2Data.nome);
                    enviar(remetente, "LOG:Você escolheu " + p2Data.nome + ". Aguardando oponente...");
                }
                
                if (p1Data != null && p2Data != null) {
                    iniciarPartida(); 
                }
                return;
            }
            
            // --- ETAPA 2: Processar Ações de Batalha ---
            if (estadoJogo == GameState.EM_JOGO) {
                String acao = tipo; 
                ServerConnection jogadorAtacante, jogadorAlvo;
                int turnoDoAtacante;

                if (remetente == jogador1) {
                    jogadorAtacante = jogador1;
                    jogadorAlvo = jogador2;
                    turnoDoAtacante = 1;
                } else {
                    jogadorAtacante = jogador2;
                    jogadorAlvo = jogador1;
                    turnoDoAtacante = 2;
                }

                if (turnoAtual != turnoDoAtacante) {
                    enviar(jogadorAtacante, "LOG:Não é a sua vez!");
                    return;
                }
                System.out.println("[GameServerCore] J" + turnoDoAtacante + " usou: " + acao);

                switch (acao) {
                    
                    case "ATACAR":
                        if (turnoDoAtacante == 1) { // P1 ataca P2
                            evasivaAtivaP1 = false; p1UsouEvasivaTurnoPassado = false;
                            boolean oponenteEvitou = false;
                            if (evasivaAtivaP2) { 
                                boolean sucessoEvasiva = rng.nextDouble() < chanceEvasivaP2;
                                if (sucessoEvasiva) {
                                    oponenteEvitou = true;
                                    enviar(jogadorAtacante, "LOG:O oponente evitou seu ataque!");
                                    enviar(jogadorAlvo, "LOG:Você evitou o ataque!");
                                } else {
                                    enviar(jogadorAlvo, "LOG:Sua evasiva falhou!");
                                }
                                evasivaAtivaP2 = false;
                            }
                            if (!oponenteEvitou) {
                                int danoCausado = calcularDano(p1Data, p2Data); // <-- Lógica de tipo
                                hpJogador2 -= danoCausado;
                                if (hpJogador2 < 0) hpJogador2 = 0; 
                                enviar(jogadorAtacante, "LOG:Você atacou! Dano: " + danoCausado);
                                enviar(jogadorAlvo, "LOG:Você foi atingido! Dano: " + danoCausado);
                                enviar(jogadorAtacante, "STATUS_OPONENTE:" + hpJogador2);
                                enviar(jogadorAlvo, "STATUS_JOGADOR:" + hpJogador2);
                            }
                        } else { // P2 ataca P1
                            evasivaAtivaP2 = false; p2UsouEvasivaTurnoPassado = false;
                            boolean oponenteEvitou = false;
                            if (evasivaAtivaP1) {
                                boolean sucessoEvasiva = rng.nextDouble() < chanceEvasivaP1;
                                if (sucessoEvasiva) {
                                    oponenteEvitou = true;
                                    enviar(jogadorAtacante, "LOG:O oponente evitou seu ataque!");
                                    enviar(jogadorAlvo, "LOG:Você evitou o ataque!");
                                } else {
                                    enviar(jogadorAlvo, "LOG:Sua evasiva falhou!");
                                }
                                evasivaAtivaP1 = false; 
                            }
                            if (!oponenteEvitou) {
                                int danoCausado = calcularDano(p2Data, p1Data); // <-- Lógica de tipo
                                hpJogador1 -= danoCausado;
                                if (hpJogador1 < 0) hpJogador1 = 0;
                                enviar(jogadorAtacante, "LOG:Você atacou! Dano: " + danoCausado);
                                enviar(jogadorAlvo, "LOG:Você foi atingido! Dano: " + danoCausado);
                                enviar(jogadorAtacante, "STATUS_OPONENTE:" + hpJogador1);
                                enviar(jogadorAlvo, "STATUS_JOGADOR:" + hpJogador1);
                            }
                        }
                        if (hpJogador1 <= 0 || hpJogador2 <= 0) finalizarPartida();
                        else passarTurno();
                        break;

                    case "CURAR":
                        if (turnoDoAtacante == 1) { // P1 cura
                            if (curasRestantesP1 > 0) {
                                curasRestantesP1--;
                                hpJogador1 += VALOR_CURA;
                                if (hpJogador1 > HP_INICIAL) hpJogador1 = HP_INICIAL;
                                enviar(jogadorAtacante, "LOG:Você se curou! (" + curasRestantesP1 + " restantes)");
                                enviar(jogadorAtacante, "STATUS_JOGADOR:" + hpJogador1);
                                enviar(jogadorAtacante, "STATUS_CURAS:" + curasRestantesP1);
                            } else {
                                enviar(jogadorAtacante, "LOG:Você não tem mais curas!");
                            }
                            p1UsouEvasivaTurnoPassado = false; evasivaAtivaP1 = false;
                        } else { // P2 cura
                           if (curasRestantesP2 > 0) {
                                curasRestantesP2--;
                                hpJogador2 += VALOR_CURA;
                                if (hpJogador2 > HP_INICIAL) hpJogador2 = HP_INICIAL;
                                enviar(jogadorAtacante, "LOG:Você se curou! (" + curasRestantesP2 + " restantes)");
                                enviar(jogadorAtacante, "STATUS_JOGADOR:" + hpJogador2);
                                enviar(jogadorAtacante, "STATUS_CURAS:" + curasRestantesP2);
                            } else {
                                enviar(jogadorAtacante, "LOG:Você não tem mais curas!");
                            }
                            p2UsouEvasivaTurnoPassado = false; evasivaAtivaP2 = false;
                        }
                        passarTurno();
                        break;
                        
                    case "EVASIVA":
                        if (turnoDoAtacante == 1) { // P1 usa evasiva
                            evasivaAtivaP1 = true;
                            if (p1UsouEvasivaTurnoPassado) {
                                chanceEvasivaP1 -= EVASIVA_REDUCAO;
                                if (chanceEvasivaP1 < EVASIVA_MINIMA) chanceEvasivaP1 = EVASIVA_MINIMA;
                            } else {
                                chanceEvasivaP1 = EVASIVA_INICIAL;
                            }
                            p1UsouEvasivaTurnoPassado = true;
                            enviar(jogadorAtacante, "LOG:Posição de evasiva! (Chance: " + (int)(chanceEvasivaP1*100) + "%)");
                        } else { // P2 usa evasiva
                            evasivaAtivaP2 = true;
                            if (p2UsouEvasivaTurnoPassado) {
                                chanceEvasivaP2 -= EVASIVA_REDUCAO;
                                if (chanceEvasivaP2 < EVASIVA_MINIMA) chanceEvasivaP2 = EVASIVA_MINIMA;
                            } else {
                                chanceEvasivaP2 = EVASIVA_INICIAL;
                            }
                            p2UsouEvasivaTurnoPassado = true;
                            enviar(jogadorAtacante, "LOG:Posição de evasiva! (Chance: " + (int)(chanceEvasivaP2*100) + "%)");
                        }
                        passarTurno();
                        break;
                        
                    case "FUGIR": // Você mencionou isso, então adicionei a ação
                        if (turnoDoAtacante == 1) handleFugir(jogador1, jogador2, 1);
                        else handleFugir(jogador2, jogador1, 2);
                        return; // Sair, pois o jogo acabou

                    default:
                        enviar(remetente, "LOG:Ação desconhecida: " + acao);
                        break;
                }
                return;
            }
            
            if (estadoJogo == GameState.FIM_DE_JOGO) {
                 enviar(remetente, "LOG:O jogo já acabou. Escolha um novo Pokémon.");
            }
        } catch (Exception e) {
            System.err.println("[GameServerCore] Erro ao processar ação: " + mensagem + " | Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // --- NOVO MÉTODO: CÁLCULO DE DANO ---
    
    /**
     * Calcula o dano final com base no crítico e na tipagem.
     * (O ataque é sempre do Tipo.FOGO para esta lógica de exemplo)
     */
    private int calcularDano(PokemonData atacante, PokemonData defensor) {
        double dano = DANO_BASE_ATAQUE;
        String logTipo = ""; // Log para o jogador

        // 1. Calcular multiplicador de tipo
        // Para este exemplo, o ATAQUE é sempre do Tipo 1 do atacante.
        Tipo tipoAtaque = atacante.tipo1; 
        
        // Calcula eficácia contra o Tipo 1 e Tipo 2 do defensor
        double multTipo1 = getEficacia(tipoAtaque, defensor.tipo1);
        double multTipo2 = getEficacia(tipoAtaque, defensor.tipo2);
        
        double multiplicadorTotal = multTipo1 * multTipo2;

        if (multiplicadorTotal > 1) logTipo = " (É super efetivo!)";
        if (multiplicadorTotal < 1) logTipo = " (Não é muito efetivo...)";
        
        dano *= multiplicadorTotal;

        // 2. Calcular Crítico (5% de chance)
        boolean critico = rng.nextDouble() < CHANCE_CRITICO;
        if (critico) {
            dano *= 2.0; // Dobra o dano final
            enviar(jogador1, "LOG:Um acerto crítico!");
            enviar(jogador2, "LOG:Um acerto crítico!");
        }
        
        // Envia o log de eficácia (apenas se não for crítico, para não poluir)
        if (!logTipo.isEmpty() && !critico) {
            enviar(jogador1, "LOG:" + logTipo);
            enviar(jogador2, "LOG:" + logTipo);
        }

        return (int) Math.round(dano); // Retorna o dano como inteiro
    }
    
    /**
     * Retorna o multiplicador de eficácia baseado nas suas regras.
     * (Ataque de 'tipoAtacante' contra 'tipoDefensor')
     */
    private double getEficacia(Tipo tipoAtacante, Tipo tipoDefensor) {
        if (tipoDefensor == Tipo.NENHUM) return 1.0; // Sem segundo tipo

        switch (tipoAtacante) {
            case FOGO:
                if (tipoDefensor == Tipo.PLANTA) return 2.0; // Forte
                if (tipoDefensor == Tipo.AGUA) return 0.5;   // Fraco
                // Resistência: Fogo é resistente a Planta
                if (tipoDefensor == Tipo.PLANTA) return 0.5; // (Sua regra: "resistente a Planta")
                break;
            case AGUA:
                if (tipoDefensor == Tipo.FOGO) return 2.0;
                if (tipoDefensor == Tipo.ELETRICO) return 0.5;
                if (tipoDefensor == Tipo.FOGO) return 0.5;
                break;
            case PLANTA:
                if (tipoDefensor == Tipo.AGUA) return 2.0;
                if (tipoDefensor == Tipo.FOGO) return 0.5;
                if (tipoDefensor == Tipo.AGUA) return 0.5;
                break;
            case ELETRICO:
                if (tipoDefensor == Tipo.AGUA) return 2.0;
                if (tipoDefensor == Tipo.TERRA) return 0.5;
                if (tipoDefensor == Tipo.VOADOR) return 0.5;
                break;
            case TERRA:
                if (tipoDefensor == Tipo.ELETRICO || tipoDefensor == Tipo.FOGO) return 2.0;
                if (tipoDefensor == Tipo.PLANTA) return 0.5;
                if (tipoDefensor == Tipo.ELETRICO) return 0.5;
                break;
            case VOADOR:
                if (tipoDefensor == Tipo.PLANTA) return 2.0;
                if (tipoDefensor == Tipo.ELETRICO) return 0.5;
                if (tipoDefensor == Tipo.PLANTA) return 0.5;
                break;
        }
        
        // Nenhuma regra se aplica, dano normal
        return 1.0;
    }
    
    // --- Métodos de Controle de Jogo (O resto) ---
    
    private void iniciarPartida() {
        this.hpJogador1 = HP_INICIAL;
        this.hpJogador2 = HP_INICIAL;
        this.curasRestantesP1 = MAX_CURAS;
        this.curasRestantesP2 = MAX_CURAS;
        this.evasivaAtivaP1 = false; this.evasivaAtivaP2 = false;
        this.chanceEvasivaP1 = EVASIVA_INICIAL; this.chanceEvasivaP2 = EVASIVA_INICIAL;
        this.p1UsouEvasivaTurnoPassado = false; this.p2UsouEvasivaTurnoPassado = false;
        this.turnoAtual = 1; 
        this.estadoJogo = GameState.EM_JOGO;
        
        System.out.println("[GameServerCore] --- PARTIDA INICIADA ---");
        
        // Protocolo: "INICIAR_PARTIDA:SeuPokemon:SeuShiny:PokemonOponente:OponenteShiny:HP:Curas"
        String p1Msg = String.format("INICIAR_PARTIDA:%s:%b:%s:%b:%d:%d",
            p1Data.nome, j1EhShiny, p2Data.nome, j2EhShiny, HP_INICIAL, MAX_CURAS);
        enviar(jogador1, p1Msg);
            
        String p2Msg = String.format("INICIAR_PARTIDA:%s:%b:%s:%b:%d:%d",
            p2Data.nome, j2EhShiny, p1Data.nome, j1EhShiny, HP_INICIAL, MAX_CURAS);
        enviar(jogador2, p2Msg);
        
        enviar(jogador1, "SEU_TURNO");
        enviar(jogador1, "LOG:É a sua vez!");
        enviar(jogador2, "ESPERE_TURNO");
        enviar(jogador2, "LOG:É a vez do Oponente.");
    }
    
    private void passarTurno() {
        if (turnoAtual == 1) {
            turnoAtual = 2;
            enviar(jogador1, "ESPERE_TURNO");
            enviar(jogador1, "LOG:É a vez do Oponente.");
            enviar(jogador2, "SEU_TURNO");
            enviar(jogador2, "LOG:É a sua vez!");
        } else {
            turnoAtual = 1;
            enviar(jogador2, "ESPERE_TURNO");
            enviar(jogador2, "LOG:É a vez do Oponente.");
            enviar(jogador1, "SEU_TURNO");
            enviar(jogador1, "LOG:É a sua vez!");
        }
        System.out.println("[GameServerCore] --- Turno do Jogador " + turnoAtual + " ---");
    }

    private void finalizarPartida() {
        this.estadoJogo = GameState.FIM_DE_JOGO;
        String vencedor = (hpJogador1 <= 0) ? "Jogador 2" : "Jogador 1";
        System.out.println("[GameServerCore] --- FIM DE JOGO: " + vencedor + " venceu! ---");
        broadcast("FIM_DE_JOGO:" + vencedor);
        resetarServidor();
    }
    
    private void handleFugir(ServerConnection fujão, ServerConnection oponente, int numJogador) {
        this.estadoJogo = GameState.FIM_DE_JOGO;
        String vencedor = (numJogador == 1) ? "Jogador 2" : "Jogador 1";
        
        enviar(fujão, "LOG:Você fugiu da batalha!");
        enviar(oponente, "LOG:O oponente fugiu! Você venceu!");
        broadcast("FIM_DE_JOGO:" + vencedor);
        resetarServidor();
    }
    
    private void resetarServidor() {
        this.p1Data = null; this.p2Data = null;
        this.hpJogador1 = 0; this.hpJogador2 = 0;
        
        if (jogador1 != null && jogador2 != null) {
            this.estadoJogo = GameState.AGUARDANDO_ESCOLHAS;
            System.out.println("[GameServerCore] Jogo resetado. Aguardando novas escolhas...");
            broadcast("LOG:Nova partida. Escolham seus Pokémon.");
        } else {
            this.estadoJogo = GameState.AGUARDANDO_JOGADORES;
            System.out.println("[GameServerCore] Jogo resetado. Aguardando jogadores...");
        }
    }
    
    public synchronized void removerCliente(ServerConnection cliente) {
        boolean eraJogoEmAndamento = (estadoJogo == GameState.EM_JOGO || estadoJogo == GameState.AGUARDANDO_ESCOLHAS);
        
        if (cliente == jogador1) {
            jogador1 = null;
            System.out.println("[GameServerCore] Jogador 1 desconectou.");
            if (eraJogoEmAndamento && jogador2 != null) {
                enviar(jogador2, "LOG:O Jogador 1 desconectou. Você venceu!");
                handleFugir(jogador1, jogador2, 1);
            }
        } else if (cliente == jogador2) {
            jogador2 = null;
            System.out.println("[GameServerCore] Jogador 2 desconectou.");
            if (eraJogoEmAndamento && jogador1 != null) {
                enviar(jogador1, "LOG:O Jogador 2 desconectou. Você venceu!");
                handleFugir(jogador2, jogador1, 2);
            }
        }
        
        if (eraJogoEmAndamento && (jogador1 == null || jogador2 == null)) {
            this.estadoJogo = GameState.AGUARDANDO_JOGADORES;
            System.out.println("[GameServerCore] Jogo interrompido. Aguardando novos jogadores...");
            resetarServidor();
        }

        try { cliente.getInput().close(); } catch (IOException ex) { /* ignora */ }
        cliente.getOutput().close();
        try { cliente.getSocket().close(); } catch (IOException ex) { /* ignora */ }
    }
    
    // --- Funções 'Helper' de Envio ---
    
    private void enviar(ServerConnection conn, String mensagem) {
        if (conn == null || conn.getSocket().isClosed()) return;
        conn.getOutput().println(mensagem);
    }
    
    private void broadcast(String mensagem) {
        enviar(jogador1, mensagem);
        enviar(jogador2, mensagem);
    }
}