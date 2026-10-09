package lf3.plp.functional3.expression;

import java.util.Map;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.expression.ValorInteiro;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;

/**
 * Valor simbolico: ou uma incognita (<code>sym("x")</code>) ou uma expressao
 * aritmetica sobre inteiros e incognitas, ainda nao reduzida a um numero
 * (por exemplo <code>x*5 - 4*0</code>). Objetos desta classe sao imutaveis.
 * <p>
 * Toda operacao aritmetica passa por {@link #soma}, {@link #sub},
 * {@link #mult}, {@link #div}, {@link #neg} e {@link #raiz}: se os dois
 * operandos forem <code>ValorInteiro</code> o resultado e um
 * <code>ValorInteiro</code> (mesma aritmetica <code>int</code> do Java);
 * se algum for simbolico, o resultado e um novo <code>ValorSimbolico</code>.
 * Assim, uma expressao sem incognitas continua se comportando como antes.
 */
public class ValorSimbolico implements Valor {

	private static final char SOMA = '+';
	private static final char SUB = '-';
	private static final char MULT = '*';
	private static final char DIV = '/';
	private static final char NEG = '~';
	/** piso(raiz(e)), usado por abs sobre complexos simbolicos. */
	private static final char RAIZ = 'R';

	/** Nome da incognita; <code>null</code> se for no composto. */
	private final String nome;

	/** Operador do no composto; 0 se for incognita. */
	private final char op;

	/** Operandos do no composto (<code>dir</code> e null em NEG e RAIZ). */
	private final Valor esq;
	private final Valor dir;

	/**
	 * Cria uma incognita.
	 *
	 * @param nome o nome da incognita, por exemplo <code>"x"</code>.
	 */
	public ValorSimbolico(String nome) {
		if (nome == null || nome.length() == 0) {
			throw new IllegalArgumentException("nome de simbolo vazio");
		}
		this.nome = nome;
		this.op = 0;
		this.esq = null;
		this.dir = null;
	}

	private ValorSimbolico(char op, Valor esq, Valor dir) {
		this.nome = null;
		this.op = op;
		this.esq = esq;
		this.dir = dir;
	}

	/** Nome da incognita, ou <code>null</code> se for uma expressao composta. */
	public String getNome() {
		return nome;
	}

	// ------------------------------------------------------------------
	// Aritmetica (inteiro/simbolico)
	// ------------------------------------------------------------------

	public static Valor soma(Valor a, Valor b) {
		return operar(SOMA, a, b);
	}

	public static Valor sub(Valor a, Valor b) {
		return operar(SUB, a, b);
	}

	public static Valor mult(Valor a, Valor b) {
		return operar(MULT, a, b);
	}

	/**
	 * Divisao. Entre inteiros trunca em direcao a zero.
	 *
	 * @throws ArithmeticException divisao inteira por zero.
	 */
	public static Valor div(Valor a, Valor b) {
		return operar(DIV, a, b);
	}

	public static Valor neg(Valor a) {
		verifica(a);
		if (a instanceof ValorInteiro) {
			return new ValorInteiro(-inteiro(a));
		}
		return new ValorSimbolico(NEG, a, null);
	}

	/**
	 * Piso da raiz quadrada. Entre inteiros: <code>(int) Math.sqrt(a)</code>.
	 *
	 * @throws ArithmeticException se <code>a</code> for um inteiro negativo.
	 */
	public static Valor raiz(Valor a) {
		verifica(a);
		if (a instanceof ValorInteiro) {
			int x = inteiro(a);
			if (x < 0) {
				throw new ArithmeticException("raiz de numero negativo: " + x);
			}
			return new ValorInteiro((int) Math.sqrt(x));
		}
		return new ValorSimbolico(RAIZ, a, null);
	}

	private static Valor operar(char op, Valor a, Valor b) {
		verifica(a);
		verifica(b);
		if (a instanceof ValorInteiro && b instanceof ValorInteiro) {
			int x = inteiro(a);
			int y = inteiro(b);
			switch (op) {
			case SOMA:
				return new ValorInteiro(x + y);
			case SUB:
				return new ValorInteiro(x - y);
			case MULT:
				return new ValorInteiro(x * y);
			case DIV:
				return new ValorInteiro(x / y);
			default:
				throw new IllegalStateException("operador desconhecido: " + op);
			}
		}
		return new ValorSimbolico(op, a, b);
	}

	private static void verifica(Valor v) {
		if (!(v instanceof ValorInteiro) && !(v instanceof ValorSimbolico)) {
			throw new IllegalArgumentException(
				"operando deve ser inteiro ou simbolico: " + v);
		}
	}

	private static int inteiro(Valor v) {
		return ((ValorInteiro) v).valor();
	}

	// ------------------------------------------------------------------
	// Substituicao (eval)
	// ------------------------------------------------------------------

	/**
	 * Substitui, de uma so vez, as incognitas presentes em
	 * <code>valores</code>. Os valores substituidos nao sao re-substituidos,
	 * entao <code>[("x", sym("y")), ("y", sym("x"))]</code> troca x e y.
	 * Incognitas ausentes do mapa permanecem. Os valores do mapa devem ser
	 * <code>ValorInteiro</code> ou <code>ValorSimbolico</code>.
	 *
	 * @return um <code>ValorInteiro</code> se nao sobrar incognita.
	 */
	public Valor substituir(Map<String, Valor> valores) {
		if (nome != null) {
			Valor v = valores.get(nome);
			return v != null ? v : this;
		}
		Valor e = substituirEm(esq, valores);
		Valor d = dir == null ? null : substituirEm(dir, valores);
		return refazer(op, e, d);
	}

	private static Valor substituirEm(Valor v, Map<String, Valor> valores) {
		if (v instanceof ValorSimbolico) {
			return ((ValorSimbolico) v).substituir(valores);
		}
		return v;
	}

	private static Valor refazer(char op, Valor a, Valor b) {
		switch (op) {
		case NEG:
			return neg(a);
		case RAIZ:
			return raiz(a);
		default:
			return operar(op, a, b);
		}
	}

	// ------------------------------------------------------------------
	// Simplificacao (simplify)
	// ------------------------------------------------------------------

	/**
	 * Simplifica de baixo para cima: dobra constantes e aplica
	 * <code>e+0=e</code>, <code>e-0=e</code>, <code>0-e=-e</code>,
	 * <code>e-e=0</code>, <code>e*1=e</code>, <code>e*0=0</code>,
	 * <code>e/1=e</code>, <code>-(-e)=e</code>, e escreve o coeficiente
	 * inteiro de um produto a esquerda (<code>x*5</code> vira
	 * <code>5*x</code>). As regras ignoram o caso de divisao por zero
	 * escondida dentro de uma incognita.
	 */
	public Valor simplificar() {
		if (nome != null) {
			return this;
		}
		Valor a = simplificarValor(esq);
		Valor b = dir == null ? null : simplificarValor(dir);
		return regra(op, a, b);
	}

	private static Valor simplificarValor(Valor v) {
		if (v instanceof ValorSimbolico) {
			return ((ValorSimbolico) v).simplificar();
		}
		return v;
	}

	private static Valor regra(char op, Valor a, Valor b) {
		switch (op) {
		case NEG:
			if (a instanceof ValorSimbolico && ((ValorSimbolico) a).op == NEG) {
				return ((ValorSimbolico) a).esq;
			}
			return neg(a);
		case RAIZ:
			return raiz(a);
		case SOMA:
			if (ehInteiro(a, 0)) {
				return b;
			}
			if (ehInteiro(b, 0)) {
				return a;
			}
			return soma(a, b);
		case SUB:
			if (ehInteiro(b, 0)) {
				return a;
			}
			if (ehInteiro(a, 0)) {
				return regra(NEG, b, null);
			}
			if (a.equals(b)) {
				return new ValorInteiro(0);
			}
			return sub(a, b);
		case MULT:
			if (ehInteiro(a, 0) || ehInteiro(b, 0)) {
				return new ValorInteiro(0);
			}
			if (ehInteiro(a, 1)) {
				return b;
			}
			if (ehInteiro(b, 1)) {
				return a;
			}
			if (b instanceof ValorInteiro && !(a instanceof ValorInteiro)) {
				return mult(b, a);
			}
			return mult(a, b);
		case DIV:
			if (ehInteiro(b, 1)) {
				return a;
			}
			return div(a, b);
		default:
			throw new IllegalStateException("operador desconhecido: " + op);
		}
	}

	private static boolean ehInteiro(Valor v, int n) {
		return v instanceof ValorInteiro && inteiro(v) == n;
	}

	// ------------------------------------------------------------------
	// Valor / Expressao
	// ------------------------------------------------------------------

	/**
	 * Retorna o proprio valor.
	 */
	public Valor avaliar(AmbienteExecucao amb) {
		return this;
	}

	/**
	 * Sempre valido.
	 */
	public boolean checaTipo(AmbienteCompilacao amb) {
		return true;
	}

	/**
	 * Uma incognita representa um inteiro ainda desconhecido, entao tem tipo
	 * inteiro; assim <code>sym("x") + 1</code> e
	 * <code>complex(sym("x"), 2)</code> passam na checagem de tipos.
	 */
	public Tipo getTipo(AmbienteCompilacao amb) {
		return TipoPrimitivo.INTEIRO;
	}

	public Expressao reduzir(AmbienteExecucao ambiente) {
		return this;
	}

	/** Imutavel, entao nao precisa copiar. */
	@Override
	public ValorSimbolico clone() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ValorSimbolico o = (ValorSimbolico) obj;
		return op == o.op && iguais(nome, o.nome) && iguais(esq, o.esq)
				&& iguais(dir, o.dir);
	}

	private static boolean iguais(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	@Override
	public int hashCode() {
		int h = op;
		h = 31 * h + (nome == null ? 0 : nome.hashCode());
		h = 31 * h + (esq == null ? 0 : esq.hashCode());
		h = 31 * h + (dir == null ? 0 : dir.hashCode());
		return h;
	}

	/**
	 * Escreve no estilo do README: <code>a*c - b*d</code>,
	 * <code>(a*c + b*d)/(c*c + d*d)</code>, parenteses so quando preciso.
	 */
	@Override
	public String toString() {
		if (nome != null) {
			return nome;
		}
		switch (op) {
		case NEG: {
			String s = texto(esq);
			if (prec(esq) < 4 || negativo(esq)) {
				s = "(" + s + ")";
			}
			return "-" + s;
		}
		case RAIZ:
			return "sqrt(" + texto(esq) + ")";
		default: {
			int p = prec(this);
			String l = texto(esq);
			String r = texto(dir);
			if (prec(esq) < p) {
				l = "(" + l + ")";
			}
			boolean mesmaPrec = prec(dir) == p;
			boolean assoc = op == SOMA
					|| (op == MULT && mesmaPrec && ((ValorSimbolico) dir).op == MULT);
			if (prec(dir) < p || (mesmaPrec && !assoc) || negativo(dir)) {
				r = "(" + r + ")";
			}
			boolean aditivo = op == SOMA || op == SUB;
			return l + (aditivo ? " " + op + " " : String.valueOf(op)) + r;
		}
		}
	}

	private static int prec(Valor v) {
		if (v instanceof ValorSimbolico) {
			switch (((ValorSimbolico) v).op) {
			case SOMA:
			case SUB:
				return 1;
			case MULT:
			case DIV:
				return 2;
			case NEG:
				return 3;
			default:
				return 4;
			}
		}
		return 4;
	}

	private static boolean negativo(Valor v) {
		return v instanceof ValorInteiro && inteiro(v) < 0;
	}

	private static String texto(Valor v) {
		if (v instanceof ValorInteiro) {
			return String.valueOf(inteiro(v));
		}
		return v.toString();
	}
}