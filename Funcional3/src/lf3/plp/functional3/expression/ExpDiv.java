package lf3.plp.functional3.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.ExpBinaria;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.expression.ValorInteiro;
import lf3.plp.expressions2.expression.ValorSimbolico;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Um objeto desta classe representa uma Expressao de Divisao.
 * Inteiro/inteiro usa a divisao inteira do Java (trunca em direcao a zero);
 * se algum lado for complexo, o outro e promovido e a divisao e feita por
 * <code>ValorComplexo.div</code>. Divisor zero lanca
 * <code>ArithmeticException</code> na execucao.
 */
public class ExpDiv extends ExpBinaria {

	/**
	 * Controi uma Expressao de Divisao com as sub-expressoes especificadas.
	 *
	 * @param esq Expressao da esquerda (dividendo)
	 * @param dir Expressao da direita (divisor)
	 */
	public ExpDiv(Expressao esq, Expressao dir) {
		super(esq, dir, "/");
	}

	/**
	 * Retorna o valor da Expressao de Divisao.
	 */
	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Valor esqAvaliado = getEsq().avaliar(amb);
		Valor dirAvaliado = getDir().avaliar(amb);
		if (esqAvaliado instanceof ValorComplexo || dirAvaliado instanceof ValorComplexo) {
			return ValorComplexo.div(ValorComplexo.promover(esqAvaliado), ValorComplexo.promover(dirAvaliado));
		}
		if (esqAvaliado instanceof ValorSimbolico || dirAvaliado instanceof ValorSimbolico) {
			return new ValorSimbolico(new ExpDiv(getEsq(), getDir()));
		}
		int divisor = ((ValorInteiro) dirAvaliado).valor();
		if (divisor == 0) {
			throw new ArithmeticException("divisao por zero");
		}
		return new ValorInteiro(((ValorInteiro) esqAvaliado).valor() / divisor);
	}

	/**
	 * Realiza a verificacao de tipos desta expressao: os dois lados devem ser
	 * inteiros ou complexos.
	 */
	@Override
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tipoEsq = getEsq().getTipo(ambiente);
		Tipo tipoDir = getDir().getTipo(ambiente);
		return (tipoEsq.eInteiro() || tipoEsq.eComplexo()) && (tipoDir.eInteiro() || tipoDir.eComplexo());
	}

	/**
	 * Retorna o tipo desta expressao: complexo se algum lado for complexo;
	 * inteiro caso contrario.
	 */
	public Tipo getTipo(AmbienteCompilacao ambiente) {
		if (getEsq().getTipo(ambiente).eComplexo() || getDir().getTipo(ambiente).eComplexo()) {
			return TipoPrimitivo.COMPLEXO;
		}
		return TipoPrimitivo.INTEIRO;
	}

	public ExpDiv clone() {
		return new ExpDiv(this.esq.clone(), this.dir.clone());
	}
}
