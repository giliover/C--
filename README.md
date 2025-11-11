# README: Analisador Sintático para Linguagem C-

Este repositório contém a implementação de um analisador sintático para a linguagem C-, baseado na gramática fornecida.

---

## Parte 1: Gramática Proposta (Original)

Esta é a gramática original fornecida para o projeto. Ela contém recursões à esquerda e ambiguidades (como a de `if-else` e a de regras que iniciam com `ident`), o que a torna inadequada para um analisador descendente recursivo (LL(1)) direto.

```ebnf
<programa> → <declarações lista>

< declarações lista > → < declarações lista> <declarações> | <declarações>

<declarações> → < declaração var > | < declaração func>

< declaração var> → <tipo> ident; | <tipo> ident [contint];

<tipo > → int | void

<declaração func> → <tipo > ident (<par formais>) <decl composto>

< par formais> → < lista par formais > | ε

< lista par formais > → <parametro>, < lista par formais > | < parametro >

<parametro> → <tipo> ident | <tipo> ident []

<decl composto> → { <declarações locais> <lista comandos> }

<declarações locais> → <declarações locais> < declaração var | ε

<lista de comandos> → <comando> <lista de comandos> | ε

<comando> → <comando expressão > | < comando composto > | <comando seleção > | <comando iteração > | <comando retorno>

< comando expressão > → <expressão>; | ;

<comando iteração > → while (<expressão>) <comando>

<comando seleção > → if (<expressão>) < comando> | If (<expressão>) < comando> else <comando>

<comando retorno> → return; | return <expressão>);

< comando composto > → { <lista de comandos> }

<expressão> → <var> = <expressão> | <expressão simples>

<var> → ident | ident [ <expressão> ]

<expressão simples> → <expressões soma> < op relacional> <expressões soma> | <expressões soma>

<op relacional> → > | < | <= | >= | == | !=

<expressões soma> → <expressões soma > <op aditivo> <termo> | <termo>

<op aditivo> → + | -

<termo> → <termo> <op mult> <fator> | <fator>

<op mult> → * | / (Nota: fonte 56 é "1/")

<fator> → (<expressão>) | <var> | <ativação> | contint

<ativação> → ident (<args> )

<args> → <args-lista > | ε

<args-lista > → <args-lista>, <expressão> | <expressão>
