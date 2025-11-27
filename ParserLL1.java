import java.util.List;
import java.util.ArrayList;

/**
 * Parser LL(1) para a linguagem C- (gramática refatorada - Parte 2)
 * Implementa análise sintática top-down recursiva
 */
public class ParserLL1 {
    private List<Token> tokens;
    private int posicaoAtual;
    private List<String> erros;
    
    /**
     * Construtor do parser
     * @param tokens Lista de tokens gerados pelo analisador léxico
     */
    public ParserLL1(List<Token> tokens) {
        this.tokens = tokens;
        this.posicaoAtual = 0;
        this.erros = new ArrayList<>();
    }
    
    /**
     * Inicia o parsing do programa
     * @return true se o parsing foi bem-sucedido, false caso contrário
     */
    public boolean parse() {
        try {
            programa();
            if (posicaoAtual < tokens.size() - 1) {
                erro("Tokens adicionais após o fim do programa");
                return false;
            }
            return erros.isEmpty();
        } catch (Exception e) {
            erro("Erro durante o parsing: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retorna a lista de erros encontrados
     */
    public List<String> getErros() {
        return erros;
    }
    
    // ========== Métodos auxiliares ==========
    
    /**
     * Retorna o token atual sem consumi-lo
     */
    private Token lookahead() {
        if (posicaoAtual >= tokens.size()) {
            return new Token(Token.Tipo.EOF, "", 0, 0);
        }
        return tokens.get(posicaoAtual);
    }
    
    /**
     * Consome o token atual e avança para o próximo
     */
    private Token consume() {
        if (posicaoAtual >= tokens.size()) {
            return new Token(Token.Tipo.EOF, "", 0, 0);
        }
        return tokens.get(posicaoAtual++);
    }
    
    /**
     * Verifica se o token atual é do tipo esperado e o consome
     */
    private boolean match(Token.Tipo tipoEsperado) {
        Token atual = lookahead();
        if (atual.getTipo() == tipoEsperado) {
            consume();
            return true;
        }
        return false;
    }
    
    /**
     * Verifica se o token atual é do tipo esperado (sem consumir)
     */
    private boolean check(Token.Tipo tipoEsperado) {
        return lookahead().getTipo() == tipoEsperado;
    }
    
    /**
     * Registra um erro de sintaxe
     */
    private void erro(String mensagem) {
        Token atual = lookahead();
        erros.add(String.format("Erro na linha %d, coluna %d: %s. Token encontrado: %s", 
                               atual.getLinha(), atual.getColuna(), mensagem, atual));
    }
    
    /**
     * Consome um token esperado ou registra erro
     */
    private void esperar(Token.Tipo tipoEsperado, String descricao) {
        if (!match(tipoEsperado)) {
            erro("Esperado " + descricao);
        }
    }
    
    // ========== Regras da gramática ==========
    
    /**
     * <programa> → <declaraçõeslista>
     */
    private void programa() {
        declaracoesLista();
    }
    
    /**
     * <declaraçõeslista> → <declarações> <declaraçõeslista'>
     */
    private void declaracoesLista() {
        declaracoes();
        declaracoesListaLinha();
    }
    
    /**
     * <declaraçõeslista'> → <declarações> <declaraçõeslista'> | ε
     */
    private void declaracoesListaLinha() {
        // Verifica se há mais declarações (FIRST de <declarações> = {int, void})
        if (check(Token.Tipo.INT) || check(Token.Tipo.VOID)) {
            declaracoes();
            declaracoesListaLinha();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <declarações> → <tipo> ident <declarações'>
     */
    private void declaracoes() {
        tipo();
        esperar(Token.Tipo.IDENT, "identificador");
        declaracoesLinha();
    }
    
    /**
     * <declarações'> → ; | [contint]; | (<parformais>) <declcomposto>
     */
    private void declaracoesLinha() {
        if (match(Token.Tipo.PONTO_VIRGULA)) {
            // Caso: ;
            return;
        } else if (match(Token.Tipo.ABRE_COLCHETES)) {
            // Caso: [contint];
            esperar(Token.Tipo.CONTINT, "constante inteira");
            esperar(Token.Tipo.FECHA_COLCHETES, "]");
            esperar(Token.Tipo.PONTO_VIRGULA, ";");
        } else if (match(Token.Tipo.ABRE_PARENTESES)) {
            // Caso: (<parformais>) <declcomposto>
            parFormais();
            esperar(Token.Tipo.FECHA_PARENTESES, ")");
            declComposto();
        } else {
            erro("Esperado ';', '[' ou '(' após identificador");
        }
    }
    
    /**
     * <declaraçãovar> → <tipo> ident <declaraçãovar'>
     */
    private void declaracaoVar() {
        tipo();
        esperar(Token.Tipo.IDENT, "identificador");
        declaracaoVarLinha();
    }
    
    /**
     * <declaraçãovar'> → ; | [contint];
     */
    private void declaracaoVarLinha() {
        if (match(Token.Tipo.PONTO_VIRGULA)) {
            // Caso: ;
            return;
        } else if (match(Token.Tipo.ABRE_COLCHETES)) {
            // Caso: [contint];
            esperar(Token.Tipo.CONTINT, "constante inteira");
            esperar(Token.Tipo.FECHA_COLCHETES, "]");
            esperar(Token.Tipo.PONTO_VIRGULA, ";");
        } else {
            erro("Esperado ';' ou '[' após identificador");
        }
    }
    
    /**
     * <tipo> → int | void
     */
    private void tipo() {
        if (!match(Token.Tipo.INT) && !match(Token.Tipo.VOID)) {
            erro("Esperado 'int' ou 'void'");
        }
    }
    
    /**
     * <parformais> → <listaparformais> | ε
     */
    private void parFormais() {
        // FIRST de <listaparformais> = {int, void}
        if (check(Token.Tipo.INT) || check(Token.Tipo.VOID)) {
            listaParFormais();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <listaparformais> → <parametro> <listaparformais'>
     */
    private void listaParFormais() {
        parametro();
        listaParFormaisLinha();
    }
    
    /**
     * <listaparformais'> → , <listaparformais> | ε
     */
    private void listaParFormaisLinha() {
        if (match(Token.Tipo.VIRGULA)) {
            listaParFormais();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <parametro> → <tipo> ident <parametro'>
     */
    private void parametro() {
        tipo();
        esperar(Token.Tipo.IDENT, "identificador");
        parametroLinha();
    }
    
    /**
     * <parametro'> → [] | ε
     */
    private void parametroLinha() {
        if (match(Token.Tipo.ABRE_COLCHETES)) {
            esperar(Token.Tipo.FECHA_COLCHETES, "]");
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <declcomposto> → { <declaraçõeslocais> <listadecomandos> }
     */
    private void declComposto() {
        esperar(Token.Tipo.ABRE_CHAVES, "{");
        declaracoesLocais();
        listaDeComandos();
        esperar(Token.Tipo.FECHA_CHAVES, "}");
    }
    
    /**
     * <declaraçõeslocais> → <declaraçãovar> <declaraçõeslocais> | ε
     */
    private void declaracoesLocais() {
        // FIRST de <declaraçãovar> = {int, void}
        while (check(Token.Tipo.INT) || check(Token.Tipo.VOID)) {
            declaracaoVar();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <listadecomandos> → <comando> <listadecomandos> | ε
     */
    private void listaDeComandos() {
        // FIRST de <comando> = {ident, contint, (, ;, {, if, while, return}
        while (ehFirstDeComando()) {
            comando();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * Verifica se o token atual é FIRST de <comando>
     */
    private boolean ehFirstDeComando() {
        Token.Tipo tipo = lookahead().getTipo();
        return tipo == Token.Tipo.IDENT || 
               tipo == Token.Tipo.CONTINT ||
               tipo == Token.Tipo.ABRE_PARENTESES ||
               tipo == Token.Tipo.PONTO_VIRGULA ||
               tipo == Token.Tipo.ABRE_CHAVES ||
               tipo == Token.Tipo.IF ||
               tipo == Token.Tipo.WHILE ||
               tipo == Token.Tipo.RETURN;
    }
    
    /**
     * <comando> → <comandoexpressão> | <comandocomposto> | <comandoseleção> | <comandoiteração> | <comandoretorno>
     */
    private void comando() {
        Token.Tipo tipo = lookahead().getTipo();
        
        if (tipo == Token.Tipo.ABRE_CHAVES) {
            comandoComposto();
        } else if (tipo == Token.Tipo.IF) {
            comandoSelecao();
        } else if (tipo == Token.Tipo.WHILE) {
            comandoIteracao();
        } else if (tipo == Token.Tipo.RETURN) {
            comandoRetorno();
        } else {
            // <comandoexpressão> → <expressão>; | ;
            comandoExpressao();
        }
    }
    
    /**
     * <comandoexpressão> → <expressão>; | ;
     */
    private void comandoExpressao() {
        if (match(Token.Tipo.PONTO_VIRGULA)) {
            // Caso: ;
            return;
        } else {
            // Caso: <expressão>;
            expressao();
            esperar(Token.Tipo.PONTO_VIRGULA, ";");
        }
    }
    
    /**
     * <comandoiteração> → while (<expressão>) <comando>
     */
    private void comandoIteracao() {
        esperar(Token.Tipo.WHILE, "while");
        esperar(Token.Tipo.ABRE_PARENTESES, "(");
        expressao();
        esperar(Token.Tipo.FECHA_PARENTESES, ")");
        comando();
    }
    
    /**
     * <comandoseleção> → if (<expressão>) <comando> <comandoseleção'>
     */
    private void comandoSelecao() {
        esperar(Token.Tipo.IF, "if");
        esperar(Token.Tipo.ABRE_PARENTESES, "(");
        expressao();
        esperar(Token.Tipo.FECHA_PARENTESES, ")");
        comando();
        comandoSelecaoLinha();
    }
    
    /**
     * <comandoseleção'> → else <comando> | ε
     */
    private void comandoSelecaoLinha() {
        if (match(Token.Tipo.ELSE)) {
            comando();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <comandoretorno> → return <comandoretorno'>
     */
    private void comandoRetorno() {
        esperar(Token.Tipo.RETURN, "return");
        comandoRetornoLinha();
    }
    
    /**
     * <comandoretorno'> → ; | <expressão>;
     */
    private void comandoRetornoLinha() {
        if (match(Token.Tipo.PONTO_VIRGULA)) {
            // Caso: ;
            return;
        } else {
            // Caso: <expressão>;
            expressao();
            esperar(Token.Tipo.PONTO_VIRGULA, ";");
        }
    }
    
    /**
     * <comandocomposto> → { <listadecomandos> }
     */
    private void comandoComposto() {
        esperar(Token.Tipo.ABRE_CHAVES, "{");
        listaDeComandos();
        esperar(Token.Tipo.FECHA_CHAVES, "}");
    }
    
    /**
     * <expressão> → ( <expressão> ) <termo'> <expressõessoma'> <expressãosimples'>
     *             | contint <termo'> <expressõessoma'> <expressãosimples'>
     *             | ident <expressãoident'>
     */
    private void expressao() {
        Token.Tipo tipo = lookahead().getTipo();
        
        if (tipo == Token.Tipo.ABRE_PARENTESES) {
            // Caso: ( <expressão> ) <termo'> <expressõessoma'> <expressãosimples'>
            consume(); // (
            expressao();
            esperar(Token.Tipo.FECHA_PARENTESES, ")");
            termoLinha();
            expressoesSomaLinha();
            expressaoSimplesLinha();
        } else if (tipo == Token.Tipo.CONTINT) {
            // Caso: contint <termo'> <expressõessoma'> <expressãosimples'>
            consume(); // contint
            termoLinha();
            expressoesSomaLinha();
            expressaoSimplesLinha();
        } else if (tipo == Token.Tipo.IDENT) {
            // Caso: ident <expressãoident'>
            consume(); // ident
            expressaoIdentLinha();
        } else {
            erro("Esperado expressão: '(', constante inteira ou identificador");
        }
    }
    
    /**
     * <expressãoident'> → = <expressão>
     *                  | [ <expressão> ] <expressãoidentcolchete'>
     *                  | ( <args> ) <termo'> <expressõessoma'> <expressãosimples'>
     *                  | <termo'> <expressõessoma'> <expressãosimples'>
     */
    private void expressaoIdentLinha() {
        Token.Tipo tipo = lookahead().getTipo();
        
        if (tipo == Token.Tipo.ATRIBUICAO) {
            // Caso: = <expressão>
            consume(); // =
            expressao();
        } else if (tipo == Token.Tipo.ABRE_COLCHETES) {
            // Caso: [ <expressão> ] <expressãoidentcolchete'>
            consume(); // [
            expressao();
            esperar(Token.Tipo.FECHA_COLCHETES, "]");
            expressaoIdentColcheteLinha();
        } else if (tipo == Token.Tipo.ABRE_PARENTESES) {
            // Caso: ( <args> ) <termo'> <expressõessoma'> <expressãosimples'>
            consume(); // (
            args();
            esperar(Token.Tipo.FECHA_PARENTESES, ")");
            termoLinha();
            expressoesSomaLinha();
            expressaoSimplesLinha();
        } else {
            // Caso: <termo'> <expressõessoma'> <expressãosimples'>
            termoLinha();
            expressoesSomaLinha();
            expressaoSimplesLinha();
        }
    }
    
    /**
     * <expressãoidentcolchete'> → = <expressão>
     *                           | <termo'> <expressõessoma'> <expressãosimples'>
     */
    private void expressaoIdentColcheteLinha() {
        if (match(Token.Tipo.ATRIBUICAO)) {
            // Caso: = <expressão>
            expressao();
        } else {
            // Caso: <termo'> <expressõessoma'> <expressãosimples'>
            termoLinha();
            expressoesSomaLinha();
            expressaoSimplesLinha();
        }
    }
    
    /**
     * <expressãosimples> → <expressõessoma> <expressãosimples'>
     */
    private void expressaoSimples() {
        expressoesSoma();
        expressaoSimplesLinha();
    }
    
    /**
     * <expressãosimples'> → <oprelacional> <expressõessoma> | ε
     */
    private void expressaoSimplesLinha() {
        if (ehOpRelacional()) {
            opRelacional();
            expressoesSoma();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * Verifica se o token atual é um operador relacional
     */
    private boolean ehOpRelacional() {
        Token.Tipo tipo = lookahead().getTipo();
        return tipo == Token.Tipo.OPRELACIONAL;
    }
    
    /**
     * <oprelacional> → > | < | <= | >= | == | !=
     */
    private void opRelacional() {
        if (!match(Token.Tipo.OPRELACIONAL)) {
            erro("Esperado operador relacional: >, <, <=, >=, == ou !=");
        }
    }
    
    /**
     * <expressõessoma> → <termo> <expressõessoma'>
     */
    private void expressoesSoma() {
        termo();
        expressoesSomaLinha();
    }
    
    /**
     * <expressõessoma'> → <opaditivo> <termo> <expressõessoma'> | ε
     */
    private void expressoesSomaLinha() {
        while (check(Token.Tipo.OPADITIVO)) {
            opAditivo();
            termo();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <opaditivo> → + | -
     */
    private void opAditivo() {
        if (!match(Token.Tipo.OPADITIVO)) {
            erro("Esperado operador aditivo: + ou -");
        }
    }
    
    /**
     * <termo> → <fator> <termo'>
     */
    private void termo() {
        fator();
        termoLinha();
    }
    
    /**
     * <termo'> → <opmult> <fator> <termo'> | ε
     */
    private void termoLinha() {
        while (check(Token.Tipo.OPMULT)) {
            opMult();
            fator();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <opmult> → * | /
     */
    private void opMult() {
        if (!match(Token.Tipo.OPMULT)) {
            erro("Esperado operador multiplicativo: * ou /");
        }
    }
    
    /**
     * <fator> → (<expressão>) | contint | ident <fatorident'>
     */
    private void fator() {
        Token.Tipo tipo = lookahead().getTipo();
        
        if (tipo == Token.Tipo.ABRE_PARENTESES) {
            // Caso: (<expressão>)
            consume(); // (
            expressao();
            esperar(Token.Tipo.FECHA_PARENTESES, ")");
        } else if (tipo == Token.Tipo.CONTINT) {
            // Caso: contint
            consume(); // contint
        } else if (tipo == Token.Tipo.IDENT) {
            // Caso: ident <fatorident'>
            consume(); // ident
            fatorIdentLinha();
        } else {
            erro("Esperado fator: '(', constante inteira ou identificador");
        }
    }
    
    /**
     * <fatorident'> → [ <expressão> ] | ( <args> ) | ε
     */
    private void fatorIdentLinha() {
        Token.Tipo tipo = lookahead().getTipo();
        
        if (tipo == Token.Tipo.ABRE_COLCHETES) {
            // Caso: [ <expressão> ]
            consume(); // [
            expressao();
            esperar(Token.Tipo.FECHA_COLCHETES, "]");
        } else if (tipo == Token.Tipo.ABRE_PARENTESES) {
            // Caso: ( <args> )
            consume(); // (
            args();
            esperar(Token.Tipo.FECHA_PARENTESES, ")");
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <args> → <argslista> | ε
     */
    private void args() {
        // FIRST de <argslista> = FIRST de <expressão> = {(, contint, ident}
        Token.Tipo tipo = lookahead().getTipo();
        if (tipo == Token.Tipo.ABRE_PARENTESES || 
            tipo == Token.Tipo.CONTINT || 
            tipo == Token.Tipo.IDENT) {
            argsLista();
        }
        // Caso contrário, ε (não faz nada)
    }
    
    /**
     * <argslista> → <expressão> <argslista'>
     */
    private void argsLista() {
        expressao();
        argsListaLinha();
    }
    
    /**
     * <argslista'> → , <expressão> <argslista'> | ε
     */
    private void argsListaLinha() {
        while (match(Token.Tipo.VIRGULA)) {
            expressao();
        }
        // Caso contrário, ε (não faz nada)
    }
}


