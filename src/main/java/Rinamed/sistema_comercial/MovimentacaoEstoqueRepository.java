package Rinamed.sistema_comercial;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    // O Spring cria o SQL automaticamente só de ler este nome!
    List<MovimentacaoEstoque> findByProdutoId(Long produtoId);
}