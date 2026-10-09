package lf3.plp.expressions2.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Um objeto desta classe representa a parte real de um complexo,
 * <code>re(e)</code>.
 */
public class ExpRe extends ExpUnaria {

	/**
	 * @param exp Expressao cuja avaliacao resulta <code>ValorComplexo</code>.
	 */
	public ExpRe(Expressao exp) {
		super(exp, "re");
	}

	public Valor avaliar(AmbienteExecucao amb) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		return ((ValorComplexo) getExp().avaliar(amb)).getReal();
	}

	protected boolean checaTipoElementoTerminal(AmbienteCompilacao amb)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		return getExp().getTipo(amb).eComplexo();
	}

	public Tipo getTipo(AmbienteCompilacao amb) {
		return TipoPrimitivo.INTEIRO;
	}

	@Override
	public String toString() {
		return String.format("re(%s)", exp);
	}

	@Override
	public ExpUnaria clone() {
		return new ExpRe(exp.clone());
	}
}
