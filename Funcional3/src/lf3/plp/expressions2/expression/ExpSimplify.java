package lf3.plp.expressions2.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import lf3.plp.functional3.expression.ExpDiv;
import lf3.plp.functional3.expression.ExpMult;
import lf3.plp.functional3.expression.ValorLista;

public class ExpSimplify extends ExpUnaria {

	public ExpSimplify(Expressao exp) {
		super(exp, "simplify");
	}

	@Override
	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Expressao simplificada = simplificar(this.getExp());
		if (simplificada instanceof Valor) {
			return (Valor) simplificada;
		}
		return (Valor) simplificada.avaliar(amb);
	}

	private Expressao simplificar(Expressao expressao) {
		if (expressao instanceof ExpSoma) {
			ExpSoma soma = (ExpSoma) expressao;
			Expressao esq = simplificar(soma.getEsq());
			Expressao dir = simplificar(soma.getDir());
			if (isZero(esq)) {
				return dir;
			}
			if (isZero(dir)) {
				return esq;
			}
			if (esq instanceof ValorInteiro && dir instanceof ValorInteiro) {
				return new ValorInteiro(((ValorInteiro) esq).valor() + ((ValorInteiro) dir).valor());
			}
			return new ExpSoma(esq, dir);
		}
		if (expressao instanceof ExpSub) {
			ExpSub sub = (ExpSub) expressao;
			Expressao esq = simplificar(sub.getEsq());
			Expressao dir = simplificar(sub.getDir());
			if (isZero(dir)) {
				return esq;
			}
			if (esq.equals(dir)) {
				return new ValorInteiro(0);
			}
			if (esq instanceof ValorInteiro && dir instanceof ValorInteiro) {
				return new ValorInteiro(((ValorInteiro) esq).valor() - ((ValorInteiro) dir).valor());
			}
			return new ExpSub(esq, dir);
		}
		if (expressao instanceof ExpMult) {
			ExpMult mult = (ExpMult) expressao;
			Expressao esq = simplificar(mult.getEsq());
			Expressao dir = simplificar(mult.getDir());
			if (isZero(esq) || isZero(dir)) {
				return new ValorInteiro(0);
			}
			if (isUm(esq)) {
				return dir;
			}
			if (isUm(dir)) {
				return esq;
			}
			if (esq instanceof ValorInteiro && dir instanceof ValorInteiro) {
				return new ValorInteiro(((ValorInteiro) esq).valor() * ((ValorInteiro) dir).valor());
			}
			if (esq instanceof ValorComplexo || dir instanceof ValorComplexo) {
				ValorComplexo esqC = esq instanceof ValorComplexo ? (ValorComplexo) esq : ValorComplexo.promover((Valor) esq);
				ValorComplexo dirC = dir instanceof ValorComplexo ? (ValorComplexo) dir : ValorComplexo.promover((Valor) dir);
				ValorComplexo residual = ValorComplexo.multResidual(esqC, dirC);
				ValorComplexo normalizado = new ValorComplexo(
					(Valor) valorOuExpressao(simplificar(residual.getReal())),
					(Valor) valorOuExpressao(simplificar(residual.getImag())));
				return simplificar(normalizado);
			}
			if (esq instanceof ValorInteiro && !(dir instanceof ValorInteiro)) {
				return new ExpMult(esq, dir);
			}
			if (dir instanceof ValorInteiro && !(esq instanceof ValorInteiro)) {
				return new ExpMult(dir, esq);
			}
			return new ExpMult(esq, dir);
		}
		if (expressao instanceof ExpDiv) {
			ExpDiv div = (ExpDiv) expressao;
			Expressao esq = simplificar(div.getEsq());
			Expressao dir = simplificar(div.getDir());
			if (isZero(esq)) {
				return new ValorInteiro(0);
			}
			if (isUm(dir)) {
				return esq;
			}
			if (esq instanceof ValorInteiro && dir instanceof ValorInteiro) {
				int divisor = ((ValorInteiro) dir).valor();
				if (divisor == 0) {
					throw new ArithmeticException("divisao por zero");
				}
				return new ValorInteiro(((ValorInteiro) esq).valor() / divisor);
			}
			if (esq instanceof ValorComplexo || dir instanceof ValorComplexo) {
				ValorComplexo esqC = esq instanceof ValorComplexo ? (ValorComplexo) esq : ValorComplexo.promover((Valor) esq);
				ValorComplexo dirC = dir instanceof ValorComplexo ? (ValorComplexo) dir : ValorComplexo.promover((Valor) dir);
				ValorComplexo residual = ValorComplexo.divResidual(esqC, dirC);
				return new ValorComplexo(
					(Valor) valorOuExpressao(simplificar(residual.getReal())),
					(Valor) valorOuExpressao(simplificar(residual.getImag())));
			}
			return new ExpDiv(esq, dir);
		}
		if (expressao instanceof ExpComplexa) {
			ExpComplexa complexa = (ExpComplexa) expressao;
			Expressao real = simplificar(complexa.getEsq());
			Expressao imag = simplificar(complexa.getDir());
			if (real instanceof Valor && imag instanceof Valor) {
				return new ValorComplexo((Valor) real, (Valor) imag);
			}
			return new ExpComplexa(real, imag);
		}
		if (expressao instanceof ExpRe) {
			return new ExpRe(simplificar(((ExpRe) expressao).getExp()));
		}
		if (expressao instanceof ExpIm) {
			return new ExpIm(simplificar(((ExpIm) expressao).getExp()));
		}
		if (expressao instanceof ExpConj) {
			return new ExpConj(simplificar(((ExpConj) expressao).getExp()));
		}
		if (expressao instanceof ExpAbs) {
			return new ExpAbs(simplificar(((ExpAbs) expressao).getExp()));
		}
		if (expressao instanceof ExpMenos) {
			return new ExpMenos(simplificar(((ExpMenos) expressao).getExp()));
		}
		if (expressao instanceof ValorComplexo) {
			ValorComplexo z = (ValorComplexo) expressao;
			return new ValorComplexo(
				(Valor) valorOuExpressao(simplificar(z.getReal())),
				(Valor) valorOuExpressao(simplificar(z.getImag())));
		}
		if (expressao instanceof ValorSimbolico) {
			ValorSimbolico simbolo = (ValorSimbolico) expressao;
			Expressao interno = simplificar(simbolo.getExpressao());
			if (interno instanceof Valor) {
				return (Valor) interno;
			}
			return new ValorSimbolico(interno);
		}
		if (expressao instanceof ValorLista) {
			ValorLista lista = (ValorLista) expressao;
			ValorLista retorno = ValorLista.getInstancia(null, null);
			ValorLista atual = lista.inverter();
			while (atual != null && !atual.isEmpty()) {
				retorno.cons(simplificar(atual.getHead()));
				atual = atual.getTail();
			}
			return retorno.inverter();
		}
		return expressao;
	}

	private Valor valorOuExpressao(Expressao expressao) {
		if (expressao instanceof Valor) {
			return (Valor) expressao;
		}
		return new ValorSimbolico(expressao);
	}

	private boolean isZero(Expressao expressao) {
		return expressao instanceof ValorInteiro && ((ValorInteiro) expressao).valor() == 0;
	}

	private boolean isUm(Expressao expressao) {
		return expressao instanceof ValorInteiro && ((ValorInteiro) expressao).valor() == 1;
	}

	@Override
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return getExp().getTipo(ambiente).eInteiro() || getExp().getTipo(ambiente).eComplexo();
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao ambiente) {
		return TipoPrimitivo.INTEIRO;
	}

	@Override
	public ExpUnaria clone() {
		return new ExpSimplify(exp.clone());
	}
}
