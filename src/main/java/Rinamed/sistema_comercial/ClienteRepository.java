package Rinamed.sistema_comercial;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca geral (mantida)
    List<Cliente> findByNomeContainingIgnoreCaseOrCpfContaining(String nome, String cpf);

    // Busca específica por tipo (para preencher as tabelas separadas)
    List<Cliente> findByTipo(String tipo);
}