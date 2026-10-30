# Extensão da Funcional 3 para números complexos e simbólicos (Funcional 3C)

Este documento descreve **o que foi alterado** na linguagem Funcional 3 original (commit `f6ad7eb`) para suportar números complexos e valores simbólicos, e **por que** cada alteração foi feita. O escopo e a sintaxe da linguagem estão no [README](README.md); aqui o foco é a implementação em [Funcional3/src/lf3/plp](Funcional3/src/lf3/plp).

## 1. Visão geral

A Funcional 3 original só tinha quatro tipos primitivos (`INTEIRO`, `BOOLEANO`, `STRING`, mais listas e funções) e aritmética inteira (`+`, `-`, `*`). A extensão adiciona:

| Capacidade | Como aparece na linguagem |
| :-- | :-- |
| Tipo complexo | `complex(e1, e2)` |
| Incógnitas | `sym("x")` |
| Operações sobre complexos | `re`, `im`, `abs`, `conj` |
| Álgebra simbólica | `simplify(e)`, `eval(e, [("x", e1), ...])` |
| Aritmética completa | `+ - * /` aceitam inteiros, complexos e simbólicos; `/` é nova |

A extensão foi feita em quatro fases (visíveis no histórico: `a8ffff8` → `8e4e90a` → `33c1741` → `5550392`), cada uma com seus testes: (1) tipo complexo, (2) aritmética e divisão, (3) símbolos, `simplify` e `eval`, (4) integração com funções, listas e compreensão de listas.

### Ideia central

Em vez de criar um novo tipo de expressão para cada combinação (complexo de inteiros, complexo de símbolos, ...), a extensão usa **composição de valores**:

```
Valor
├── ValorInteiro        (já existia)
├── ValorSimbolico      (novo)  incógnita ou árvore de operações sobre inteiros/incógnitas
└── ValorComplexo       (novo)  par (real, imag) onde cada parte é ValorInteiro ou ValorSimbolico
```

Consequências:

* `complex(sym("x"), 2)` não exige nenhum caso especial: a parte real é um `ValorSimbolico`, a imaginária um `ValorInteiro`.
* As fórmulas de soma, produto e divisão de complexos são escritas **uma única vez**, em termos das operações de [ValorSimbolico](Funcional3/src/lf3/plp/functional3/expression/ValorSimbolico.java), que por sua vez devolve `ValorInteiro` quando os dois operandos são inteiros. Assim, `complex(1, 2) * complex(3, 4)` produz `complex(-5, 10)` (numérico) e `complex(sym("a"), sym("b")) * complex(sym("c"), sym("d"))` produz `complex(a*c - b*d, a*d + b*c)` (simbólico) pelo mesmo código.
* Programas que não usam as novidades se comportam exatamente como antes (há testes de regressão `base_*.f3`).

## 2. Mapa das mudanças

### 2.1 Arquivos novos

| Arquivo | Papel |
| :-- | :-- |
| [ValorComplexo](Funcional3/src/lf3/plp/functional3/expression/ValorComplexo.java) | Valor imutável `a + bi`; contém `promover`, `soma`, `sub`, `mult`, `div`, `conj`, `abs` |
| [ValorSimbolico](Funcional3/src/lf3/plp/functional3/expression/ValorSimbolico.java) | Incógnita ou nó de operação; contém a aritmética, `substituir` (eval) e `simplificar` |
| [ExpComplexa](Funcional3/src/lf3/plp/functional3/expression/ExpComplexa.java) | Construtor `complex(e1, e2)` |
| [ExpRe](Funcional3/src/lf3/plp/functional3/expression/ExpRe.java), [ExpIm](Funcional3/src/lf3/plp/functional3/expression/ExpIm.java) | Partes real e imaginária |
| [ExpAbs](Funcional3/src/lf3/plp/functional3/expression/ExpAbs.java) | Módulo |
| [ExpConj](Funcional3/src/lf3/plp/functional3/expression/ExpConj.java) | Conjugado |
| [ExpSimplify](Funcional3/src/lf3/plp/functional3/expression/ExpSimplify.java) | `simplify(e)` |
| [ExpEval](Funcional3/src/lf3/plp/functional3/expression/ExpEval.java), [ParSubstituicao](Funcional3/src/lf3/plp/functional3/expression/ParSubstituicao.java) | `eval(e, [("x", e1), ...])` e seus pares |
| [ExpDiv](Funcional3/src/lf3/plp/expressions2/expression/ExpDiv.java) | Divisão (a linguagem original não tinha `/`) |
| [testes/](Funcional3/testes) | 63 programas `.f3` e o script [rodar.sh](Funcional3/testes/rodar.sh) |

### 2.2 Arquivos modificados

| Arquivo | Mudança |
| :-- | :-- |
| [Tipo](Funcional3/src/lf3/plp/expressions1/util/Tipo.java) | Novo método `eComplexo()` na interface |
| [TipoPrimitivo](Funcional3/src/lf3/plp/expressions1/util/TipoPrimitivo.java) | Novo valor `COMPLEXO` e implementação de `eComplexo()` |
| [TipoFuncao](Funcional3/src/lf3/plp/functional1/util/TipoFuncao.java), [TipoPolimorfico](Funcional3/src/lf3/plp/functional1/util/TipoPolimorfico.java), [TipoLista](Funcional3/src/lf3/plp/functional3/util/TipoLista.java) | Implementam `eComplexo()` (exigido pela interface) |
| [ExpSoma](Funcional3/src/lf3/plp/expressions2/expression/ExpSoma.java), [ExpSub](Funcional3/src/lf3/plp/expressions2/expression/ExpSub.java), [ExpMult](Funcional3/src/lf3/plp/expressions2/expression/ExpMult.java) | Avaliação e checagem de tipos passam a aceitar complexo e simbólico |
| [ExpMult](Funcional3/src/lf3/plp/expressions2/expression/ExpMult.java) | Movida de `functional3.expression` para `expressions2.expression` |
| [ExpMenos](Funcional3/src/lf3/plp/expressions2/expression/ExpMenos.java) | Aceita operando simbólico |
| [PartialInstantiatorVisitor](Funcional3/src/lf3/plp/functional3/util/PartialInstantiatorVisitor.java) | Ajuste de import por causa da mudança de pacote de `ExpMult` |
| [Funcional3.jj](Funcional3/src/lf3/plp/functional3/parser/Funcional3.jj) | Novos tokens, produções, operador `/` e impressão do resultado |

## 3. Detalhamento e justificativas

### 3.1 Sistema de tipos

**O que mudou.** `TipoPrimitivo` ganhou a constante `COMPLEXO`, e a interface `Tipo` ganhou `eComplexo()`, ao lado de `eInteiro()`, `eBooleano()` e `eString()`.

**Por quê.** A checagem de tipos da linguagem é feita por predicados (`getTipo(amb).eInteiro()`), não por comparação direta de tipos. Seguir o mesmo padrão com `eComplexo()` mantém a checagem das expressões uniforme. Como `eComplexo()` entrou na interface, **todas** as implementações precisaram dele: `TipoFuncao` delega à imagem da função, `TipoLista` devolve `false` (uma lista nunca é um complexo) e `TipoPolimorfico` tenta unificar com `COMPLEXO`, o que permite inferir parâmetros de funções polimórficas usados como complexos.

**Incógnitas têm tipo `INTEIRO`.** `ValorSimbolico.getTipo` devolve `INTEIRO`: uma incógnita representa um inteiro ainda desconhecido. Isso evita criar um tipo "simbólico" que se propagaria por todas as regras de tipagem; `sym("x") + 1` e `complex(sym("x"), 2)` passam na checagem sem regras adicionais.

### 3.2 Promoção de tipos nas operações binárias

**O que mudou.** Em `ExpSoma`, `ExpSub`, `ExpMult` e `ExpDiv`:

* `checaTipoElementoTerminal` aceita cada operando como `INTEIRO` **ou** `COMPLEXO` (antes, só `INTEIRO`);
* `getTipo` devolve `COMPLEXO` se qualquer lado for complexo, senão `INTEIRO`;
* `avaliar` decide pelo tipo **dinâmico** dos valores: se algum for `ValorComplexo`, promove os dois com `ValorComplexo.promover` (inteiro `a` vira `complex(a, 0)`) e chama a operação de complexos; senão, se algum for `ValorSimbolico`, chama a operação simbólica; senão faz a conta `int` original.

**Por quê.**

* É a tabela de coerção do README (inteiro com complexo resulta em complexo) implementada em um único lugar por operador.
* A ordem dos testes importa: complexo vem antes de simbólico, pois as partes de um complexo podem ser simbólicas e a operação de complexos já trata isso internamente.
* O ramo inteiro permanece idêntico ao original, preservando o comportamento de programas antigos.
* A checagem estática continua rejeitando misturas inválidas, como `complex(1, 2) + true` (teste `f1_erro_soma_complexo_booleano`).

### 3.3 Divisão (`ExpDiv`) e o operador `/`

**O que mudou.** A linguagem original não tinha divisão. `ExpDiv` foi criada seguindo o molde de `ExpSoma`/`ExpSub`, e o token `SLASH` (já declarado no `.jj`, mas sem produção) passou a gerar `ExpDiv` em `PExpBinaria`.

**Regras.**

* Inteiro / inteiro usa a divisão inteira do Java (trunca em direção a zero). Divisor zero lança `ArithmeticException("divisao por zero")`.
* Complexo: `(a + bi)/(c + di) = ((ac + bd)/(c² + d²)) + ((bc − ad)/(c² + d²))i`. Se `c² + d²` resulta no inteiro 0, lança `ArithmeticException` explicitamente em `ValorComplexo.div`.

**Por quê.** O README exige as quatro operações. A divisão de complexos precisa do divisor `c² + d²`; quando ele é numérico e nulo, a divisão é indefinida e o erro é lançado antes de construir o resultado. Quando o divisor é simbólico, nada pode ser decidido em tempo de execução, então o resultado permanece simbólico.

### 3.4 `ValorComplexo`

Guarda `real` e `imag` como `Valor` já avaliados (`ValorInteiro` ou `ValorSimbolico`) e é imutável. Os métodos estáticos implementam as fórmulas do README delegando às operações de `ValorSimbolico`.

* `promover(Valor)` implementa a coerção `a → complex(a, 0)`.
* `conj(z)` nega a parte imaginária com `ValorSimbolico.neg`.
* `abs(z)` calcula `raiz(re² + im²)`. Sobre inteiros, `raiz` é `(int) Math.sqrt(x)`, ou seja, o **piso** do módulo (`abs(complex(3, 4))` dá `5`; para `complex(1, 1)` dá `1`). O truncamento é a consequência de a linguagem não ter números reais: o resultado precisa ser `INTEIRO`. Se a soma dos quadrados for simbólica, o resultado é um nó `sqrt(...)`.
* `equals`/`hashCode`/`clone`/`toString` implementados: `equals` compara as duas partes, `clone` copia as duas partes (necessário porque o interpretador clona expressões) e `toString` imprime `complex(a, b)`, a mesma sintaxe do literal.

**Por que partes `Valor` e não `int`.** Se as partes fossem `int`, `complex(sym("x"), 2)` e toda a parte simbólica do README seriam impossíveis.

### 3.5 `ValorSimbolico`

Um único tipo de valor representa dois casos: uma **incógnita** (`nome != null`) ou um **nó composto** (`op`, `esq`, `dir`) para `+ - * /`, negação unária e raiz. É imutável, então `clone()` devolve `this`.

* **Aritmética** (`soma`, `sub`, `mult`, `div`, `neg`, `raiz`): se os dois operandos são `ValorInteiro`, calcula na hora e devolve `ValorInteiro`; caso contrário monta um nó. Isso garante que expressões sem incógnitas "colapsam" para números e que o ramo simbólico só é usado quando necessário. Operandos que não são inteiro nem simbólico são rejeitados (`verifica`).
* **`substituir(Map)`** (usado por `eval`): substitui todas as incógnitas de uma só vez e reconstrói a árvore pelas mesmas operações; se não sobrar incógnita, o resultado volta a ser `ValorInteiro`. A substituição é **simultânea** (os valores colocados não são re-substituídos), então `[("x", sym("y")), ("y", sym("x"))]` troca `x` e `y` em vez de entrar em loop.
* **`simplificar()`**: reescrita de baixo para cima com dobramento de constantes e identidades `e+0=e`, `e−0=e`, `0−e=−e`, `e−e=0`, `e*1=e`, `e*0=0`, `e/1=e`, `−(−e)=e`, mais a normalização do coeficiente à esquerda (`x*5` → `5*x`). É o que produz `complex(5*x, 20)` no exemplo 3.3 do README. Não é um simplificador algébrico completo (não fatora nem reordena somas), por isso o conjunto de regras é deliberadamente pequeno e fácil de justificar. A regra `e*0=0` ignora o caso de a incógnita valer zero em um divisor, o que está documentado no código.
* **`toString()`** imprime com parênteses só quando necessário (`a*c - b*d`, `(a*c + b*d)/(c*c + d*d)`), para que a saída seja legível e igual ao estilo do README.

**Por que simbolismo como valor, e não como tipo de expressão.** `sym("x")` precisa poder ser guardado em `let`, passado a funções e colocado em listas. Isso só funciona de graça se ele for um `Valor`, como o inteiro.

### 3.6 Novas expressões

Todas seguem o padrão das expressões existentes: `avaliar`, `checaTipoElementoTerminal`/`checaTipo`, `getTipo`, `clone`, `toString`.

| Expressão | Regra de tipo | Resultado |
| :-- | :-- | :-- |
| `ExpComplexa` (`complex(e1, e2)`) | partes `INTEIRO` (incógnitas contam como inteiro) | `COMPLEXO` |
| `ExpRe`, `ExpIm` | argumento `COMPLEXO` | `INTEIRO` (a parte pode ser simbólica em tempo de execução) |
| `ExpAbs` | argumento `COMPLEXO` | `INTEIRO` |
| `ExpConj` | argumento `COMPLEXO` | `COMPLEXO` |
| `ExpSimplify` | argumento `INTEIRO` ou `COMPLEXO` | mesmo tipo do argumento |
| `ExpEval` | expressão `INTEIRO`/`COMPLEXO`; valores de substituição `INTEIRO` | mesmo tipo da expressão |

* `ExpRe`, `ExpIm`, `ExpAbs` e `ExpConj` estendem `ExpUnaria`, aproveitando a checagem de tipos do operando e o `toString`.
* `ExpComplexa` estende `ExpBinaria` (duas subexpressões) com o rótulo `"complex"`.
* `ExpSimplify` e `ExpEval` implementam `Expressao` diretamente, pois o tipo de saída depende do operando e `ExpEval` tem uma lista de pares. Ambos aplicam a operação **às partes** de um complexo, uma a uma.
* `ExpEval` avalia cada valor de substituição antes de substituir e rejeita valores que não sejam inteiro ou simbólico (`IllegalArgumentException`). `ParSubstituicao` é só o par imutável `(nome, expressão)`.
* `ExpEval` recebe `List<?>` e converte para `List<ParSubstituicao>` internamente, porque o JavaCC gera `List` sem tipo.
* Se `re`, `im`, `abs` ou `conj` forem aplicados a um inteiro, a checagem de tipos falha **antes** da execução (testes `f1_erro_re_inteiro`, `f2_erro_im_inteiro`).

**Por que `simplify` e `eval` são separados.** As operações aritméticas só dobram constantes, sem reescrever a estrutura. Assim `complex(sym("x"), 4) * 5` mostra o resultado "cru" e `simplify` é uma etapa explícita, que o usuário pede quando quer. `eval` é a etapa que transforma símbolos em números.

### 3.7 Operador unário `-`

`ExpMenos.avaliar` ganhou um ramo para `ValorSimbolico` (usando `ValorSimbolico.neg`). **A checagem de tipos de `ExpMenos` não foi alterada**: continua exigindo `INTEIRO`, então `-complex(1, 2)` é rejeitado na checagem. Para negar um complexo use `conj` ou `complex(0, 0) - z`.

### 3.8 Parser (`Funcional3.jj`)

* **Novas palavras reservadas:** `complex`, `re`, `im`, `abs`, `conj`, `simplify`, `sym`, `eval`. O JavaCC dá prioridade a tokens declarados antes de `IDENTIFIER`, então **estes nomes deixam de poder ser usados como identificadores** (por exemplo `var re = 1`). É o custo de uma sintaxe de chamada sem prefixo especial.
* **Novas produções:** `PValorSimbolico`, `PExpComplexa`, `PExpRe`, `PExpIm`, `PExpAbs`, `PExpConj`, `PExpSimplify`, `PExpEval`, `PListaParesSubstituicao` e `PParSubstituicao`. `PValorSimbolico` entra em `PValor` (por isso `sym("x")` é um valor), e as demais entram em `PExpUnaria`.
* **Operador `/`:** nova alternativa em `PExpBinaria`, com a mesma estrutura de `+` e `-` (operando direito é `PExpUnaria`, associativa à esquerda). `*`, herdado da linguagem original, usa `PExpressao` no lado direito. Portanto `a * b + c` é lido como `a * (b + c)`. Isso é anterior à extensão e não foi alterado; use parênteses nas expressões mistas.
* **Impressão do resultado:** `main` imprime `ValorComplexo` e `ValorSimbolico` (e, por último, qualquer outro `Valor`) usando `toString()`. Sem isso, o resultado de um programa complexo não seria exibido.
* **Imports:** as classes novas são importadas no cabeçalho do parser.

### 3.9 Reorganização de `ExpMult`

`ExpMult` morava em `functional3.expression`, enquanto `ExpSoma` e `ExpSub` estão em `expressions2.expression`. Ela foi movida para junto das demais aritméticas, onde `ExpDiv` também foi criada, e o import em `PartialInstantiatorVisitor` foi ajustado. O import de `DeclaracaoFuncional`, que não era usado nesse arquivo, foi removido.

## 4. Testes

A pasta [Funcional3/testes](Funcional3/testes) tem 63 programas. A primeira linha de cada um é `// esperado: <resultado>`, e o script [rodar.sh](Funcional3/testes/rodar.sh) compila, executa e compara. Valores especiais indicam falhas esperadas: `erro-sintaxe`, `erro-tipo`, `erro-variavel`, `erro-execucao`.

| Prefixo | Cobre |
| :-- | :-- |
| `f1_*` | tipo complexo: construção, `re`, `im`, `conj`, soma, erros de tipo |
| `f2_*` | `-`, `*`, `/`, `abs`, truncamento, divisão por zero |
| `f3_*` | símbolos, `simplify`, `eval` |
| `f4_*` | complexos em funções, listas, `map`/`filter`/`fold`, compreensão de listas |
| `readme_*` | exemplos das seções 3.1 a 3.4 do README |
| `base_*` | regressão: programas sem complexos continuam funcionando |

Exemplos (valor esperado declarado no arquivo):

```
complex(1, 2) * complex(3, 4)                                   // complex(-5, 10)
simplify(complex(sym("x"), 4) * 5)                              // complex(5*x, 20)
eval(complex(sym("x"), sym("y")) * complex(2, 1),
     [("x", 1), ("y", 3)])                                      // complex(-1, 7)
complex(1, 1) / complex(0, 0)                                   // erro-execucao
complex(1, 2) + true                                            // erro-tipo
```

Para executar: `cd Funcional3 && testes/rodar.sh` (veja [SETUP.md](SETUP.md) para o ambiente).
