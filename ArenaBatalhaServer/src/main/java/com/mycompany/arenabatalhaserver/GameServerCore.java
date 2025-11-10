package com.mycompany.arenabatalhaserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random; // NOVO: Para calcular o shiny

public class GameServerCore extends Thread {

    private ServerConnection jogador1;
    private ServerConnection jogador2;

    // Estado do jogo
    private int hpJogador1;
    private int hpJogador2;
    private int turnoAtual;
    
    // NOVO: Variáveis para a fase de seleção
    private String escolhaJogador1;
    private String escolhaJogador2;
    private boolean j1EhShiny;
    private boolean j2EhShiny;
    private static final Random rng = new Random(); // Gerador de números aleatórios

    // MUDANÇA: O Enum de estado agora tem mais etapas
    private enum GameState {
        AGUARDANDO_JOGADORES, // Esperando 2 conexões
        AGUARDANDO_ESCOLHAS,  // Os 2 estão conectados, esperando msg "ESCOLHEU:..."
        EM_JOGO,
        FIM_DE_JOGO
    }
    private GameState estadoJogo;

    private ServerSocket server;
    private int porta;

    public GameServerCore(int porta) throws IOException {
        this.porta = porta;
        this.server = new ServerSocket(porta);
        // MUDANÇA: O estado inicial é AGUARDANDO_JOGADORES
        this.estadoJogo = GameState.AGUARDANDO_JOGADORES; 
        
        System.out.println("[GameServerCore] Servidor de Batalha rodando na porta: " + server.getLocalPort());
        System.out.println("[GameServerCore] Aguardando 2 jogadores...");
    }

    @Override
    public void run() {
        Socket socket;
        while (true) {
            // MUDANÇA: Agora só aceita conexões se estiver AGUARDANDO_JOGADORES
            if (estadoJogo == GameState.AGUARDANDO_JOGADORES) {
                try {
                    socket = this.server.accept();
                    ServerConnection novoCliente = new ServerConnection(socket);
                    registrarNovoCliente(novoCliente); // Tenta alocar o cliente
                    
                    // Cria e inicia o Handler (o "Ouvinte") para este cliente
                    (new GameServerHandler(novoCliente, this)).start();
                    
                } catch (IOException ex) {
                    System.out.println("[GameServerCore] Erro ao aceitar conexão: " + ex.getMessage());
                }
            }
            
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }

    /**
     * Tenta registrar um novo cliente como Jogador 1 ou Jogador 2.
     */
    public synchronized void registrarNovoCliente(ServerConnection cliente) {
        if (jogador1 == null) {
            jogador1 = cliente;
            System.out.println("[GameServerCore] Jogador 1 conectou: " + cliente.getSocket().getInetAddress());
            jogador1.getOutput().println("LOG:Você é o Jogador 1. Escolha seu Pokémon.");
            
        } else if (jogador2 == null) {
            jogador2 = cliente;
            System.out.println("[GameServerCore] Jogador 2 conectou: " + cliente.getSocket().getInetAddress());
            jogador2.getOutput().println("LOG:Você é o Jogador 2. Escolha seu Pokémon.");
            
            // MUDANÇA: Os dois jogadores estão conectados. Muda o estado.
            this.estadoJogo = GameState.AGUARDANDO_ESCOLHAS;
            System.out.println("[GameServerCore] Ambos conectados. Aguardando escolhas...");
            
            // Avisa ambos que o oponente foi encontrado
            jogador1.getOutput().println("OPONENTE_ENCONTRADO");
            jogador2.getOutput().println("OPONENTE_ENCONTRADO");
            
        } else {
            // Jogo já está cheio
            System.out.println("[GameServerCore] Conexão recusada: Jogo em andamento.");
            cliente.getOutput().println("LOG:O servidor está cheio. Tente mais tarde.");
            try {
                cliente.getSocket().close();
            } catch (IOException e) { /* ignora */ }
        }
    }
    
    /**
     * O CÉREBRO. Esta é a função que o Handler vai chamar.
     */
    public synchronized void processarAcao(ServerConnection remetente, String mensagem) {
        
        // --- ETAPA 1: Processar Escolha de Pokémon ---
        if (estadoJogo == GameState.AGUARDANDO_ESCOLHAS) {
            if (!mensagem.startsWith("ESCOLHEU:")) {
                remetente.getOutput().println("LOG:Ação inválida. Escolha seu Pokémon primeiro.");
                return;
            }
            
            // Ex: "ESCOLHEU:Pikachu"
            String pokemon = mensagem.split(":")[1];
            
            if (remetente == jogador1) {
                escolhaJogador1 = pokemon;
                j1EhShiny = rng.nextDouble() < 0.1; // 10% de chance de ser shiny
                System.out.println("[GameServerCore] Jogador 1 escolheu: " + pokemon + (j1EhShiny ? " (Shiny!)" : ""));
                jogador1.getOutput().println("LOG:Você escolheu " + pokemon + ". Aguardando oponente...");
            } else {
                escolhaJogador2 = pokemon;
                j2EhShiny = rng.nextDouble() < 0.1;
                System.out.println("[GameServerCore] Jogador 2 escolheu: " + pokemon + (j2EhShiny ? " (Shiny!)" : ""));
                jogador2.getOutput().println("LOG:Você escolheu " + pokemon + ". Aguardando oponente...");
            }
            
            // Verifica se AMBOS já escolheram
            if (escolhaJogador1 != null && escolhaJogador2 != null) {
                iniciarPartida(); // Inicia a partida!
            }
            return; // Sai da função após processar a escolha
        }
        
        // --- ETAPA 2: Processar Ações de Batalha ---
        if (estadoJogo == GameState.EM_JOGO) {
            
            // Identifica quem está jogando
            ServerConnection jogadorAtacante;
            ServerConnection jogadorAlvo;
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

            // Verifica o turno
            if (turnoAtual != turnoDoAtacante) {
                jogadorAtacante.getOutput().println("LOG:Não é a sua vez!");
                return;
            }
            
            System.out.println("[GameServerCore] Jogador " + turnoDoAtacante + " usou: " + mensagem);

            // Processa a ação de batalha
            if (mensagem.equals("ATACAR")) {
                int dano = 15; // Dano fixo (por enquanto)
                
                if (turnoDoAtacante == 1) {
                    hpJogador2 -= dano;
                    jogadorAtacante.getOutput().println("LOG:Você atacou o Oponente! Dano: " + dano);
                    jogadorAtacante.getOutput().println("DANO:OPONENTE:" + hpJogador2);
                    jogadorAlvo.getOutput().println("LOG:Você foi atingido! Dano: " + dano);
                    jogadorAlvo.getOutput().println("DANO:JOGADOR:" + hpJogador2);
                } else {
                    hpJogador1 -= dano;
                    jogadorAtacante.getOutput().println("LOG:Você atacou o Oponente! Dano: " + dano);
                    jogadorAtacante.getOutput().println("DANO:OPONENTE:" + hpJogador1);
                    jogadorAlvo.getOutput().println("LOG:Você foi atingido! Dano: " + dano);
                    jogadorAlvo.getOutput().println("DANO:JOGADOR:" + hpJogador1);
                }
                
                if (hpJogador1 <= 0 || hpJogador2 <= 0) {
                    finalizarPartida();
                } else {
                    passarTurno();
                }
            } else if (mensagem.equals("CURAR")) {
                // Lógica de cura...
                jogadorAtacante.getOutput().println("LOG:Você usou Curar!");
                // (não esqueça de passarTurno() depois)
                passarTurno();
            } else if (mensagem.equals("EVASIVA")) {
                // Lógica de evasiva...
                jogadorAtacante.getOutput().println("LOG:Você usou Evasiva!");
                passarTurno();
            }
            return; // Sai da função após processar a batalha
        }
        
        // --- ETAPA 3: Jogo ainda não começou ou já acabou ---
        if (estadoJogo == GameState.FIM_DE_JOGO) {
             remetente.getOutput().println("LOG:O jogo já acabou.");
        }
    }
    
    /**
     * MUDANÇA: Agora é chamado DEPOIS das escolhas.
     * Prepara o estado inicial do jogo e avisa os jogadores.
     */
    private void iniciarPartida() {
        this.hpJogador1 = 80;
        this.hpJogador2 = 80;
        this.turnoAtual = 1; // Jogador 1 começa
        this.estadoJogo = GameState.EM_JOGO;
        
        System.out.println("[GameServerCore] --- PARTIDA INICIADA ---");
        
        // NOVO: Envia os dados da partida para os clientes
        // Protocolo: "INICIAR_PARTIDA:SeuPokemon:SeuShiny:PokemonOponente:OponenteShiny"
        
        jogador1.getOutput().println(String.format("INICIAR_PARTIDA:%s:%b:%s:%b",
            escolhaJogador1, j1EhShiny, escolhaJogador2, j2EhShiny));
            
        jogador2.getOutput().println(String.format("INICIAR_PARTIDA:%s:%b:%s:%b",
            escolhaJogador2, j2EhShiny, escolhaJogador1, j1EhShiny));
        
        // Avisa quem começa
        jogador1.getOutput().println("SEU_TURNO");
        jogador1.getOutput().println("LOG:É a sua vez!");
        
        jogador2.getOutput().println("ESPERE_TURNO");
        jogador2.getOutput().println("LOG:É a vez do Oponente.");
    }
    
    private void passarTurno() {
        if (turnoAtual == 1) {
            turnoAtual = 2;
            jogador1.getOutput().println("ESPERE_TURNO");
            jogador1.getOutput().println("LOG:É a vez do Oponente.");
            jogador2.getOutput().println("SEU_TURNO");
            jogador2.getOutput().println("LOG:É a sua vez!");
        } else {
            turnoAtual = 1;
            jogador2.getOutput().println("ESPERE_TURNO");
            jogador2.getOutput().println("LOG:É a vez do Oponente.");
            jogador1.getOutput().println("SEU_TURNO");
            jogador1.getOutput().println("LOG:É a sua vez!");
        }
        System.out.println("[GameServerCore] --- Turno do Jogador " + turnoAtual + " ---");
    }

    private void finalizarPartida() {
        this.estadoJogo = GameState.FIM_DE_JOGO;
        String vencedor = (hpJogador1 <= 0) ? "Jogador 2" : "Jogador 1";
        
        String msg = "FIM_DE_JOGO:O " + vencedor + " venceu!";
        System.out.println("[GameServerCore] --- " + msg + " ---");
        
        broadcast(msg);
        
        // MUDANÇA: Prepara o servidor para uma nova partida
        resetarServidor();
    }
    
    /**
     * MUDANÇA: Reseta o estado do jogo para AGUARDANDO_JOGADORES
     * (mas mantém os jogadores conectados, eles precisam escolher de novo)
     */
    private void resetarServidor() {
        this.escolhaJogador1 = null;
        this.escolhaJogador2 = null;
        this.hpJogador1 = 0;
        this.hpJogador2 = 0;
        
        // Se ambos ainda estiverem conectados, eles vão para a tela de seleção
        if (jogador1 != null && jogador2 != null) {
            this.estadoJogo = GameState.AGUARDANDO_ESCOLHAS;
            System.out.println("[GameServerCore] Jogo resetado. Aguardando novas escolhas...");
            broadcast("LOG:Nova partida. Escolham seus Pokémon.");
        } else {
            // Se alguém desconectou, espera novos jogadores
            this.estadoJogo = GameState.AGUARDANDO_JOGADORES;
            System.out.println("[GameServerCore] Jogo resetado. Aguardando jogadores...");
        }
    }

    public void broadcast(String mensagem) {
        if (jogador1 != null && jogador1.getSocket().isConnected()) {
            jogador1.getOutput().println(mensagem);
        }
        if (jogador2 != null && jogador2.getSocket().isConnected()) {
            jogador2.getOutput().println(mensagem);
        }
    }

    public synchronized void removerCliente(ServerConnection cliente) {
        boolean eraJogoEmAndamento = (estadoJogo == GameState.EM_JOGO || estadoJogo == GameState.AGUARDANDO_ESCOLHAS);
        
        if (cliente == jogador1) {
            jogador1 = null;
            System.out.println("[GameServerCore] Jogador 1 desconectou.");
            if (eraJogoEmAndamento && jogador2 != null) {
                jogador2.getOutput().println("LOG:O Jogador 1 desconectou. Você venceu!");
            }
        } else if (cliente == jogador2) {
            jogador2 = null;
            System.out.println("[GameServerCore] Jogador 2 desconectou.");
            if (eraJogoEmAndamento && jogador1 != null) {
                jogador1.getOutput().println("LOG:O Jogador 2 desconectou. Você venceu!");
            }
        }
        
        // Se um jogador saiu no meio da partida, o jogo acaba e volta a esperar
        if (eraJogoEmAndamento) {
            this.estadoJogo = GameState.AGUARDANDO_JOGADORES; // Volta a esperar por 2
            this.escolhaJogador1 = null;
            this.escolhaJogador2 = null;
            System.out.println("[GameServerCore] Jogo interrompido. Aguardando novos jogadores...");
        }

        // Fecha conexões do cliente
        try { cliente.getInput().close(); } catch (IOException ex) { /* ignora */ }
        cliente.getOutput().close();
        try { cliente.getSocket().close(); } catch (IOException ex) { /* ignora */ }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.server.close();
    }
}