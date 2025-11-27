import java.util.List;

/**
 * Exemplo de uso do ParserLL1
 * Demonstra como instanciar o parser e fazer o parsing top-down
 * usando o analisador léxico para gerar tokens dinamicamente
 */
public class ExemploUso {
    
    public static void main(String[] args) {
        // Exemplo de código C- para análise
        String codigoFonte = 
            "int main() {\n" +
            "    int x;\n" +
            "    int y[10];\n" +
            "    x = 5;\n" +
            "    if (x > 0) {\n" +
            "        y[0] = x + 1;\n" +
            "    } else {\n" +
            "        y[0] = 0;\n" +
            "    }\n" +
            "    while (x > 0) {\n" +
            "        x = x - 1;\n" +
            "    }\n" +
            "    return 0;\n" +
            "}\n";
        
        System.out.println("=== Código Fonte ===");
        System.out.println(codigoFonte);
        
        // 1. Analisar código-fonte e gerar tokens (análise léxica)
        System.out.println("\n=== Análise Léxica ===");
        Lexico lexico = new Lexico(codigoFonte);
        List<Token> tokens = lexico.analisar();
        
        System.out.println("Tokens gerados: " + tokens.size());
        for (Token token : tokens) {
            System.out.println("  " + token);
        }
        
        // 2. Instanciar o parser LL(1)
        System.out.println("\n=== Análise Sintática (Parser LL(1)) ===");
        ParserLL1 parser = new ParserLL1(tokens);
        
        // 3. Executar o parsing
        boolean sucesso = parser.parse();
        
        // 4. Verificar resultado
        if (sucesso) {
            System.out.println("Parsing bem-sucedido! Programa sintaticamente correto.");
        } else {
            System.out.println("✗ Erros encontrados durante o parsing:");
            for (String erro : parser.getErros()) {
                System.out.println("  - " + erro);
            }
        }
    }
}
