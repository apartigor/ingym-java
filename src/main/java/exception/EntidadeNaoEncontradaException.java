package exception;

// excecao personalizada pra quando um recurso nao e encontrado no banco
public class EntidadeNaoEncontradaException extends RuntimeException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
