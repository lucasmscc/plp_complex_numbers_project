package lf3.plp.expressions2.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Um objeto desta classe representa o conjugado de um complexo,
 * <code>conj(e)</code>: <code>conj(a + bi) = a - bi</code>.
 */
public class ExpConj extends ExpUnaria {

	/**
	 * @param exp Expressao cuja avaliacao resulta <code>ValorComplexo</code>.
	 */
	public ExpConj(Expressao exp) {
		super(exp, "conj");
	}

	public Valor avaliar(AmbienteExecucao amb) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		ValorComplexo z = (ValorComplexo) getExp().avaliar(amb);
		return new ValorComplexo(z.getReal(), new ExpMenos(z.getImag()).avaliar(amb));
	}

	protected boolean checaTipoElementoTerminal(AmbienteCompilacao amb)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		return getExp().getTipo(amb).eComplexo();
	}

	public Tipo getTipo(AmbienteCompilacao amb) {
		return TipoPrimitivo.COMPLEXO;
	}

	@Override
	public String toString() {
		return String.format("conj(%s)", exp);
	}

	@Override
	public ExpUnaria clone() {
		return new ExpConj(exp.clone());
	}
}
