
package com.codepos.service;

import com.codepos.dao.CompraDAO;
import com.codepos.dao.ProveedorDAO;
import com.codepos.model.Compra;
import com.codepos.model.Proveedor;

import java.math.BigDecimal;
import java.util.List;

public class CompraService {

    private final CompraDAO compraDAO;
    private final ProveedorDAO proveedorDAO;

    public CompraService() {
        this.compraDAO = new CompraDAO();
        this.proveedorDAO = new ProveedorDAO();
    }

    /**
     * Busca una compra perteneciente a una empresa.
     */
    public Compra consultar(
            Long empresaId,
            Long compraId) {

        validarEmpresa(empresaId);
        validarId(compraId, "Compra");

        Compra compra =
                compraDAO.buscarPorId(
                        empresaId,
                        compraId
                );

        if (compra == null) {
            throw new IllegalArgumentException(
                    "No existe la compra indicada"
            );
        }

        return compra;
    }

    /**
     * Lista todas las compras de una empresa.
     */
    public List<Compra> listarPorEmpresa(
            Long empresaId) {

        validarEmpresa(empresaId);

        return compraDAO.listarPorEmpresa(
                empresaId
        );
    }

    /**
     * Crea una nueva compra.
     */
    public Long crear(Compra compra) {

        if (compra == null) {
            throw new IllegalArgumentException(
                    "La compra es obligatoria"
            );
        }

        validarEmpresa(compra.getEmpresaId());

        validarId(
                compra.getSucursalId(),
                "Sucursal"
        );

        validarId(
                compra.getProveedorId(),
                "Proveedor"
        );

        validarTexto(
                compra.getNumero(),
                "El número de compra es obligatorio"
        );

        validarEstado(
                compra.getEstado()
        );

        validarMonto(
                compra.getSubtotal(),
                "Subtotal"
        );

        validarMonto(
                compra.getDescuento(),
                "Descuento"
        );

        validarMonto(
                compra.getImpuesto(),
                "Impuesto"
        );

        validarMonto(
                compra.getTotal(),
                "Total"
        );

        validarProveedor(
                compra.getEmpresaId(),
                compra.getProveedorId()
        );

        return compraDAO.crear(compra);
    }

    /**
     * Valida que el proveedor exista y
     * pertenezca a la empresa.
     */
    private void validarProveedor(
            Long empresaId,
            Long proveedorId) {

        Proveedor proveedor =
                proveedorDAO.buscarPorId(
                        empresaId,
                        proveedorId
                );

        if (proveedor == null) {
            throw new IllegalArgumentException(
                    "El proveedor no existe para la empresa indicada"
            );
        }

        if (!Boolean.TRUE.equals(
                proveedor.getActivo())) {

            throw new IllegalStateException(
                    "El proveedor está inactivo"
            );
        }
    }

    /**
     * Valida el estado permitido por la BD.
     */
    private void validarEstado(String estado) {

        validarTexto(
                estado,
                "El estado de la compra es obligatorio"
        );

        if (!estado.equals("REGISTRADA")
                && !estado.equals("APLICADA")
                && !estado.equals("ANULADA")) {

            throw new IllegalArgumentException(
                    "Estado de compra inválido: "
                            + estado
            );
        }
    }

    /**
     * Valida montos monetarios.
     */
    private void validarMonto(
            BigDecimal monto,
            String campo) {

        if (monto == null) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }

        if (monto.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    campo + " no puede ser negativo"
            );
        }
    }

    /**
     * Valida un identificador.
     */
    private void validarId(
            Long id,
            String entidad) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    entidad + " inválido"
            );
        }
    }

    /**
     * Valida la empresa.
     */
    private void validarEmpresa(
            Long empresaId) {

        if (empresaId == null || empresaId <= 0) {
            throw new IllegalArgumentException(
                    "Empresa inválida"
            );
        }
    }

    /**
     * Valida textos obligatorios.
     */
    private void validarTexto(
            String texto,
            String mensaje) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}
