package lf3.plp.expressions2.expression;

import java.util.HashMap;
import java.util.Map;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import lf3.plp.functional3.expression.ExpDiv;
import lf3.plp.functional3.expression.ExpMult;
import lf3.plp.functional3.expression.ValorLista;

public class ExpEval implements Expressao {

	private final Expressao expressao;
	private final Expressao simbolos;
	private final Expressao valores;

	public ExpEval(Expressao expressao, Expressao simbolos, Expressao valores) {
		this.expressao = expressao;
		this.simbolos = simbolos;
		this.valores = valores;
	}

	public Expressao getExpressao() {
		return expressao;
	}

	public Expressao getSimbolos() {
		return simbolos;
	}

	public Expressao getValores() {
		return valores;
	}

	@Override
	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Map<String, Expressao> substituicoes = montarSubstituicoes(amb);
		Expressao reduzida = substituir(expressao, substituicoes);
		return reduzida.avaliar(amb);
	}

	private Map<String, Expressao> montarSubstituicoes(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Map<String, Expressao> substituicoes = new HashMap<String, Expressao>();
		ValorLista nomes = (ValorLista) simbolos.avaliar(amb);
		ValorLista valoresLista = (ValorLista) valores.avaliar(amb);
		ValorLista nomesAtual = nomes.inverter();
		ValorLista valoresAtual = valoresLista.inverter();
		while (nomesAtual != null && !nomesAtual.isEmpty() && valoresAtual != null && !valoresAtual.isEmpty()) {
			String nome = ((ValorString) nomesAtual.getHead()).valor();
			substituicoes.put(nome, valoresAtual.getHead());
			nomesAtual = nomesAtual.getTail();
			valoresAtual = valoresAtual.getTail();
		}
		return substituicoes;
	}

	private Expressao valorOuExpressao(Expressao expressao) {
		if (expressao instanceof Valor) {
			return (Valor) expressao;
		}
		return new ValorSimbolico(expressao);
	}

	private Expressao substituir(Expressao expressao, Map<String, Expressao> substituicoes) {
		if (expressao instanceof ValorSimbolico) {
			ValorSimbolico simbolo = (ValorSimbolico) expressao;
			if (substituicoes.containsKey(simbolo.getNome())) {
				return valorOuExpressao(substituicoes.get(simbolo.getNome()));
			}
			return expressao;
		}
		if (expressao instanceof Id) {
			Id id = (Id) expressao;
			if (substituicoes.containsKey(id.getIdName())) {
				return valorOuExpressao(substituicoes.get(id.getIdName()));
			}
			return expressao;
		}
		if (expressao instanceof ValorComplexo) {
			ValorComplexo z = (ValorComplexo) expressao;
			return new ValorComplexo(
				(Valor) valorOuExpressao(substituir(z.getReal(), substituicoes)),
				(Valor) valorOuExpressao(substituir(z.getImag(), substituicoes)));
		}
		if (expressao instanceof ValorLista) {
			ValorLista lista = (ValorLista) expressao;
			ValorLista retorno = ValorLista.getInstancia(null, null);
			ValorLista atual = lista.inverter();
			while (atual != null && !atual.isEmpty()) {
				retorno.cons(substituir(atual.getHead(), substituicoes));
				atual = atual.getTail();
			}
			return retorno.inverter();
		}
		if (expressao instanceof ExpSoma) {
			ExpSoma soma = (ExpSoma) expressao;
			return new ExpSoma(substituir(soma.getEsq(), substituicoes), substituir(soma.getDir(), substituicoes));
		}
		if (expressao instanceof ExpSub) {
			ExpSub sub = (ExpSub) expressao;
			return new ExpSub(substituir(sub.getEsq(), substituicoes), substituir(sub.getDir(), substituicoes));
		}
		if (expressao instanceof ExpMult) {
			ExpMult mult = (ExpMult) expressao;
			return new ExpMult(substituir(mult.getEsq(), substituicoes), substituir(mult.getDir(), substituicoes));
		}
		if (expressao instanceof ExpDiv) {
			ExpDiv div = (ExpDiv) expressao;
			return new ExpDiv(substituir(div.getEsq(), substituicoes), substituir(div.getDir(), substituicoes));
		}
		if (expressao instanceof ExpMenos) {
			ExpMenos menos = (ExpMenos) expressao;
			return new ExpMenos(substituir(menos.getExp(), substituicoes));
		}
		if (expressao instanceof ExpRe) {
			return new ExpRe(substituir(((ExpRe) expressao).getExp(), substituicoes));
		}
		if (expressao instanceof ExpIm) {
			return new ExpIm(substituir(((ExpIm) expressao).getExp(), substituicoes));
		}
		if (expressao instanceof ExpConj) {
			return new ExpConj(substituir(((ExpConj) expressao).getExp(), substituicoes));
		}
		if (expressao instanceof ExpAbs) {
			return new ExpAbs(substituir(((ExpAbs) expressao).getExp(), substituicoes));
		}
		if (expressao instanceof ExpComplexa) {
			ExpComplexa complexa = (ExpComplexa) expressao;
			return new ExpComplexa(substituir(complexa.getEsq(), substituicoes), substituir(complexa.getDir(), substituicoes));
		}
		if (expressao instanceof ExpSimplify) {
			return new ExpSimplify(substituir(((ExpSimplify) expressao).getExp(), substituicoes));
		}
		return expressao;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return expressao.checaTipo(amb);
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return expressao.getTipo(amb);
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		return this;
	}

	@Override
	public ExpEval clone() {
		return new ExpEval(expressao.clone(), simbolos.clone(), valores.clone());
	}

	@Override
	public String toString() {
		return String.format("eval(%s, %s, %s)", expressao, simbolos, valores);
	}
}
