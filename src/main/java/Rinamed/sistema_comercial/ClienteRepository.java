package Rinamed.sistema_comercial;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNameContainingIgnoreCaseOrCpfContaining(String name, String cpf);
}
