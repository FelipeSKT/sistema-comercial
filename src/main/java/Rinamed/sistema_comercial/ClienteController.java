package Rinamed.sistema_comercial;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClienteController {

    @Autowired
    private ClienteRepository repository;

    // Endpoint para buscar por tipo (Física ou Jurídica)
    @GetMapping("/clientes/tipo/{tipo}")
    public List<Cliente> listarPorTipo(@PathVariable String tipo) {
        return repository.findByTipo(tipo.toUpperCase());
    }

    // Mantemos o salvar genérico, ele aceita o JSON completo
    @PostMapping("/cliente")
    public String cadastrar(@RequestBody Cliente novoCliente) {
        repository.save(novoCliente);
        return "Cliente salvo com sucesso!";
    }

    @DeleteMapping("/cliente/{id}")
    public void excluirCliente(@PathVariable Long id) {
        repository.deleteById(id);
    }

    // Método de busca antigo (opcional manter se quiser buscar todos misturados)
    @GetMapping("/clientes")
    public List<Cliente> listarTodos() {
        return repository.findAll();
    }
}
