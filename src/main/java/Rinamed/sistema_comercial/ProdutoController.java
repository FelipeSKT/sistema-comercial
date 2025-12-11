package Rinamed.sistema_comercial;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @GetMapping("/produtos")
    // MODIFICADO: Aceita um parâmetro de pesquisa 'q' (opcional)
    public List<Produto> listar(@RequestParam(required = false) String q) {
        if (q != null && !q.isEmpty()) {
            // Se houver termo, busca pelo nome OU fabricante
            return repository.findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(q, q);
        }
        return repository.findAll(); // Senão, devolve tudo (comportamento original)
    }

    @GetMapping("/produtoserver")
    public String dizerOla() {
        return "Olá! O sistema está online.";
    }

    @PostMapping("/produto")
    public String cadastrar(@Valid @RequestBody Produto novoProduto) {
        repository.save(novoProduto);
        System.out.println("Chegou um Produto: " + novoProduto.getName());
        return "Produto " + novoProduto.getName() + " recebido com sucesso!";
    }

    @DeleteMapping("/produto/{id}")
    @Transactional // Garante que as duas exclusões ocorram juntas (ou nenhuma)
    public void excluirProduto(@PathVariable Long id) {
        // 1. Primeiro, limpamos o histórico deste produto (remove as amarras)
        movimentacaoRepository.deleteByProdutoId(id);

        // 2. Agora o produto está livre para ser excluído
        repository.deleteById(id);
    }
}
