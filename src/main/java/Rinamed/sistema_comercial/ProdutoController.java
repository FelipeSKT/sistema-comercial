package Rinamed.sistema_comercial;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

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
    public void excluirProduto(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
