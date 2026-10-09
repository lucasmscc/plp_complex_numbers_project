package lf3.plp.functional3.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.expression.ValorInteiro;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;

/**
 * Objetos desta classe encapsulam um numero complexo <code>a + bi</code>.
 * As partes real e imaginaria sao <code>Valor</code>es ja avaliados:
 * <code>ValorInteiro</code> ou <code>ValorSimbolico</code>. Objetos desta
 * classe sao imutaveis.
 */
public class ValorComplexo implements Valor {

	private final Valor real;

	private final Valor imag;

	/**
	 * Cria <code>ValorComplexo</code> com as partes fornecidas.
	 * 
	 * @param real a parte real ja avaliada.
	 * @param imag a parte imaginaria ja avaliada.
	 */
	public ValorComplexo(Valor real, Valor imag) {
		this.real = real;
		this.imag = imag;
	}

	public Valor getReal() {
		return real;
	}

	public Valor getImag() {
		return imag;
	}

	/**
	 * Promove um valor a complexo: um <code>ValorComplexo</code> e retornado
	 * como esta; um inteiro <code>a</code> vira <code>complex(a, 0)</code>.
	 */
	public static ValorComplexo promover(Valor valor) {
		if (valor instanceof ValorComplexo) {
			return (ValorComplexo) valor;
		}
		return new ValorComplexo(valor, new ValorInteiro(0));
	}

	/**
	 * Soma de dois complexos: <code>(a + bi) + (c + di) = (a + c) + (b + d)i</code>.
	 * Partes simbolicas geram expressoes simbolicas (ver {@link ValorSimbolico}).
	 */
	public static ValorComplexo soma(ValorComplexo esq, ValorComplexo dir) {
		return new ValorComplexo(
			ValorSimbolico.soma(esq.real, dir.real),
			ValorSimbolico.soma(esq.imag, dir.imag));
	}

	/**
	 * Subtracao de dois complexos: <code>(a + bi) - (c + di) = (a - c) + (b - d)i</code>.
	 */
	public static ValorComplexo sub(ValorComplexo esq, ValorComplexo dir) {
		return new ValorComplexo(
			ValorSimbolico.sub(esq.real, dir.real),
			ValorSimbolico.sub(esq.imag, dir.imag));
	}

	/**
	 * Multiplicacao de dois complexos:
	 * <code>(a + bi)(c + di) = (ac - bd) + (ad + bc)i</code>.
	 * Usa <code>ValorSimbolico</code> para que partes inteiras e simbolicas
	 * sigam o mesmo caminho.
	 */
	public static ValorComplexo mult(ValorComplexo esq, ValorComplexo dir) {
		Valor a = esq.real, b = esq.imag;
		Valor c = dir.real, d = dir.imag;
		return new ValorComplexo(
			ValorSimbolico.sub(ValorSimbolico.mult(a, c), ValorSimbolico.mult(b, d)),
			ValorSimbolico.soma(ValorSimbolico.mult(a, d), ValorSimbolico.mult(b, c)));
	}

	/**
	 * Divisao de dois complexos:
	 * <code>(a + bi)/(c + di) = ((ac + bd)/(c^2 + d^2)) + ((bc - ad)/(c^2 + d^2))i</code>.
	 * Entre inteiros cada parte usa divisao inteira do Java (trunca em direcao
	 * a zero), entao o resultado pode perder precisao.
	 *
	 * @throws ArithmeticException se o divisor for <code>complex(0, 0)</code>.
	 */
	public static ValorComplexo div(ValorComplexo esq, ValorComplexo dir) {
		Valor a = esq.real, b = esq.imag;
		Valor c = dir.real, d = dir.imag;
		Valor divisor = ValorSimbolico.soma(ValorSimbolico.mult(c, c), ValorSimbolico.mult(d, d));
		if (divisor instanceof ValorInteiro && ((ValorInteiro) divisor).valor() == 0) {
			throw new ArithmeticException("divisao por zero: divisor complexo nulo");
		}
		return new ValorComplexo(
			ValorSimbolico.div(
				ValorSimbolico.soma(ValorSimbolico.mult(a, c), ValorSimbolico.mult(b, d)), divisor),
			ValorSimbolico.div(
				ValorSimbolico.sub(ValorSimbolico.mult(b, c), ValorSimbolico.mult(a, d)), divisor));
	}

	/**
	 * Conjugado: <code>conj(a + bi) = a - bi</code>.
	 */
	public static ValorComplexo conj(ValorComplexo z) {
		return new ValorComplexo(z.real, ValorSimbolico.neg(z.imag));
	}

	/**
	 * Modulo de um complexo, truncado: <code>piso(raiz(a^2 + b^2))</code>.
	 * Por exemplo, <code>abs(complex(3, 4)) = 5</code> e
	 * <code>abs(complex(1, 1)) = 1</code>. Com partes simbolicas o resultado e
	 * simbolico, por exemplo <code>sqrt(x*x + 4)</code>.
	 */
	public static Valor abs(ValorComplexo z) {
		return ValorSimbolico.raiz(
			ValorSimbolico.soma(
				ValorSimbolico.mult(z.real, z.real),
				ValorSimbolico.mult(z.imag, z.imag)));
	}

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

	public Tipo getTipo(AmbienteCompilacao amb) {
		return TipoPrimitivo.COMPLEXO;
	}

	public Expressao reduzir(AmbienteExecucao ambiente) {
		return this;
	}

	@Override
	public ValorComplexo clone() {
		return new ValorComplexo((Valor) real.clone(), (Valor) imag.clone());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ValorComplexo other = (ValorComplexo) obj;
		return real.equals(other.real) && imag.equals(other.imag);
	}

	@Override
	public int hashCode() {
		return 31 * real.hashCode() + imag.hashCode();
	}

	@Override
	public String toString() {
		return String.format("complex(%s, %s)", real, imag);
	}
}