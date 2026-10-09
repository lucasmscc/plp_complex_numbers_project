package lf3.plp.expressions2.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Um objeto desta classe representa o modulo de um numero complexo,
 * truncado para inteiro: <code>abs(complex(3, 4))</code> resulta em <code>5</code>.
 */
public class ExpAbs extends ExpUnaria {

	/**
	 * Controi uma expressao que calcula o modulo de <code>exp</code>.
	 * Assume-se que <code>exp</code> resulta em <code>ValorComplexo</code>.
	 *
	 * @param exp expressao complexa
	 */
	public ExpAbs(Expressao exp) {
		super(exp, "abs");
	}

	/**
	 * Retorna o modulo (piso de <code>raiz(a^2 + b^2)</code>) do complexo avaliado.
	 */
	public Valor avaliar(AmbienteExecucao amb) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		return ValorComplexo.abs((ValorComplexo) getExp().avaliar(amb));
	}

	/**
	 * Realiza a verificacao de tipos desta expressao: o argumento deve ser complexo.
	 */
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao amb)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		return getExp().getTipo(amb).eComplexo();
	}

	/**
	 * Retorna o tipo desta expressao: inteiro.
	 */
	public Tipo getTipo(AmbienteCompilacao amb) {
		return TipoPrimitivo.INTEIRO;
	}

	@Override
	public String toString() {
		return String.format("abs(%s)", exp);
	}

	@Override
	public ExpUnaria clone() {
		return new ExpAbs(exp.clone());
	}
}
