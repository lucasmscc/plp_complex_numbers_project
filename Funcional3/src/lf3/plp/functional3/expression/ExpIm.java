package lf3.plp.functional3.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.ExpUnaria;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Um objeto desta classe representa a parte imaginaria de um numero complexo:
 * <code>im(complex(a, b))</code> resulta em <code>b</code>.
 */
public class ExpIm extends ExpUnaria {

	/**
	 * Controi uma expressao que extrai a parte imaginaria de <code>exp</code>.
	 * Assume-se que <code>exp</code> resulta em <code>ValorComplexo</code>.
	 *
	 * @param exp expressao complexa
	 */
	public ExpIm(Expressao exp) {
		super(exp, "im");
	}

	/**
	 * Retorna a parte imaginaria do complexo avaliado.
	 */
	public Valor avaliar(AmbienteExecucao amb) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		return ((ValorComplexo) getExp().avaliar(amb)).getImag();
	}

	/**
	 * Realiza a verificacao de tipos desta expressao: o argumento deve ser complexo.
	 */
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao amb)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		return getExp().getTipo(amb).eComplexo();
	}

	/**
	 * Retorna o tipo desta expressao: inteiro (as partes sao inteiras).
	 */
	public Tipo getTipo(AmbienteCompilacao amb) {
		return TipoPrimitivo.INTEIRO;
	}

	@Override
	public String toString() {
		return String.format("im(%s)", exp);
	}

	@Override
	public ExpUnaria clone() {
		return new ExpIm(exp.clone());
	}
}
