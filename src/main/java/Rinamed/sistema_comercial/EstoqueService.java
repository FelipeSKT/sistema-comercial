package Rinamed.sistema_comercial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional
    public String registrarMovimentacao(Long produtoId, TipoMovimentacao tipo, Integer quantidade, String descricao, Boolean porEmbalagem) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        System.out.println("--- DEBUG ---");
        System.out.println("Produto: " + produto.getName());
        System.out.println("Checkbox marcado? " + porEmbalagem);
        System.out.println("Tamanho da Embalagem no Banco: " + produto.getUnitPackSize());
        System.out.println("----------------");

        // CORREÇÃO: Proteção contra valores nulos
        // Se o stock for nulo (produto antigo), assumimos que é 0
        int saldoAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;

        // 2. LÓGICA NOVA: Calcular a quantidade real
        int quantidadeReal = quantidade;

        // Verificamos se "porEmbalagem" é verdadeiro (Boolean.TRUE garante que não dá erro se vier nulo)
        if (Boolean.TRUE.equals(porEmbalagem)) {
            // Se for por embalagem, multiplicamos!
            // Ex: 5 caixas * 12 unidades = 60 unidades reais
            if (produto.getUnitPackSize() != null) {
                quantidadeReal = (int) (quantidade * produto.getUnitPackSize());
            }
        }

        // 3. Agora usamos 'quantidadeReal' para as contas de stock
        if (tipo == TipoMovimentacao.SAIDA) {
            if (saldoAtual < quantidadeReal) {
                throw new RuntimeException("Stock insuficiente!");
            }
            produto.setQuantidadeEstoque(saldoAtual - quantidadeReal);
        } else {
            produto.setQuantidadeEstoque(saldoAtual + quantidadeReal);
        }

        produtoRepository.save(produto);

        MovimentacaoEstoque novaMovimentacao = new MovimentacaoEstoque(produto, tipo, quantidade, descricao);
        movimentacaoRepository.save(novaMovimentacao);

        return "Stock atualizado com sucesso!";
    }

    // ... dentro do EstoqueService

    @Transactional
    public void atualizarEstoqueMinimo(Long produtoId, Integer novoMinimo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setEstoqueMinimo(novoMinimo);
        produtoRepository.save(produto);
    }

}