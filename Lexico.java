import java.util.List;
import java.util.ArrayList;

/**
 * Analisador Léxico para a linguagem C-
 * Converte código-fonte em uma lista de tokens
 */
public class Lexico {
    private String codigo;
    private int posicao;
    private int linha;
    private int coluna;
    private List<Token> tokens;
    
    public Lexico(String codigo) {
        this.codigo = codigo;
        this.posicao = 0;
        this.linha = 1;
        this.coluna = 1;
        this.tokens = new ArrayList<>();
    }
    
    /**
     * Analisa o código-fonte e retorna a lista de tokens
     */
    public List<Token> analisar() {
        tokens.clear();
        
        while (posicao < codigo.length()) {
            pularEspacos();
            
            if (posicao >= codigo.length()) {
                break;
            }
            
            char atual = codigo.charAt(posicao);
            
            // Identificadores e palavras-chave
            if (Character.isLetter(atual) || atual == '_') {
                processarIdentificador();
            }
            // Números
            else if (Character.isDigit(atual)) {
                processarNumero();
            }
            // Operadores e delimitadores
            else {
                processarSimbolo();
            }
        }
        
        // Adiciona token EOF
        tokens.add(new Token(Token.Tipo.EOF, "", linha, coluna));
        
        return tokens;
    }
    
    /**
     * Processa identificadores e palavras-chave
     */
    private void processarIdentificador() {
        int inicio = posicao;
        int colInicio = coluna;
        
        while (posicao < codigo.length() && 
               (Character.isLetterOrDigit(codigo.charAt(posicao)) || 
                codigo.charAt(posicao) == '_')) {
            avancar();
        }
        
        String valor = codigo.substring(inicio, posicao);
        Token.Tipo tipo = identificarPalavraChave(valor);
        
        if (tipo == null) {
            tipo = Token.Tipo.IDENT;
        }
        
        tokens.add(new Token(tipo, valor, linha, colInicio));
    }
    
    /**
     * Identifica se uma string é uma palavra-chave
     */
    private Token.Tipo identificarPalavraChave(String palavra) {
        switch (palavra) {
            case "int": return Token.Tipo.INT;
            case "void": return Token.Tipo.VOID;
            case "if": return Token.Tipo.IF;
            case "else": return Token.Tipo.ELSE;
            case "while": return Token.Tipo.WHILE;
            case "return": return Token.Tipo.RETURN;
            default: return null;
        }
    }
    
    /**
     * Processa números inteiros
     */
    private void processarNumero() {
        int inicio = posicao;
        int colInicio = coluna;
        
        while (posicao < codigo.length() && Character.isDigit(codigo.charAt(posicao))) {
            avancar();
        }
        
        String valor = codigo.substring(inicio, posicao);
        tokens.add(new Token(Token.Tipo.CONTINT, valor, linha, colInicio));
    }
    
    /**
     * Processa símbolos (operadores e delimitadores)
     */
    private void processarSimbolo() {
        int colInicio = coluna;
        char atual = codigo.charAt(posicao);
        
        switch (atual) {
            case ';':
                tokens.add(new Token(Token.Tipo.PONTO_VIRGULA, ";", linha, colInicio));
                avancar();
                break;
                
            case ',':
                tokens.add(new Token(Token.Tipo.VIRGULA, ",", linha, colInicio));
                avancar();
                break;
                
            case '(':
                tokens.add(new Token(Token.Tipo.ABRE_PARENTESES, "(", linha, colInicio));
                avancar();
                break;
                
            case ')':
                tokens.add(new Token(Token.Tipo.FECHA_PARENTESES, ")", linha, colInicio));
                avancar();
                break;
                
            case '[':
                tokens.add(new Token(Token.Tipo.ABRE_COLCHETES, "[", linha, colInicio));
                avancar();
                break;
                
            case ']':
                tokens.add(new Token(Token.Tipo.FECHA_COLCHETES, "]", linha, colInicio));
                avancar();
                break;
                
            case '{':
                tokens.add(new Token(Token.Tipo.ABRE_CHAVES, "{", linha, colInicio));
                avancar();
                break;
                
            case '}':
                tokens.add(new Token(Token.Tipo.FECHA_CHAVES, "}", linha, colInicio));
                avancar();
                break;
                
            case '=':
                if (proximoChar() == '=') {
                    tokens.add(new Token(Token.Tipo.OPRELACIONAL, "==", linha, colInicio));
                    avancar(); // consome o segundo '='
                    avancar();
                } else {
                    tokens.add(new Token(Token.Tipo.ATRIBUICAO, "=", linha, colInicio));
                    avancar();
                }
                break;
                
            case '!':
                if (proximoChar() == '=') {
                    tokens.add(new Token(Token.Tipo.OPRELACIONAL, "!=", linha, colInicio));
                    avancar(); // consome o '='
                    avancar();
                } else {
                    erro("Caractere inesperado: !");
                    avancar();
                }
                break;
                
            case '<':
                if (proximoChar() == '=') {
                    tokens.add(new Token(Token.Tipo.OPRELACIONAL, "<=", linha, colInicio));
                    avancar(); // consome o '='
                    avancar();
                } else {
                    tokens.add(new Token(Token.Tipo.OPRELACIONAL, "<", linha, colInicio));
                    avancar();
                }
                break;
                
            case '>':
                if (proximoChar() == '=') {
                    tokens.add(new Token(Token.Tipo.OPRELACIONAL, ">=", linha, colInicio));
                    avancar(); // consome o '='
                    avancar();
                } else {
                    tokens.add(new Token(Token.Tipo.OPRELACIONAL, ">", linha, colInicio));
                    avancar();
                }
                break;
                
            case '+':
                tokens.add(new Token(Token.Tipo.OPADITIVO, "+", linha, colInicio));
                avancar();
                break;
                
            case '-':
                tokens.add(new Token(Token.Tipo.OPADITIVO, "-", linha, colInicio));
                avancar();
                break;
                
            case '*':
                tokens.add(new Token(Token.Tipo.OPMULT, "*", linha, colInicio));
                avancar();
                break;
                
            case '/':
                if (proximoChar() == '/') {
                    // Comentário de linha: ignora até o fim da linha
                    while (posicao < codigo.length() && codigo.charAt(posicao) != '\n') {
                        avancar();
                    }
                } else if (proximoChar() == '*') {
                    // Comentário de bloco: ignora até */
                    avancar(); // consome o '*'
                    avancar(); // consome o '/'
                    while (posicao < codigo.length()) {
                        if (codigo.charAt(posicao) == '*' && 
                            posicao + 1 < codigo.length() && 
                            codigo.charAt(posicao + 1) == '/') {
                            avancar(); // consome o '*'
                            avancar(); // consome o '/'
                            break;
                        }
                        avancar();
                    }
                } else {
                    tokens.add(new Token(Token.Tipo.OPMULT, "/", linha, colInicio));
                    avancar();
                }
                break;
                
            default:
                erro("Caractere inesperado: " + atual);
                avancar();
                break;
        }
    }
    
    /**
     * Retorna o próximo caractere sem consumir
     */
    private char proximoChar() {
        if (posicao + 1 < codigo.length()) {
            return codigo.charAt(posicao + 1);
        }
        return '\0';
    }
    
    /**
     * Avança a posição atual
     */
    private void avancar() {
        if (posicao < codigo.length()) {
            if (codigo.charAt(posicao) == '\n') {
                linha++;
                coluna = 1;
            } else {
                coluna++;
            }
            posicao++;
        }
    }
    
    /**
     * Pula espaços em branco
     */
    private void pularEspacos() {
        while (posicao < codigo.length() && 
               Character.isWhitespace(codigo.charAt(posicao))) {
            avancar();
        }
    }
    
    /**
     * Registra um erro (pode ser melhorado com tratamento de erros)
     */
    private void erro(String mensagem) {
        System.err.println("Erro léxico na linha " + linha + ", coluna " + coluna + ": " + mensagem);
    }
}


