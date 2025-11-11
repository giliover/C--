# README: Analisador Sintático para Linguagem C-

Este repositório contém a implementação de um analisador sintático para a linguagem C-, baseado na gramática fornecida.

---

## Parte 1: Gramática Proposta (Original)

Esta é a gramática original fornecida para o projeto. Ela contém recursões à esquerda e ambiguidades (como a de `if-else` e a de regras que iniciam com `ident`), o que a torna inadequada para um analisador descendente recursivo (LL(1)) direto.

```
<programa> → <declaraçõeslista>

<declaraçõeslista> → <declaraçõeslista> <declarações> | <declarações>

<declarações> → <declaraçãovar > | <func>

<declaraçãovar> → <tipo> ident; | <tipo> ident [contint];

<tipo > → int | void

<func> → <tipo > ident (<parformais>) <declcomposto>

<parformais> → <listaparformais> | ε

<listaparformais> → <parametro>, <listaparformais> | <parametro>

<parametro> → <tipo> ident | <tipo> ident []

<declcomposto> → { <declaraçõeslocais> <lista comandos> }

<declaraçõeslocais> → <declaraçõeslocais> <declaraçãovar | ε

<listadecomandos> → <comando> <listadecomandos> | ε

<comando> → <comandoexpressão> | <comandocomposto > | <comandoseleção > | <comandoiteração > | <comando retorno>

<comandoexpressão> → <expressão>; | ;

<comandoiteração > → while (<expressão>) <comando>

<comandoseleção > → if (<expressão>) <comando> | If (<expressão>) <comando> else <comando>

<comando retorno> → return; | return <expressão>);

<comandocomposto > → { <listadecomandos> }

<expressão> → <var> = <expressão> | <expressãosimples>

<var> → ident | ident [ <expressão> ]

<expressãosimples> → <expressõessoma> <oprelacional> <expressõessoma> | <expressõessoma>

<oprelacional> → > | < | <= | >= | == | !=

<expressõessoma> → <expressõessoma> <opaditivo> <termo> | <termo>

<opaditivo> → + | -

<termo> → <termo> <opmult> <fator> | <fator>

<opmult> → * | / 

<fator> → (<expressão>) | <var> | <ativação> | contint

<ativação> → ident (<args> )

<args> → <argslista > | ε

<argslista > → <argslista>, <expressão> | <expressão>
```

## Parte 2: Gramática Refatorada para LL(1)

Abaixo está a gramática original modificada para ser compatível com um parser LL(1). As duas técnicas principais aplicadas foram:

1.  **Eliminação de Recursão à Esquerda:** Regras na forma `A → A α | β` foram convertidas para `A → β A'` e `A' → α A' | ε` (representadas no parser como um loop `while`).
2.  **Fatoração à Esquerda:** Regras na forma `A → α β | α γ` foram fatoradas para `A → α A'` e `A' → β | γ` (representadas no parser como um `if` ou `switch` para decidir o caminho).

```
<programa> → <declaraçõeslista>

<declaraçõeslista> → <declarações> <declaraçõeslista'>
<declaraçõeslista'> → <declarações> <declaraçõeslista'> | ε

<declarações> → <tipo> ident <declarações'>
<declarações'> → ; | [contint]; | (<parformais>) <declcomposto>

<declaraçãovar> → <tipo> ident <declaraçãovar'>
<declaraçãovar'> → ; | [contint];

<tipo > → int | void

<parformais> → <listaparformais> | ε

<listaparformais> → <parametro> <listaparformais'>
<listaparformais'> → , <listaparformais> | ε

<parametro> → <tipo> ident <parametro'>
<parametro'> → [] | ε

<declcomposto> → { <declaraçõeslocais> <listadecomandos> }

<declaraçõeslocais> → <declaraçãovar> <declaraçõeslocais> | ε

<listadecomandos> → <comando> <listadecomandos> | ε

<comando> → <comandoexpressão> | <comandocomposto > | <comandoseleção > | <comandoiteração > | <comando retorno>

<comandoexpressão> → <expressão>; | ;

<comandoiteração > → while (<expressão>) <comando>

<comandoseleção> → if (<expressão>) <comando> <comandoseleção'>
<comandoseleção'> → else <comando> | ε

<comando retorno> → return <comando retorno'>
<comando retorno'> → ; | <expressão>;

<comandocomposto > → { <listadecomandos> }

<expressão> → ( <expressão> ) <termo'> <expressõessoma'> <expressãosimples'>
| contint <termo'> <expressõessoma'> <expressãosimples'>
| ident <expressão_ident'>

<expressão_ident'> → = <expressão>
| [ <expressão> ] <expressão_ident_colchete'>
| ( <args> ) <termo'> <expressõessoma'> <expressãosimples'>
| <termo'> <expressõessoma'> <expressãosimples'>

<expressão_ident_colchete'> → = <expressão>
| <termo'> <expressõessoma'> <expressãosimples'>

<expressãosimples> → <expressõessoma> <expressãosimples'>
<expressãosimples'> → <oprelacional> <expressõessoma> | ε

<oprelacional> → > | < | <= | >= | == | !=

<expressõessoma> → <termo> <expressõessoma'>
<expressõessoma'> → <opaditivo> <termo> <expressõessoma'> | ε

<opaditivo> → + | -

<termo> → <fator> <termo'>
<termo'> → <opmult> <fator> <termo'> | ε

<opmult> → * | /

<fator> → (<expressão>) | contint | ident <fatorident'>
<fatorident'> → [ <expressão> ] | ( <args> ) | ε

<args> → <argslista > | ε

<argslista> → <expressão> <argslista'>
<argslista'> → , <expressão> <argslista'> | ε
```