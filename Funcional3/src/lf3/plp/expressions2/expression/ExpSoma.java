package lf3.plp.expressions2.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import lf3.plp.functional3.expression.ValorComplexo;
import lf3.plp.functional3.expression.ValorSimbolico;

/**
 * Um objeto desta classe representa uma Expressao de Soma.
 */
public class ExpSoma extends ExpBinaria {

	/**
	 * Controi uma Expressao de Soma com as sub-expressoes especificadas.
	 * Assume-se que estas sub-expressoes resultam em <code>ValorInteiro</code> 
	 * quando avaliadas.
	 * @param esq Expressao da esquerda
	 * @param dir Expressao da direita
	 */
	public ExpSoma(Expressao esq, Expressao dir) {
		super(esq, dir, "+");
	}

	/**
	 * Retorna o valor da Expressao de Soma
	 */
	public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Valor esqAvaliado = getEsq().avaliar(amb);
		Valor dirAvaliado = getDir().avaliar(amb);
		if (esqAvaliado instanceof ValorComplexo || dirAvaliado instanceof ValorComplexo) {
			return ValorComplexo.soma(ValorComplexo.promover(esqAvaliado), ValorComplexo.promover(dirAvaliado));
		}
		if (esqAvaliado instanceof ValorSimbolico || dirAvaliado instanceof ValorSimbolico) {
			return ValorSimbolico.soma(esqAvaliado, dirAvaliado);
		}
		return new ValorInteiro(
			((ValorInteiro) esqAvaliado).valor() +
			((ValorInteiro) dirAvaliado).valor() );
	}
	
	/**
	 * Realiza a verificacao de tipos desta expressao.
	 *
	 * @param ambiente o ambiente de compila��o.
	 * @return <code>true</code> se os tipos da expressao sao validos;
	 *          <code>false</code> caso contrario.
	 * @exception VariavelNaoDeclaradaException se existir um identificador
	 *          nao declarado no ambiente.
	 * @exception VariavelNaoDeclaradaException se existir um identificador
	 *          declarado mais de uma vez no mesmo bloco do ambiente.
	 */
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tipoEsq = getEsq().getTipo(ambiente);
		Tipo tipoDir = getDir().getTipo(ambiente);
		return ((tipoEsq.eInteiro() || tipoEsq.eComplexo()) && (tipoDir.eInteiro() || tipoDir.eComplexo()));
	}

	/**
	 * Retorna os tipos possiveis desta expressao.
	 *
	 * @param ambiente o ambiente de compila��o.
	 * @return os tipos possiveis desta expressao.
	 */
	public Tipo getTipo(AmbienteCompilacao ambiente) {
		if (getEsq().getTipo(ambiente).eComplexo() || getDir().getTipo(ambiente).eComplexo()) {
			return TipoPrimitivo.COMPLEXO;
		}
		return TipoPrimitivo.INTEIRO;
	}
	
	@Override
	public ExpBinaria clone() {
		return new ExpSoma(esq.clone(), dir.clone());
	}
}
