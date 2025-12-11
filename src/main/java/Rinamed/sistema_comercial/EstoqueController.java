package Rinamed.sistema_comercial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private MovimentacaoEstoqueRepository repository;

    @PostMapping("/movimentacao")
    public String registrar(@RequestBody DadosMovimentacao dados) {
        // Passamos 'null' no final pois ajuste manual de estoque não tem "preço de venda" obrigatório
        return estoqueService.registrarMovimentacao(
                dados.produtoId(),
                dados.tipo(),
                dados.quantidade(),
                dados.descricao(),
                dados.porEmbalagem(),
                null
        );
    }

    @GetMapping("/movimentacoes/{idProduto}")
    public List<MovimentacaoEstoque> listarHistorico(@PathVariable Long idProduto) {
        return repository.findByProdutoId(idProduto);
    }

    @PostMapping("/produto/minimo/{id}")
    public void atualizarMinimo(@PathVariable Long id, @RequestBody Integer novoMinimo) {
        estoqueService.atualizarEstoqueMinimo(id, novoMinimo);
    }
}
