# README: Analisador Sintático para Linguagem C-

Este repositório contém a implementação de um analisador sintático para a linguagem C-, baseado na gramática fornecida.

---

## Parte 1: Gramática Proposta (Original)

Esta é a gramática original fornecida para o projeto. Ela contém recursões à esquerda e ambiguidades (como a de `if-else` e a de regras que iniciam com `ident`), o que a torna inadequada para um analisador descendente recursivo (LL(1)) direto.

```
<programa> → <declaraçõeslista>

<declaraçõeslista> → <declaraçõeslista> <declarações> | <declarações>

<declarações> → <declaraçãovar > | <declaração func>

<declaraçãovar> → <tipo> ident; | <tipo> ident [contint];

<tipo > → int | void

<declaração func> → <tipo > ident (<parformais>) <declcomposto>

<parformais> → <listaparformais> | ε

<listaparformais> → <parametro>, <listaparformais > | <parametro>

<parametro> → <tipo> ident | <tipo> ident []

<declcomposto> → { <declaraçõeslocais> <lista comandos> }

<declaraçõeslocais> → <declaraçõeslocais> <declaraçãovar | ε

<lista de comandos> → <comando> <lista de comandos> | ε

<comando> → <comandoexpressão> | <comandocomposto > | <comandoseleção > | <comandoiteração > | <comando retorno>

<comandoexpressão> → <expressão>; | ;

<comandoiteração > → while (<expressão>) <comando>

<comandoseleção > → if (<expressão>) <comando> | If (<expressão>) <comando> else <comando>

<comando retorno> → return; | return <expressão>);

<comandocomposto > → { <lista de comandos> }

<expressão> → <var> = <expressão> | <expressãosimples>

<var> → ident | ident [ <expressão> ]

<expressãosimples> → <expressõessoma> <op relacional> <expressõessoma> | <expressõessoma>

<op relacional> → > | < | <= | >= | == | !=

<expressõessoma> → <expressõessoma > <op aditivo> <termo> | <termo>

<op aditivo> → + | -

<termo> → <termo> <opmult> <fator> | <fator>

<opmult> → * | / 

<fator> → (<expressão>) | <var> | <ativação> | contint

<ativação> → ident (<args> )

<args> → <argslista > | ε

<argslista > → <argslista>, <expressão> | <expressão>
```
