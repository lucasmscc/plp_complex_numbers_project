package lf3.plp.functional3.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * <code>simplify(e)</code>: simplifica algebricamente o resultado de
 * <code>e</code>. Aceita inteiro, simbolico ou complexo (cujas partes sao
 * simplificadas uma a uma); o tipo do resultado e o mesmo de <code>e</code>.
 */
public class ExpSimplify implements Expressao {

	private final Expressao exp;

	public ExpSimplify(Expressao exp) {
		this.exp = exp;
	}

	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return simplificar(exp.avaliar(amb));
	}

	private static Valor simplificar(Valor v) {
		if (v instanceof ValorSimbolico) {
			return ((ValorSimbolico) v).simplificar();
		}
		if (v instanceof ValorComplexo) {
			ValorComplexo z = (ValorComplexo) v;
			return new ValorComplexo(simplificar(z.getReal()), simplificar(z.getImag()));
		}
		return v;
	}

	/**
	 * O operando deve ser inteiro (ou simbolico) ou complexo.
	 */
	public boolean checaTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		if (!exp.checaTipo(amb)) {
			return false;
		}
		Tipo t = exp.getTipo(amb);
		return t.equals(TipoPrimitivo.INTEIRO) || t.equals(TipoPrimitivo.COMPLEXO);
	}

	public Tipo getTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return exp.getTipo(amb);
	}

	public Expressao reduzir(AmbienteExecucao ambiente) {
		return new ExpSimplify(exp.reduzir(ambiente));
	}

	@Override
	public ExpSimplify clone() {
		return new ExpSimplify((Expressao) exp.clone());
	}

	@Override
	public String toString() {
		return "simplify(" + exp + ")";
	}
}