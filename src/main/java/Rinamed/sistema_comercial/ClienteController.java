package Rinamed.sistema_comercial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ClienteController {

    @Autowired
    private ClienteRepository repository;

    @GetMapping("/clientes")
    // MODIFICADO: Aceita um parâmetro de pesquisa 'q' (opcional)
    public List<Cliente> listar(@RequestParam(required = false) String q) {
        if (q != null && !q.isEmpty()) {
            // Se houver termo, busca por nome OU CPF
            return repository.findByNameContainingIgnoreCaseOrCpfContaining(q, q);
        }
        return repository.findAll(); // Senão, devolve tudo
    }

    @GetMapping("/clienteserver")
    public String dizerOla() {
        return "Olá! O sistema está online.";
    }

    @PostMapping("/cliente")
    public String cadastrar(@Valid @RequestBody Cliente novoCliente) {
        repository.save(novoCliente);
        System.out.println("Chegou um cliente: " + novoCliente.getName());
        return "Cliente " + novoCliente.getName() + " recebido com sucesso!";
    }

    @DeleteMapping("/cliente/{id}")
    public void excluirCliente(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
