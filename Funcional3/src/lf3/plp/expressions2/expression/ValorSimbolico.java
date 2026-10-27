package lf3.plp.expressions2.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;

public class ValorSimbolico implements Valor {

	private final Expressao expressao;

	public ValorSimbolico(String simbolo) {
		this(new Id(simbolo));
	}

	public ValorSimbolico(Expressao expressao) {
		this.expressao = expressao;
	}

	public Expressao getExpressao() {
		return expressao;
	}

	public String getNome() {
		if (expressao instanceof Id) {
			return ((Id) expressao).getIdName();
		}
		return expressao.toString();
	}

	@Override
	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return this;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return true;
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return TipoPrimitivo.INTEIRO;
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		return this;
	}

	@Override
	public ValorSimbolico clone() {
		return new ValorSimbolico(this.expressao.clone());
	}

	@Override
	public String toString() {
		return expressao.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		ValorSimbolico other = (ValorSimbolico) obj;
		return expressao.equals(other.expressao);
	}

	@Override
	public int hashCode() {
		return expressao.hashCode();
	}
}
