package lf3.plp.functional3.expression;

import lf3.plp.expressions2.expression.Expressao;

/**
 * Par <code>("x", expressao)</code> da lista de substituicoes de
 * <code>eval</code>. Imutavel.
 */
public class ParSubstituicao {

	private final String nome;

	private final Expressao valor;

	/**
	 * @param nome o nome da incognita, sem aspas.
	 * @param valor a expressao que sera avaliada e colocada no lugar da incognita.
	 */
	public ParSubstituicao(String nome, Expressao valor) {
		this.nome = nome;
		this.valor = valor;
	}

	public String getNome() {
		return nome;
	}

	public Expressao getValor() {
		return valor;
	}

	@Override
	public String toString() {
		return "(\"" + nome + "\", " + valor + ")";
	}
}