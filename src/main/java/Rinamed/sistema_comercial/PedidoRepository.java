package Rinamed.sistema_comercial;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Buscar pedidos pendentes para listar no painel de conferência
    List<Pedido> findByStatus(String status);

    // Buscar pedidos de um cliente específico
    List<Pedido> findByClienteId(Long clienteId);
}