package Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Genera un informe HTML completo de una casa.
 * El HTML resultante está diseñado para imprimirse desde el navegador
 * como PDF (Ctrl+P → "Guardar como PDF").
 */
public class GeneradorReporte {

    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String generarHtml(
            String numeroCasa,
            Residentes residente,
            ArrayList<Alicuota>     alicuotas,
            ArrayList<Multa>        multas,
            ArrayList<Arriendo>     arriendos,
            ArrayList<ArriendoSede> reservasSede) {

        StringBuilder sb = new StringBuilder();
        String ahora = LocalDateTime.now().format(FMT_DT);

        // ── Resumen de pendientes ──────────────────────────────────
        double alicPendiente  = totalEstado(alicuotas,  "Pendiente",  1);
        double alicAtrasado   = totalEstado(alicuotas,  "Atrasado",   1);
        double multaPendiente = totalEstado(multas,      "Pendiente",  2);
        double arriPendiente  = totalEstado(arriendos,   "Pendiente",  3);
        double totalDeuda     = alicPendiente + alicAtrasado + multaPendiente + arriPendiente;

        sb.append("<!DOCTYPE html>\n<html lang='es'>\n<head>\n")
          .append("<meta charset='UTF-8'>\n")
          .append("<title>Consulta Casa ").append(numeroCasa).append("</title>\n")
          .append("<style>\n")
          .append("  * { box-sizing: border-box; margin: 0; padding: 0; }\n")
          .append("  body { font-family: 'Segoe UI', Arial, sans-serif; font-size: 12px; color: #222; padding: 20px; }\n")
          .append("  h1 { font-size: 20px; color: #111; margin-bottom: 4px; }\n")
          .append("  h2 { font-size: 14px; color: #333; background: #f0f0f0; padding: 6px 10px; margin: 14px 0 6px; border-left: 4px solid #333; }\n")
          .append("  .meta { color: #666; font-size: 11px; margin-bottom: 14px; }\n")
          .append("  .resumen-box { display: flex; gap: 12px; margin-bottom: 14px; flex-wrap: wrap; }\n")
          .append("  .card { border-radius: 6px; padding: 10px 16px; min-width: 150px; }\n")
          .append("  .card-red   { background: #ffe5e5; border: 1px solid #f99; }\n")
          .append("  .card-orange{ background: #fff3e0; border: 1px solid #ffcc80; }\n")
          .append("  .card-green { background: #e8f5e9; border: 1px solid #a5d6a7; }\n")
          .append("  .card-blue  { background: #e3f2fd; border: 1px solid #90caf9; }\n")
          .append("  .card-title { font-size: 11px; color: #555; }\n")
          .append("  .card-value { font-size: 16px; font-weight: bold; margin-top: 2px; }\n")
          .append("  .residente-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 4px 20px; }\n")
          .append("  .field { display: flex; gap: 6px; }\n")
          .append("  .field-label { font-weight: bold; min-width: 130px; }\n")
          .append("  table { width: 100%; border-collapse: collapse; margin-bottom: 4px; font-size: 11px; }\n")
          .append("  th { background: #333; color: #fff; padding: 5px 7px; text-align: left; font-size: 11px; }\n")
          .append("  td { padding: 4px 7px; border-bottom: 1px solid #e0e0e0; }\n")
          .append("  tr:nth-child(even) td { background: #fafafa; }\n")
          .append("  .badge { display: inline-block; padding: 1px 7px; border-radius: 10px; font-size: 10px; font-weight: bold; }\n")
          .append("  .pend  { background: #fff3e0; color: #e65100; }\n")
          .append("  .atra  { background: #fce4ec; color: #c62828; }\n")
          .append("  .pago  { background: #e8f5e9; color: #2e7d32; }\n")
          .append("  .canc  { background: #f3e5f5; color: #6a1b9a; }\n")
          .append("  .conf  { background: #e3f2fd; color: #1565c0; }\n")
          .append("  .anul  { background: #eeeeee; color: #555; }\n")
          .append("  .sin-datos { color: #888; font-style: italic; padding: 8px 0; }\n")
          .append("  .footer { margin-top: 20px; border-top: 1px solid #ccc; padding-top: 8px; color: #888; font-size: 10px; }\n")
          .append("  .deuda-total { font-size: 15px; font-weight: bold; color: #c62828; margin-top: 4px; }\n")
          .append("  @media print { body { padding: 10px; } }\n")
          .append("</style>\n</head>\n<body>\n");

        // ── Encabezado ────────────────────────────────────────────
        sb.append("<h1>📋 Consulta de Casa N° ").append(numeroCasa).append("</h1>\n");
        sb.append("<p class='meta'>Generado el: ").append(ahora).append("</p>\n");

        // ── Tarjetas de resumen ────────────────────────────────────
        sb.append("<div class='resumen-box'>\n");
        card(sb, "Alícuotas Pendientes",  alicPendiente,  "card-orange");
        card(sb, "Alícuotas Atrasadas",   alicAtrasado,   "card-red");
        card(sb, "Multas Pendientes",     multaPendiente, "card-red");
        card(sb, "Arriendos Pendientes",  arriPendiente,  "card-orange");
        sb.append("  <div class='card card-red'><div class='card-title'>⚠ TOTAL DEUDA</div>")
          .append("<div class='deuda-total'>$ ").append(String.format("%.2f", totalDeuda)).append("</div></div>\n");
        sb.append("</div>\n");

        // ══════════════════════════════════════════════════════════
        // DATOS DEL RESIDENTE
        // ══════════════════════════════════════════════════════════
        sb.append("<h2>👤 Datos del Residente</h2>\n");
        if (residente == null) {
            sb.append("<p class='sin-datos'>No hay residente registrado para esta casa.</p>\n");
        } else {
            sb.append("<div class='residente-grid'>\n");
            field(sb, "Nombres:",            residente.getNombres() + " " + residente.getApellidos());
            field(sb, "Cédula:",             residente.getCedula());
            field(sb, "Teléfono Móvil:",     ns(residente.getTelefonoMovil()));
            field(sb, "Tel. Convencional:",  ns(residente.getTelefonoConvencional()));
            field(sb, "Tipo de residente:",  ns(residente.getTipoResidente()));
            field(sb, "Estado:",             residente.getEstadoResidente());
            field(sb, "Mascotas:",           residente.isTieneMascotas() ? "Sí" : "No");
            field(sb, "Vehículos:",          residente.getVehiculosResumen());
            if (residente.getFechaRegistro() != null)
                field(sb, "Fecha de registro:", residente.getFechaRegistro().format(FMT_DT));
            sb.append("</div>\n");
        }

        // ══════════════════════════════════════════════════════════
        // ALÍCUOTAS
        // ══════════════════════════════════════════════════════════
        sb.append("<h2>💵 Alícuotas</h2>\n");
        if (alicuotas.isEmpty()) {
            sb.append("<p class='sin-datos'>Sin alícuotas registradas.</p>\n");
        } else {
            sb.append("<table>\n<tr>")
              .append(th("Período")).append(th("Monto ($)")).append(th("Estado"))
              .append(th("Forma Pago")).append(th("N° Tx")).append(th("Fecha Pago"))
              .append(th("Registrado"))
              .append("</tr>\n");
            for (Alicuota a : alicuotas) {
                sb.append("<tr>")
                  .append(td(ns(a.getPeriodo())))
                  .append(td(String.format("%.2f", a.getMonto())))
                  .append("<td>").append(badge(a.getEstado())).append("</td>")
                  .append(td(ns(a.getFormaPago())))
                  .append(td(ns(a.getNumeroTransaccion()).isEmpty() ? "—" : a.getNumeroTransaccion()))
                  .append(td(a.getFechaPago() != null ? a.getFechaPago().format(FMT_D) : "—"))
                  .append(td(a.getFechaRegistro() != null ? a.getFechaRegistro().format(FMT_DT) : "—"))
                  .append("</tr>\n");
            }
            sb.append("</table>\n");
            sb.append("<p style='text-align:right; font-size:11px; color:#555;'>")
              .append("Pagado: <b>$").append(String.format("%.2f", totalEstado(alicuotas,"Pagado",1))).append("</b>")
              .append(" &nbsp;|&nbsp; Pendiente: <b>$").append(String.format("%.2f", alicPendiente)).append("</b>")
              .append(" &nbsp;|&nbsp; Atrasado: <b>$").append(String.format("%.2f", alicAtrasado)).append("</b>")
              .append("</p>\n");
        }

        // ══════════════════════════════════════════════════════════
        // MULTAS
        // ══════════════════════════════════════════════════════════
        sb.append("<h2>🚫 Multas</h2>\n");
        if (multas.isEmpty()) {
            sb.append("<p class='sin-datos'>Sin multas registradas.</p>\n");
        } else {
            sb.append("<table>\n<tr>")
              .append(th("Categoría")).append(th("Motivo")).append(th("Fecha Infracción"))
              .append(th("Monto ($)")).append(th("Estado")).append(th("Observaciones"))
              .append("</tr>\n");
            for (Multa m : multas) {
                sb.append("<tr>")
                  .append(td(ns(m.getCategoria())))
                  .append(td(ns(m.getMotivo())))
                  .append(td(m.getFechaInfraccion() != null ? m.getFechaInfraccion().format(FMT_D) : "—"))
                  .append(td(String.format("%.2f", m.getMonto())))
                  .append("<td>").append(badge(m.getEstado())).append("</td>")
                  .append(td(ns(m.getObservaciones())))
                  .append("</tr>\n");
            }
            sb.append("</table>\n");
            sb.append("<p style='text-align:right; font-size:11px; color:#555;'>")
              .append("Pendiente: <b>$").append(String.format("%.2f", multaPendiente)).append("</b>")
              .append(" &nbsp;|&nbsp; Pagada: <b>$").append(String.format("%.2f", totalEstado(multas,"Pagada",2))).append("</b>")
              .append("</p>\n");
        }

        // ══════════════════════════════════════════════════════════
        // ARRIENDOS (locales / parqueaderos)
        // ══════════════════════════════════════════════════════════
        sb.append("<h2>🏪 Arriendos (Locales / Parqueaderos)</h2>\n");
        if (arriendos.isEmpty()) {
            sb.append("<p class='sin-datos'>Sin arriendos registrados para esta casa.</p>\n");
        } else {
            sb.append("<table>\n<tr>")
              .append(th("Tipo")).append(th("Espacio")).append(th("Período"))
              .append(th("Monto ($)")).append(th("Estado")).append(th("Forma Pago"))
              .append(th("N° Tx")).append(th("Fecha Pago"))
              .append("</tr>\n");
            for (Arriendo a : arriendos) {
                sb.append("<tr>")
                  .append(td(ns(a.getTipoEspacio())))
                  .append(td(ns(a.getNombreEspacio())))
                  .append(td(ns(a.getMesPeriodo())))
                  .append(td(String.format("%.2f", a.getMontoMensual())))
                  .append("<td>").append(badge(a.getEstado())).append("</td>")
                  .append(td(ns(a.getFormaPago())))
                  .append(td(ns(a.getNumeroTransaccion()).isEmpty() ? "—" : a.getNumeroTransaccion()))
                  .append(td(a.getFechaPago() != null ? a.getFechaPago().format(FMT_D) : "—"))
                  .append("</tr>\n");
            }
            sb.append("</table>\n");
            sb.append("<p style='text-align:right; font-size:11px; color:#555;'>")
              .append("Pagado: <b>$").append(String.format("%.2f", totalEstado(arriendos,"Pagado",3))).append("</b>")
              .append(" &nbsp;|&nbsp; Pendiente: <b>$").append(String.format("%.2f", arriPendiente)).append("</b>")
              .append("</p>\n");
        }

        // ══════════════════════════════════════════════════════════
        // RESERVAS SEDE SOCIAL
        // ══════════════════════════════════════════════════════════
        sb.append("<h2>🏛 Reservas de Sede Social</h2>\n");
        if (reservasSede.isEmpty()) {
            sb.append("<p class='sin-datos'>Sin reservas de sede registradas.</p>\n");
        } else {
            sb.append("<table>\n<tr>")
              .append(th("Fecha Reserva")).append(th("Modalidad")).append(th("Motivo"))
              .append(th("Monto ($)")).append(th("Estado")).append(th("Forma Pago"))
              .append("</tr>\n");
            for (ArriendoSede s : reservasSede) {
                sb.append("<tr>")
                  .append(td(s.getFechaReserva() != null ? s.getFechaReserva().format(FMT_D) : "—"))
                  .append(td(ns(s.getModalidad())))
                  .append(td(ns(s.getMotivo())))
                  .append(td(String.format("%.2f", s.getMonto())))
                  .append("<td>").append(badge(s.getEstado())).append("</td>")
                  .append(td(ns(s.getFormaPago())))
                  .append("</tr>\n");
            }
            sb.append("</table>\n");
        }

        // ── Pie de página ─────────────────────────────────────────
        sb.append("<div class='footer'>")
          .append("Sistema de Administración Residencial — Reporte generado automáticamente el ")
          .append(ahora)
          .append("</div>\n");
        sb.append("</body>\n</html>");

        return sb.toString();
    }

    // ── Helpers de construcción ───────────────────────────────────
    private static void card(StringBuilder sb, String titulo, double monto, String cls) {
        sb.append("  <div class='card ").append(cls).append("'>")
          .append("<div class='card-title'>").append(titulo).append("</div>")
          .append("<div class='card-value'>$ ").append(String.format("%.2f", monto)).append("</div>")
          .append("</div>\n");
    }
    private static void field(StringBuilder sb, String label, String value) {
        sb.append("<div class='field'><span class='field-label'>").append(label).append("</span>")
          .append("<span>").append(value != null ? esc(value) : "—").append("</span></div>\n");
    }
    private static String th(String t) { return "<th>" + esc(t) + "</th>"; }
    private static String td(String v) { return "<td>" + esc(v != null ? v : "—") + "</td>"; }
    private static String badge(String estado) {
        if (estado == null) return "";
        String cls;
        switch (estado) {
            case "Pendiente": cls = "pend"; break;
            case "Atrasado":  cls = "atra"; break;
            case "Pagado":    cls = "pago"; break;
            case "Pagada":    cls = "pago"; break;
            case "Cancelado": cls = "canc"; break;
            case "Cancelada": cls = "canc"; break;
            case "Confirmada":cls = "conf"; break;
            case "Anulada":   cls = "anul"; break;
            default:          cls = "anul"; break;
        }
        return "<span class='badge " + cls + "'>" + esc(estado) + "</span>";
    }
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
    private static String ns(String s) { return s != null ? s : ""; }

    /** tipo 1=Alicuota, 2=Multa, 3=Arriendo */
    @SuppressWarnings("unchecked")
    private static double totalEstado(ArrayList<?> lista, String estado, int tipo) {
        double total = 0;
        for (Object o : lista) {
            if (tipo == 1) {
                Alicuota a = (Alicuota) o;
                if (estado.equals(a.getEstado())) total += a.getMonto();
            } else if (tipo == 2) {
                Multa m = (Multa) o;
                if (estado.equals(m.getEstado())) total += m.getMonto();
            } else {
                Arriendo a = (Arriendo) o;
                if (estado.equals(a.getEstado())) total += a.getMontoMensual();
            }
        }
        return total;
    }
}
