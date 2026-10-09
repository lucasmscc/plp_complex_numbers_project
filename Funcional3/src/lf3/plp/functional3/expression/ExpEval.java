package lf3.plp.functional3.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.expression.ValorInteiro;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * <code>eval(e, [("x", e1), ("y", e2)])</code>: avalia <code>e</code>,
 * substitui as incognitas listadas pelos valores de <code>e1</code>,
 * <code>e2</code>... (todas de uma vez) e devolve o resultado. Se nao sobrar
 * incognita, o resultado e numerico.
 */
public class ExpEval implements Expressao {

	private final Expressao exp;

	private final List<ParSubstituicao> pares;

	/**
	 * @param exp a expressao simbolica a ser avaliada.
	 * @param pares lista de {@link ParSubstituicao} (lista crua porque o
	 *        parser JavaCC devolve <code>List</code> sem tipo); pode ser
	 *        <code>null</code> ou vazia.
	 */
	public ExpEval(Expressao exp, List<?> pares) {
		this.exp = exp;
		this.pares = new ArrayList<ParSubstituicao>();
		if (pares != null) {
			for (Object p : pares) {
				this.pares.add((ParSubstituicao) p);
			}
		}
	}

	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Map<String, Valor> substituicoes = new HashMap<String, Valor>();
		for (ParSubstituicao p : pares) {
			Valor v = p.getValor().avaliar(amb);
			if (!(v instanceof ValorInteiro) && !(v instanceof ValorSimbolico)) {
				throw new IllegalArgumentException(
					"eval: o valor de \"" + p.getNome()
						+ "\" deve ser inteiro ou simbolico, mas e " + v);
			}
			substituicoes.put(p.getNome(), v);
		}
		return substituir(exp.avaliar(amb), substituicoes);
	}

	private static Valor substituir(Valor v, Map<String, Valor> substituicoes) {
		if (v instanceof ValorSimbolico) {
			return ((ValorSimbolico) v).substituir(substituicoes);
		}
		if (v instanceof ValorComplexo) {
			ValorComplexo z = (ValorComplexo) v;
			return new ValorComplexo(
				substituir(z.getReal(), substituicoes),
				substituir(z.getImag(), substituicoes));
		}
		return v;
	}

	/**
	 * A expressao deve ser inteira ou complexa e cada valor de substituicao
	 * deve ser inteiro.
	 */
	public boolean checaTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		if (!exp.checaTipo(amb)) {
			return false;
		}
		Tipo t = exp.getTipo(amb);
		if (!t.equals(TipoPrimitivo.INTEIRO) && !t.equals(TipoPrimitivo.COMPLEXO)) {
			return false;
		}
		for (ParSubstituicao p : pares) {
			Expressao v = p.getValor();
			if (!v.checaTipo(amb) || !v.getTipo(amb).equals(TipoPrimitivo.INTEIRO)) {
				return false;
			}
		}
		return true;
	}

	public Tipo getTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return exp.getTipo(amb);
	}

	public Expressao reduzir(AmbienteExecucao ambiente) {
		List<ParSubstituicao> reduzidos = new ArrayList<ParSubstituicao>();
		for (ParSubstituicao p : pares) {
			reduzidos.add(new ParSubstituicao(p.getNome(), p.getValor().reduzir(ambiente)));
		}
		return new ExpEval(exp.reduzir(ambiente), reduzidos);
	}

	@Override
	public ExpEval clone() {
		List<ParSubstituicao> copia = new ArrayList<ParSubstituicao>();
		for (ParSubstituicao p : pares) {
			copia.add(new ParSubstituicao(p.getNome(), (Expressao) p.getValor().clone()));
		}
		return new ExpEval((Expressao) exp.clone(), copia);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("eval(").append(exp).append(", [");
		for (int i = 0; i < pares.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(pares.get(i));
		}
		return sb.append("])").toString();
	}
}