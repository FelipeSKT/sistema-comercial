package Rinamed.sistema_comercial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class EstoqueService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional
    public String registrarMovimentacao(Long produtoId, TipoMovimentacao tipo, Integer quantidade, String descricao, Boolean porEmbalagem, BigDecimal valorVenda) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        System.out.println("--- DEBUG ---");
        System.out.println("Produto: " + produto.getName());
        System.out.println("Checkbox marcado? " + porEmbalagem);
        System.out.println("Tamanho da Embalagem no Banco: " + produto.getUnitPackSize());
        System.out.println("----------------");

        int saldoAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
        int quantidadeReal = quantidade;
        if (Boolean.TRUE.equals(porEmbalagem) && produto.getUnitPackSize() != null) {
            quantidadeReal = (int) (quantidade * produto.getUnitPackSize());
        }

        // 3. Agora usamos 'quantidadeReal' para as contas de stock
        if (tipo == TipoMovimentacao.SAIDA) {
            if (saldoAtual < quantidadeReal) throw new RuntimeException("Stock insuficiente!");
            produto.setQuantidadeEstoque(saldoAtual - quantidadeReal);
        } else {
            produto.setQuantidadeEstoque(saldoAtual + quantidadeReal);
        }
        produtoRepository.save(produto);

        MovimentacaoEstoque novaMovimentacao = new MovimentacaoEstoque(produto, tipo, quantidade, descricao, valorVenda);
        movimentacaoRepository.save(novaMovimentacao);

        return "Estoque atualizado com sucesso!";
    }

    @Transactional
    public void atualizarEstoqueMinimo(Long produtoId, Integer novoMinimo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setEstoqueMinimo(novoMinimo);
        produtoRepository.save(produto);
    }

}