package lf3.plp.expressions2.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Um objeto desta classe representa a construcao de um numero complexo,
 * <code>complex(re, im)</code>. Avalia as duas partes e devolve um
 * <code>ValorComplexo</code>.
 */
public class ExpComplexa extends ExpBinaria {

	/**
	 * Constroi a expressao com as partes real e imaginaria.
	 * 
	 * @param real Expressao cuja avaliacao resulta <code>ValorInteiro</code>.
	 * @param imag Expressao cuja avaliacao resulta <code>ValorInteiro</code>.
	 */
	public ExpComplexa(Expressao real, Expressao imag) {
		super(real, imag, "complex");
	}

	/**
	 * Avalia as partes e retorna o <code>ValorComplexo</code> resultante.
	 */
	public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return new ValorComplexo(getEsq().avaliar(amb), getDir().avaliar(amb));
	}

	/**
	 * Realiza a verificacao de tipos desta expressao: as duas partes devem ser
	 * inteiras.
	 */
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return (getEsq().getTipo(ambiente).eInteiro() && getDir().getTipo(ambiente).eInteiro());
	}

	public Tipo getTipo(AmbienteCompilacao ambiente) {
		return TipoPrimitivo.COMPLEXO;
	}

	@Override
	public String toString() {
		return String.format("complex(%s, %s)", esq, dir);
	}

	@Override
	public ExpBinaria clone() {
		return new ExpComplexa(esq.clone(), dir.clone());
	}
}
