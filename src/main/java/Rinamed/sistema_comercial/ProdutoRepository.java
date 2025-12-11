package Rinamed.sistema_comercial;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(String name, String manufacturer);
}