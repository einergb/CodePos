package com.codepos.service;

import com.codepos.dao.VentaDetalleDAO;
import com.codepos.model.VentaDetalle;

import java.math.BigDecimal;
import java.util.List;

/**

 * Contiene la lógica de negocio de los detalles de venta.
 *
 * El Service valida los datos antes de enviarlos al DAO.
 */
public class VentaDetalleService {

    private final VentaDetalleDAO ventaDetalleDAO;

    public VentaDetalleService() {
        this.ventaDetalleDAO =
                new VentaDetalleDAO();
    }

    /**

     * Busca un detalle de venta por su ID.
     *
     * @param detalleId ID del detalle
     * @return detalle encontrado o null
     */
    public VentaDetalle buscarPorId(
            Long detalleId) {

        validarId(
                detalleId,
                "El ID del detalle es obligatorio"
        );

        return ventaDetalleDAO.buscarPorId(
                detalleId
        );
    }

    /**

     * Lista todos los detalles asociados
     * a una venta.
     *
     * @param ventaId ID de la venta
     * @return lista de detalles
     */
    public List<VentaDetalle> listarPorVenta(
            Long ventaId) {

        validarId(
                ventaId,
                "El ID de la venta es obligatorio"
        );

        return ventaDetalleDAO.listarPorVenta(
                ventaId
        );
    }

    /**

     * Crea un nuevo detalle de venta.
     *
     * @param detalle detalle a crear
     * @return ID generado
     */
    public Long crear(
            VentaDetalle detalle) {

        validarDetalle(detalle);

        return ventaDetalleDAO.crear(
                detalle
        );
    }

    /**

     * Valida los datos principales del detalle.
     */
    private void validarDetalle(
            VentaDetalle detalle) {

        if (detalle == null) {


            throw new IllegalArgumentException(
                    "El detalle de venta es obligatorio"
            );


        }

        /*

         * La venta es obligatoria.
         */
        validarId(
                detalle.getVentaId(),
                "La venta es obligatoria"
        );

        /*

         * El producto es obligatorio.
         */
        validarId(
                detalle.getProductoId(),
                "El producto es obligatorio"
        );

        /*

         * La cantidad debe existir.
         */
        if (detalle.getCantidad() == null) {

            throw new IllegalArgumentException(
                    "La cantidad es obligatoria"
            );
        }

        /*

         * La cantidad debe ser mayor que cero.
         */
        if (detalle.getCantidad()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        /*

         * El precio debe existir y no puede ser negativo.
         */
        validarMonto(
                detalle.getPrecioVenta(),
                "El precio de venta es obligatorio",
                "El precio de venta no puede ser negativo"
        );

        /*

         * El descuento no puede ser negativo.
         */
        validarMontoNoNegativo(
                detalle.getDescuento(),
                "El descuento no puede ser negativo"
        );

        /*

         * El impuesto no puede ser negativo.
         */
        validarMontoNoNegativo(
                detalle.getImpuesto(),
                "El impuesto no puede ser negativo"
        );

        /*

         * El subtotal debe existir y no puede ser negativo.
         */
        validarMonto(
                detalle.getSubtotal(),
                "El subtotal es obligatorio",
                "El subtotal no puede ser negativo"
        );
    }

    /**

     * Valida un monto obligatorio.
     */
    private void validarMonto(
            BigDecimal monto,
            String mensajeNulo,
            String mensajeNegativo) {

        if (monto == null) {


            throw new IllegalArgumentException(
                    mensajeNulo
            );


        }

        if (monto.compareTo(BigDecimal.ZERO) < 0) {


            throw new IllegalArgumentException(
                    mensajeNegativo
            );


        }
    }

    /**

     * Valida un monto opcional.
     *
     * Si el valor es null se permite.
     */
    private void validarMontoNoNegativo(
            BigDecimal monto,
            String mensaje) {

        if (monto != null
                && monto.compareTo(BigDecimal.ZERO) < 0) {


            throw new IllegalArgumentException(
                    mensaje
            );


        }
    }

    /**

     * Valida un identificador.
     */
    private void validarId(
            Long id,
            String mensaje) {

        if (id == null || id <= 0) {


            throw new IllegalArgumentException(
                    mensaje
            );


        }
    }
}
