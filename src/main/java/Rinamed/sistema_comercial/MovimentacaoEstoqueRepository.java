package Rinamed.sistema_comercial;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;



public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    // O Spring cria o SQL automaticamente só de ler este nome!
    List<MovimentacaoEstoque> findByProdutoId(Long produtoId);

    @Transactional
        // Necessário para operações de DELETE ou UPDATE customizadas
    void deleteByProdutoId(Long produtoId);

    List<MovimentacaoEstoque> findByDescricaoContainingOrderByDataHoraDesc(String texto);

    List<MovimentacaoEstoque> findByDataHoraBetweenAndDescricaoContainingOrderByDataHoraDesc(
            LocalDateTime inicio,
            LocalDateTime fim,
            String descricao
    );
}