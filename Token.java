/**
 * Classe que representa um token da linguagem C-
 */
public class Token {
    public enum Tipo {
        // Palavras-chave
        INT, VOID, IF, ELSE, WHILE, RETURN,
        
        // Identificadores e constantes
        IDENT, CONTINT,
        
        // Operadores
        OPRELACIONAL, // >, <, <=, >=, ==, !=
        OPADITIVO,    // +, -
        OPMULT,       // *, /
        ATRIBUICAO,   // =
        
        // Delimitadores
        PONTO_VIRGULA,    // ;
        VIRGULA,          // ,
        ABRE_PARENTESES,  // (
        FECHA_PARENTESES, // )
        ABRE_COLCHETES,   // [
        FECHA_COLCHETES,  // ]
        ABRE_CHAVES,      // {
        FECHA_CHAVES,     // }
        
        // Fim de arquivo
        EOF
    }
    
    private Tipo tipo;
    private String valor;
    private int linha;
    private int coluna;
    
    public Token(Tipo tipo, String valor, int linha, int coluna) {
        this.tipo = tipo;
        this.valor = valor;
        this.linha = linha;
        this.coluna = coluna;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public String getValor() {
        return valor;
    }
    
    public int getLinha() {
        return linha;
    }
    
    public int getColuna() {
        return coluna;
    }
    
    @Override
    public String toString() {
        return String.format("Token(%s, '%s', linha: %d, coluna: %d)", 
                            tipo, valor, linha, coluna);
    }
}


