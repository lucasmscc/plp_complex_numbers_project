# Linguagem Funcional 3 com Suporte a Números Complexos (Funcional 3C)

Universidade Federal de Pernambuco - Centro de Informática
IN1007 - Paradigmas de Linguagens de Programação

## Equipe

* Dayane Lira
* Lucas Mascena

---

## 1. Escopo e Motivação

Este projeto estende a linguagem **Funcional 3** (caracterizada por transparência referencial, funções de primeira classe, recursão, listas e compreensão de listas) introduzindo suporte nativo ao tipo de dado **Número Complexo** e aritmética no plano complexo.

Na matemática e em engenharia, números complexos possuem a forma $z = a + bi$, em que $a \in \mathbb{Z}$ representa a parte real e $b \in \mathbb{Z}$ a parte imaginária. Na **Funcional 3C**, números complexos são tratados como valores imutáveis e de primeira classe (`ValorConcreto`).

Como valores de primeira classe, números complexos podem:

* Ser atribuídos a identificadores por meio de `let`/`var`;
* Ser passados como argumentos para funções;
* Ser retornados por funções;
* Ser utilizados por funções de alta ordem;
* Ser armazenados em listas;
* Ser processados por meio de compreensão de listas;
* Participar de expressões aritméticas simbólicas.

O principal objetivo da extensão é permitir que as **quatro operações aritméticas básicas** (`+`, `-`, `*`, `/`) sejam realizadas diretamente sobre números complexos, sem a necessidade de decompor manualmente suas partes real e imaginária.

---

## 2. Elementos Adicionados

### 2.1 Novas Estruturas e Tipos

* **`ValorComplexo` / `ExpComplexa`** : Estrutura interna que armazena dois nós de expressão simbólica (`real` e `imaginario`).
* **`TipoComplexo`** : Interface de tipo para validação e checagem estática/dinâmica.
* **Literal / Construtor `complex(e1, e2)`** : Criação de um número complexo onde `e1` e `e2` podem ser inteiros, reais ou expressões algébricas simbólicas contendo identificadores.
* **Símbolo `sym("x")` / Variável Simbólica** : Expressão que representa uma incógnita não avaliada.

### 2.2 Operadores Unários

* **`re(e)`**: retorna o `ValorInteiro` referente à parte real de $z$.
* **`im(e)`**: retorna o `ValorInteiro` referente à parte imaginária de $z$.
* **`abs(e)`**: calcula o módulo/norma $\Vert{}z\Vert{} = \sqrt{a^2 + b^2}$ (retorna `ValorInteiro` truncado/aproximado).
* **`conj(e)`**: retorna o conjugado complexo $\bar{z} = a - bi$.
* **`simplify(e)`** : Simplifica algebricamente a expressão complexa resultante das operações simbólicas.

### 2.3 Operadores Binários e Promoção de Tipos

Os quatro operadores binários fundamentais (`+`, `-`, `*`, `/`) aplicados a números complexos simbólicos **$z_1 = a + bi$** e **$z_2 = c + di$** seguem as regras algébricas expandidas simbolicamente:

1. **Adição (`+`)** : **$(a + c) + (b + d)i$**
2. **Subtração (`-`)** : **$(a - c) + (b - d)i$**
3. **Multiplicação (`*`)** : **$(ac - bd) + (ad + bc)i$**
4. **Divisão (`/`)** : **$\left(\frac{ac + bd}{c^2 + d^2}\right) + \left(\frac{bc - ad}{c^2 + d^2}\right)i$**

Para manter a ergonomia da linguagem, foi implementada uma regra de **coerção/promoção estática de tipos**:

| Operando Esquerdo | Operando Direito | Tipo Resultante | Ação de Promoção        |
| :---------------- | :--------------- | :-------------- | :-------------------------- |
| `INTEIRO`       | `INTEIRO`      | `INTEIRO`     | Nenhuma (operação nativa) |
| `COMPLEXO`      | `COMPLEXO`     | `COMPLEXO`    | Nenhuma                     |
| `INTEIRO`       | `COMPLEXO`     | `COMPLEXO`    | $a \to complex(a, 0)$     |
| `COMPLEXO`      | `INTEIRO`      | `COMPLEXO`    | $a \to complex(a, 0)$     |

## 3. Exemplos de Código

### 3.1 Instanciação SImbólica e Operações Básicas

```sml
let
    // Criando números complexos simbólicos: z1 = x + 2i, z2 = 3 + yi
    var z1 = complex(sym("x"), 2)
    var z2 = complex(3, sym("y"))
  
    // Adição simbólica: (x + 3) + (2 + y)i
    var soma = z1 + z2
  
    // Conjugado simbólico: x - 2i
    var z1_conj = conj(z1)
in
    soma
end;
```

### 3.2 Operações SImbólicas

```sml
let
    var z1 = complex(sym("a"), sym("b"))
    var z2 = complex(sym("c"), sym("d"))

    var s = z1 + z2  // Resulta: complex(a + c, b + d)
    var sub = z1 - z2 // Resulta: complex(a - c, b - d)
    var m = z1 * z2  // Resulta: complex(a*c - b*d, a*d + b*c)
    var d = z1 / z2  // Resulta: complex((a*c + b*d)/(c^2 + d^2), (b*c - a*d)/(c^2 + d^2))
in
    d
end;
```

### 3.3 Promoção de Tipos e Simplificação

```sml
let
    var z = complex(sym("x"), 4)
    // Promoção implícita do inteiro 5 para complex(5, 0) na multiplicação simbólica
    var expr = simplify(z * 5)
in
    expr // Resulta em: complex(5*x, 20)
end;
```

### 3.4 Avaliação (Substituição de Símbolos)

```sml
let
    var z_simbolico = complex(sym("x"), sym("y")) * complex(2, 1)
    // Avalia substituindo x=1 e y=3 na expressão simbólica gerada
    var z_avaliado = eval(z_simbolico, [("x", 1), ("y", 3)])
in
    z_avaliado
end;
```

## 4. Gramática

Cada símbolo não terminal e cada operador/palavra-chave que corresponde a uma classe é um link para o código-fonte (`Ctrl+clique` no VS Code; clique normal no GitHub). `ListId`, `ListExp` e `Filtro` não têm classe própria e apontam para a produção correspondente em [Funcional3.jj](Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj).

<pre>
<a href="Funcional3/src/lf3/plp/functional3/Programa.java">Programa</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/Valor.java">Valor</a>
           | <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpUnaria.java">ExpUnaria</a>
           | <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpBinaria.java">ExpBinaria</a>
           | <a href="Funcional3/src/lf3/plp/functional2/expression/ExpDeclaracao.java">ExpDeclaracao</a>
           | <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a>
           | <a href="Funcional3/src/lf3/plp/functional2/expression/Aplicacao.java">Aplicacao</a>
           | <a href="Funcional3/src/lf3/plp/functional1/expression/IfThenElse.java">IfThenElse</a>

<a href="Funcional3/src/lf3/plp/expressions2/expression/Valor.java">Valor</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/ValorConcreto.java">ValorConcreto</a>
        | <a href="Funcional3/src/lf3/plp/functional3/expression/ValorSimbolico.java">ValorSimbolico</a>
        | <a href="Funcional3/src/lf3/plp/functional2/expression/ValorAbstrato.java">ValorAbstrato</a>

<a href="Funcional3/src/lf3/plp/functional2/expression/ValorAbstrato.java">ValorAbstrato</a> ::= <a href="Funcional3/src/lf3/plp/functional2/expression/ValorFuncao.java">ValorFuncao</a>

<a href="Funcional3/src/lf3/plp/expressions2/expression/ValorConcreto.java">ValorConcreto</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/ValorInteiro.java">ValorInteiro</a>
                | <a href="Funcional3/src/lf3/plp/expressions2/expression/ValorBooleano.java">ValorBooleano</a>
                | <a href="Funcional3/src/lf3/plp/expressions2/expression/ValorString.java">ValorString</a>
                | <a href="Funcional3/src/lf3/plp/functional3/expression/ValorLista.java">ValorLista</a>
                | <a href="Funcional3/src/lf3/plp/functional3/expression/ValorComplexo.java">ValorComplexo</a>

<a href="Funcional3/src/lf3/plp/functional3/expression/ValorSimbolico.java">ValorSimbolico</a> ::= "sym" "(" StringLiteral ")"

<a href="Funcional3/src/lf3/plp/functional2/expression/ValorFuncao.java">ValorFuncao</a> ::= "fn" <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a> "." <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="Funcional3/src/lf3/plp/functional3/expression/ValorComplexo.java">ValorComplexo</a> ::= <a href="Funcional3/src/lf3/plp/functional3/expression/ExpComplexa.java">"complex"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> "," <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"

<a href="Funcional3/src/lf3/plp/expressions2/expression/ExpUnaria.java">ExpUnaria</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpMenos.java">"-"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
            | <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpNot.java">"not"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
            | <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpLength.java">"length"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpHead.java">"head"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpTail.java">"tail"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpRe.java">"re"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpIm.java">"im"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpAbs.java">"abs"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpConj.java">"conj"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpSimplify.java">"simplify"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"
            | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpCompreensaoLista.java">ExpCompreensaoLista</a>

<a href="Funcional3/src/lf3/plp/functional3/expression/ExpCompreensaoLista.java">ExpCompreensaoLista</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/functional3/expression/Gerador.java">Gerador</a>
                      | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/functional3/expression/Gerador.java">Gerador</a> <a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L835">Filtro</a>

<a href="Funcional3/src/lf3/plp/functional3/expression/Gerador.java">Gerador</a> ::= "for" <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a> "in" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
          | "for" <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a> "in" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> "," <a href="Funcional3/src/lf3/plp/functional3/expression/Gerador.java">Gerador</a>

<a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L835">Filtro</a> ::= "if" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="Funcional3/src/lf3/plp/expressions2/expression/ExpBinaria.java">ExpBinaria</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpSoma.java">"+"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpSub.java">"-"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpMult.java">"*"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpDiv.java">"/"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/functional3/expression/ExpMaiorQue.java">"&gt;"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/functional3/expression/ExpMenorQue.java">"&lt;"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpAnd.java">"and"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpOr.java">"or"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpEquals.java">"=="</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/ExpConcat.java">"++"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/functional3/expression/ExpSequencia.java">".."</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/functional3/expression/ExpCons.java">":"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> <a href="Funcional3/src/lf3/plp/functional3/expression/ExpConcatLista.java">"^^"</a> <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
             | <a href="Funcional3/src/lf3/plp/functional3/expression/ExpEval.java">"eval"</a> "(" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> "," "[" <a href="Funcional3/src/lf3/plp/functional3/expression/ExpEval.java">ListaParesSubstituicao</a> "]" ")"

<a href="Funcional3/src/lf3/plp/functional3/expression/ExpEval.java">ListaParesSubstituicao</a> ::= "[" <a href="Funcional3/src/lf3/plp/functional3/expression/ParSubstituicao.java">ParSubstituicao</a> "]"
                         | "[" <a href="Funcional3/src/lf3/plp/functional3/expression/ParSubstituicao.java">ParSubstituicao</a> ("," <a href="Funcional3/src/lf3/plp/functional3/expression/ParSubstituicao.java">ParSubstituicao</a>)* "]"

<a href="Funcional3/src/lf3/plp/functional3/expression/ParSubstituicao.java">ParSubstituicao</a> ::= "(" StringLiteral "," <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> ")"

<a href="Funcional3/src/lf3/plp/functional2/expression/ExpDeclaracao.java">ExpDeclaracao</a> ::= "let" <a href="Funcional3/src/lf3/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a> "in" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="Funcional3/src/lf3/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a> ::= <a href="Funcional3/src/lf3/plp/functional1/declaration/DecVariavel.java">DecVariavel</a>
                      | <a href="Funcional3/src/lf3/plp/functional2/declaration/DecFuncao.java">DecFuncao</a>
                      | <a href="Funcional3/src/lf3/plp/functional3/declaration/DecComposta.java">DecComposta</a>

<a href="Funcional3/src/lf3/plp/functional1/declaration/DecVariavel.java">DecVariavel</a> ::= "var" <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a> "=" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="Funcional3/src/lf3/plp/functional2/declaration/DecFuncao.java">DecFuncao</a> ::= "fun" <a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L648">ListId</a> "=" <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="Funcional3/src/lf3/plp/functional3/declaration/DecComposta.java">DecComposta</a> ::= <a href="Funcional3/src/lf3/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a> "," <a href="Funcional3/src/lf3/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a>

<a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L648">ListId</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a>
        | <a href="Funcional3/src/lf3/plp/expressions2/expression/Id.java">Id</a> "," <a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L648">ListId</a>

<a href="Funcional3/src/lf3/plp/functional2/expression/Aplicacao.java">Aplicacao</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> "(" <a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L761">ListExp</a> ")"

<a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L761">ListExp</a> ::= <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a>
        | <a href="Funcional3/src/lf3/plp/expressions2/expression/Expressao.java">Expressao</a> "," <a href="Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj#L761">ListExp</a>
</pre>
