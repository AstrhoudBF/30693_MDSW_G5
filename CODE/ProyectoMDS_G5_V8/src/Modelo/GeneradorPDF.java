package Modelo;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.print.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Genera un PDF real usando Java2D + javax.print.
 * No requiere ninguna librería externa.
 * Dibuja el reporte de casa página a página con Graphics2D.
 */
public class GeneradorPDF implements Printable {

    // ── Formato de fechas ─────────────────────────────────────────
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Datos del reporte ─────────────────────────────────────────
    private final String                    numeroCasa;
    private final Residentes                residente;
    private final ArrayList<Alicuota>       alicuotas;
    private final ArrayList<Multa>          multas;
    private final ArrayList<Arriendo>       arriendos;
    private final ArrayList<ArriendoSede>   sede;

    // ── Líneas de contenido (se construyen antes de imprimir) ─────
    private final List<Object[]> lineas = new ArrayList<>(); // {tipo, texto, color, bold}
    private int totalLineas = 0;

    // ── Constantes de layout ──────────────────────────────────────
    private static final int MARGIN_X    = 55;
    private static final int MARGIN_TOP  = 60;
    private static final int MARGIN_BOT  = 55;
    private static final int LINE_H      = 14;
    private static final int PAGE_W      = 595; // A4 puntos
    private static final int PAGE_H      = 842;
    private static final int CONTENT_W   = PAGE_W - MARGIN_X * 2;

    public GeneradorPDF(String numeroCasa, Residentes residente,
                        ArrayList<Alicuota> alicuotas, ArrayList<Multa> multas,
                        ArrayList<Arriendo> arriendos, ArrayList<ArriendoSede> sede) {
        this.numeroCasa = numeroCasa;
        this.residente  = residente;
        this.alicuotas  = alicuotas;
        this.multas     = multas;
        this.arriendos  = arriendos;
        this.sede       = sede;
        construirLineas();
    }

    // ─────────────────────────────────────────────────────────────
    // Construir el contenido como lista de líneas
    // ─────────────────────────────────────────────────────────────
    private void construirLineas() {
        lineas.clear();
        String ahora = LocalDateTime.now().format(FMT_DT);

        // Resumen de deuda
        double alicPend  = sumaEstado(alicuotas, "Pendiente", 1);
        double alicAtras = sumaEstado(alicuotas, "Atrasado",  1);
        double multPend  = sumaEstado(multas,    "Pendiente", 2);
        double arrPend   = sumaEstado(arriendos, "Pendiente", 3);
        double totalDeuda = alicPend + alicAtras + multPend + arrPend;

        // ENCABEZADO
        add("TITULO",  "CONSULTA DE CASA N° " + numeroCasa, null, true);
        add("SUBTIT",  "Generado el: " + ahora, Color.GRAY, false);
        add("SEP", null, null, false);

        // Resumen de deuda
        add("SEC",   "RESUMEN DE DEUDA", new Color(60,60,60), true);
        add("KV",    "Alícuotas Pendientes:    $ " + fmt(alicPend), null, false);
        add("KV",    "Alícuotas Atrasadas:     $ " + fmt(alicAtras), null, false);
        add("KV",    "Multas Pendientes:        $ " + fmt(multPend), null, false);
        add("KV",    "Arriendos Pendientes:     $ " + fmt(arrPend), null, false);
        add("DEUDA", "TOTAL DEUDA: $ " + fmt(totalDeuda), new Color(180,0,0), true);
        add("SEP", null, null, false);

        // RESIDENTE
        add("SEC", "DATOS DEL RESIDENTE", new Color(30,80,160), true);
        if (residente == null) {
            add("KV", "Sin residente registrado para esta casa.", Color.GRAY, false);
        } else {
            add("KV", "Nombre:              " + residente.getNombres() + " " + residente.getApellidos(), null, false);
            add("KV", "Cédula:              " + ns(residente.getCedula()), null, false);
            add("KV", "Tel. Móvil:          " + ns(residente.getTelefonoMovil()), null, false);
            add("KV", "Tel. Convencional:   " + ns(residente.getTelefonoConvencional()), null, false);
            add("KV", "Tipo de residente:   " + ns(residente.getTipoResidente()), null, false);
            add("KV", "Estado:              " + residente.getEstadoResidente(), null, false);
            add("KV", "Mascotas:            " + (residente.isTieneMascotas() ? "Sí" : "No"), null, false);
            add("KV", "Vehículos:           " + residente.getVehiculosResumen(), null, false);
            if (residente.getFechaRegistro() != null)
                add("KV", "Fecha registro:      " + residente.getFechaRegistro().format(FMT_DT), null, false);
        }
        add("SEP", null, null, false);

        // ALÍCUOTAS
        add("SEC", "ALÍCUOTAS (" + alicuotas.size() + ")", new Color(30,80,160), true);
        if (alicuotas.isEmpty()) {
            add("KV", "Sin alícuotas registradas.", Color.GRAY, false);
        } else {
            // Cabecera tabla
            add("TH", padCol("Período",12) + padCol("Monto",9) + padCol("Estado",11) +
                       padCol("Forma Pago",14) + padCol("N° Tx",12) + "Fecha Pago", null, true);
            add("THLINE", null, null, false);
            for (Alicuota a : alicuotas) {
                add("TR", padCol(ns(a.getPeriodo()),12) +
                           padCol("$"+fmt(a.getMonto()),9) +
                           padCol(ns(a.getEstado()),11) +
                           padCol(ns(a.getFormaPago()),14) +
                           padCol(ntx(a.getNumeroTransaccion()),12) +
                           (a.getFechaPago()!=null ? a.getFechaPago().format(FMT_D) : "—"),
                    estadoColor(a.getEstado()), false);
            }
            add("KV", "  Pagado: $" + fmt(sumaEstado(alicuotas,"Pagado",1)) +
                       "   Pendiente: $" + fmt(alicPend) +
                       "   Atrasado: $" + fmt(alicAtras),
                Color.DARK_GRAY, false);
        }
        add("SEP", null, null, false);

        // MULTAS
        add("SEC", "MULTAS (" + multas.size() + ")", new Color(30,80,160), true);
        if (multas.isEmpty()) {
            add("KV", "Sin multas registradas.", Color.GRAY, false);
        } else {
            add("TH", padCol("Categoría",12) + padCol("Motivo",24) +
                       padCol("Fecha Infrac.",13) + padCol("Monto",9) + padCol("Estado",11) + "Forma Pago",
                null, true);
            add("THLINE", null, null, false);
            for (Multa m : multas) {
                String motivo = truncar(ns(m.getMotivo()), 22);
                add("TR", padCol(ns(m.getCategoria()),12) +
                           padCol(motivo,24) +
                           padCol(m.getFechaInfraccion()!=null ? m.getFechaInfraccion().format(FMT_D) : "—", 13) +
                           padCol("$"+fmt(m.getMonto()),9) +
                           padCol(ns(m.getEstado()),11) +
                           ns(m.getFormaPago()),
                    estadoColor(m.getEstado()), false);
            }
            add("KV", "  Pendiente: $" + fmt(multPend) +
                       "   Pagada: $" + fmt(sumaEstado(multas,"Pagada",2)),
                Color.DARK_GRAY, false);
        }
        add("SEP", null, null, false);

        // ARRIENDOS
        add("SEC", "ARRIENDOS — LOCALES / PARQUEADEROS (" + arriendos.size() + ")", new Color(30,80,160), true);
        if (arriendos.isEmpty()) {
            add("KV", "Sin arriendos registrados.", Color.GRAY, false);
        } else {
            add("TH", padCol("Tipo",12) + padCol("Espacio",16) + padCol("Período",12) +
                       padCol("Monto",9) + padCol("Estado",11) + "Forma Pago",
                null, true);
            add("THLINE", null, null, false);
            for (Arriendo a : arriendos) {
                add("TR", padCol(ns(a.getTipoEspacio()),12) +
                           padCol(truncar(ns(a.getNombreEspacio()),14),16) +
                           padCol(ns(a.getMesPeriodo()),12) +
                           padCol("$"+fmt(a.getMontoMensual()),9) +
                           padCol(ns(a.getEstado()),11) +
                           ns(a.getFormaPago()),
                    estadoColor(a.getEstado()), false);
            }
            add("KV", "  Pagado: $" + fmt(sumaEstado(arriendos,"Pagado",3)) +
                       "   Pendiente: $" + fmt(arrPend),
                Color.DARK_GRAY, false);
        }
        add("SEP", null, null, false);

        // SEDE SOCIAL
        add("SEC", "RESERVAS DE SEDE SOCIAL (" + sede.size() + ")", new Color(30,80,160), true);
        if (sede.isEmpty()) {
            add("KV", "Sin reservas de sede registradas.", Color.GRAY, false);
        } else {
            add("TH", padCol("Fecha",11) + padCol("Modalidad",12) +
                       padCol("Monto",9) + padCol("Estado",11) + "Motivo",
                null, true);
            add("THLINE", null, null, false);
            for (ArriendoSede s : sede) {
                add("TR", padCol(s.getFechaReserva()!=null ? s.getFechaReserva().format(FMT_D) : "—", 11) +
                           padCol(ns(s.getModalidad()),12) +
                           padCol("$"+fmt(s.getMonto()),9) +
                           padCol(ns(s.getEstado()),11) +
                           truncar(ns(s.getMotivo()),30),
                    estadoColor(s.getEstado()), false);
            }
        }
        add("SEP", null, null, false);
        add("FOOTER", "Sistema de Administración Residencial — " + ahora, Color.GRAY, false);

        totalLineas = lineas.size();
    }

    private void add(String tipo, String texto, Color color, boolean bold) {
        lineas.add(new Object[]{tipo, texto, color, bold});
    }

    // ─────────────────────────────────────────────────────────────
    // Printable.print() — se llama una vez por página
    // ─────────────────────────────────────────────────────────────
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int usableH = (int)(pf.getImageableHeight()) - MARGIN_TOP - MARGIN_BOT;
        int linesPerPage = usableH / LINE_H;

        int startLine = pageIndex * linesPerPage;
        if (startLine >= totalLineas) return NO_SUCH_PAGE;

        // Translate to imageable area
        g2.translate(pf.getImageableX(), pf.getImageableY());

        // Encabezado de página
        drawPageHeader(g2, pf, pageIndex);

        int y = MARGIN_TOP;
        int x = MARGIN_X;

        for (int i = startLine; i < totalLineas && i < startLine + linesPerPage; i++) {
            Object[] l = lineas.get(i);
            String tipo  = (String)  l[0];
            String texto = (String)  l[1];
            Color  color = (Color)   l[2];
            boolean bold = (Boolean) l[3];

            switch (tipo) {
                case "TITULO":
                    g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g2.setColor(new Color(20, 20, 80));
                    g2.drawString(texto, x, y + 12);
                    y += 20;
                    // Línea bajo el título
                    g2.setColor(new Color(20,20,80));
                    g2.fillRect(x, y, CONTENT_W, 2);
                    y += 6;
                    break;

                case "SUBTIT":
                    g2.setFont(new Font("SansSerif", Font.ITALIC, 9));
                    g2.setColor(Color.GRAY);
                    g2.drawString(texto, x, y + 9);
                    y += LINE_H;
                    break;

                case "SEC":
                    y += 4;
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.setColor(color != null ? color : Color.BLACK);
                    // Fondo del título de sección
                    g2.setColor(new Color(230, 235, 255));
                    g2.fillRect(x - 4, y - 1, CONTENT_W + 8, LINE_H + 2);
                    g2.setColor(new Color(30, 80, 160));
                    g2.fillRect(x - 4, y - 1, 4, LINE_H + 2);
                    g2.setColor(new Color(20, 50, 130));
                    g2.drawString(texto, x + 4, y + 10);
                    y += LINE_H + 4;
                    break;

                case "DEUDA":
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.setColor(new Color(180, 0, 0));
                    g2.fillRect(x - 4, y - 1, CONTENT_W + 8, LINE_H + 2);
                    g2.setColor(Color.WHITE);
                    g2.drawString(texto, x + 4, y + 10);
                    y += LINE_H + 2;
                    break;

                case "KV":
                    g2.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, 9));
                    g2.setColor(color != null ? color : Color.BLACK);
                    g2.drawString(texto, x, y + 9);
                    y += LINE_H;
                    break;

                case "TH":
                    g2.setFont(new Font("Monospaced", Font.BOLD, 8));
                    g2.setColor(new Color(50, 50, 50));
                    g2.setColor(new Color(220, 220, 220));
                    g2.fillRect(x - 2, y - 1, CONTENT_W + 4, LINE_H);
                    g2.setColor(new Color(30, 30, 30));
                    g2.drawString(texto, x, y + 9);
                    y += LINE_H;
                    break;

                case "THLINE":
                    g2.setColor(new Color(150,150,150));
                    g2.drawLine(x, y, x + CONTENT_W, y);
                    y += 2;
                    break;

                case "TR":
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 8));
                    Color rowColor = color != null ? color : Color.BLACK;
                    g2.setColor(rowColor);
                    g2.drawString(texto, x, y + 9);
                    y += LINE_H;
                    break;

                case "SEP":
                    y += 4;
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawLine(x, y, x + CONTENT_W, y);
                    y += 6;
                    break;

                case "FOOTER":
                    g2.setFont(new Font("SansSerif", Font.ITALIC, 8));
                    g2.setColor(Color.GRAY);
                    g2.drawString(texto, x, y + 9);
                    y += LINE_H;
                    break;
            }
        }

        // Número de página
        drawPageFooter(g2, pf, pageIndex);
        return PAGE_EXISTS;
    }

    private void drawPageHeader(Graphics2D g2, PageFormat pf, int pageIndex) {
        if (pageIndex == 0) return; // el encabezado completo va en la primera línea
        int w = (int) pf.getImageableWidth();
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(new Color(100,100,100));
        g2.drawString("Consulta Casa N° " + numeroCasa, MARGIN_X, 20);
        g2.drawLine(MARGIN_X, 24, MARGIN_X + CONTENT_W, 24);
    }

    private void drawPageFooter(Graphics2D g2, PageFormat pf, int pageIndex) {
        int yFoot = (int) pf.getImageableHeight() - 20;
        int w = (int) pf.getImageableWidth();
        g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g2.setColor(new Color(120,120,120));
        g2.drawLine(MARGIN_X, yFoot - 4, MARGIN_X + CONTENT_W, yFoot - 4);
        String pagTxt = "Página " + (pageIndex + 1);
        Rectangle2D r = g2.getFontMetrics().getStringBounds(pagTxt, g2);
        g2.drawString(pagTxt, (int)(w - r.getWidth()) / 2, yFoot + 6);
    }

    // ─────────────────────────────────────────────────────────────
    // Guardar como PDF en un archivo elegido por el usuario
    // ─────────────────────────────────────────────────────────────
    public void guardarPDF(java.awt.Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar reporte como PDF");
        fc.setSelectedFile(new File("Reporte_Casa_" + numeroCasa + ".pdf"));
        fc.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));

        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File destino = fc.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf"))
            destino = new File(destino.getAbsolutePath() + ".pdf");

        PrinterJob job = PrinterJob.getPrinterJob();

        // Buscar servicio de impresión PDF
        PrintService pdfService = encontrarServicioPDF();

        if (pdfService != null) {
            // Imprimir a PDF via PrintService
            try {
                job.setPrintService(pdfService);
                PageFormat pf = configurarA4(job);
                job.setPrintable(this, pf);

                PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
                attrs.add(new Destination(destino.toURI()));
                attrs.add(MediaSizeName.ISO_A4);
                attrs.add(OrientationRequested.PORTRAIT);

                job.print(attrs);
                JOptionPane.showMessageDialog(parent,
                    "PDF guardado correctamente en:\n" + destino.getAbsolutePath(),
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);
                // Abrir con el visor de PDF del sistema
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(destino);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                    "Error al guardar PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Fallback: imprimir a imagen y construir PDF manualmente
            try {
                generarPDFManual(destino);
                JOptionPane.showMessageDialog(parent,
                    "PDF guardado en:\n" + destino.getAbsolutePath(),
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(destino);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                    "Error al generar PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Buscar servicio de impresión PDF ───────────────────────────
    private PrintService encontrarServicioPDF() {
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
        attrs.add(new Destination(new File(System.getProperty("user.home"), "tmp.pdf").toURI()));
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        for (PrintService s : services) {
            String name = s.getName().toLowerCase();
            if (name.contains("pdf") || name.contains("print to file") || name.contains("cups-pdf"))
                return s;
        }
        return null;
    }

    private PageFormat configurarA4(PrinterJob job) {
        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();
        double w = 595.28 / 72.0 * 72; // A4
        double h = 841.89 / 72.0 * 72;
        paper.setSize(w, h);
        paper.setImageableArea(0, 0, w, h);
        pf.setPaper(paper);
        pf.setOrientation(PageFormat.PORTRAIT);
        return pf;
    }

    // ── Generador PDF manual (sin PrintService PDF) ────────────────
    // Construye un PDF usando primitivas del formato PDF (no requiere librería)
    private void generarPDFManual(File destino) throws IOException {
        // Usamos Java2D para renderizar cada página como imagen
        // y luego escribimos un PDF usando el formato básico
        // Calculamos número de páginas
        int usableH = PAGE_H - MARGIN_TOP - MARGIN_BOT - 60;
        int linesPerPage = usableH / LINE_H;
        int totalPages = (int) Math.ceil((double) totalLineas / linesPerPage);
        if (totalPages < 1) totalPages = 1;

        // Renderizar cada página como imagen con BufferedImage
        List<java.awt.image.BufferedImage> paginas = new ArrayList<>();
        for (int p = 0; p < totalPages; p++) {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                PAGE_W, PAGE_H, java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // Fondo blanco
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, PAGE_W, PAGE_H);

            // Simular PageFormat A4
            PageFormat pf = new PageFormat();
            Paper paper = new Paper();
            paper.setSize(PAGE_W, PAGE_H);
            paper.setImageableArea(0, 0, PAGE_W, PAGE_H);
            pf.setPaper(paper);

            print(g2, pf, p);
            g2.dispose();
            paginas.add(img);
        }

        // Escribir PDF con imágenes JPEG embebidas
        escribirPDFConImagenes(destino, paginas);
    }

    private void escribirPDFConImagenes(File destino, List<java.awt.image.BufferedImage> paginas) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<int[]> objOffsets = new ArrayList<>();
        List<byte[]> imageData = new ArrayList<>();

        // Comprimir páginas como JPEG
        for (java.awt.image.BufferedImage img : paginas) {
            ByteArrayOutputStream imgBaos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "jpeg", imgBaos);
            imageData.add(imgBaos.toByteArray());
        }

        // Escribir PDF
        try (FileOutputStream fos = new FileOutputStream(destino)) {
            List<Integer> xrefOffsets = new ArrayList<>();
            ByteArrayOutputStream body = new ByteArrayOutputStream();

            // %PDF-1.4 header
            byte[] header = "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n".getBytes();
            fos.write(header);
            int offset = header.length;

            // Objetos de imagen y páginas
            List<String> pageRefs = new ArrayList<>();
            List<String> imgRefs  = new ArrayList<>();

            int objNum = 1;

            // Imagen y Page por cada página
            for (int p = 0; p < paginas.size(); p++) {
                byte[] jpeg = imageData.get(p);

                // Image XObject
                xrefOffsets.add(offset);
                String imgObj = objNum + " 0 obj\n<< /Type /XObject /Subtype /Image"
                    + " /Width " + PAGE_W + " /Height " + PAGE_H
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8"
                    + " /Filter /DCTDecode /Length " + jpeg.length + " >>\nstream\n";
                byte[] imgObjBytes = imgObj.getBytes();
                fos.write(imgObjBytes);
                fos.write(jpeg);
                fos.write("\nendstream\nendobj\n".getBytes());
                offset += imgObjBytes.length + jpeg.length + "\nendstream\nendobj\n".length();
                imgRefs.add(objNum + " 0 R");
                objNum++;

                // Page Content stream
                xrefOffsets.add(offset);
                String content = "q " + PAGE_W + " 0 0 " + PAGE_H + " 0 0 cm /Img" + p + " Do Q";
                String contentObj = objNum + " 0 obj\n<< /Length " + content.length() + " >>\nstream\n"
                    + content + "\nendstream\nendobj\n";
                byte[] contentBytes = contentObj.getBytes();
                fos.write(contentBytes);
                offset += contentBytes.length;
                int contentRef = objNum;
                objNum++;

                // Page
                xrefOffsets.add(offset);
                String pageObj = objNum + " 0 obj\n<< /Type /Page /Parent 2 0 R"
                    + " /MediaBox [0 0 " + PAGE_W + " " + PAGE_H + "]"
                    + " /Contents " + contentRef + " 0 R"
                    + " /Resources << /XObject << /Img" + p + " " + (objNum-2) + " 0 R >> >> >>\nendobj\n";
                byte[] pageBytes = pageObj.getBytes();
                fos.write(pageBytes);
                offset += pageBytes.length;
                pageRefs.add(objNum + " 0 R");
                objNum++;
            }

            // Catalog (obj 1) — prepend al inicio, pero PDF requiere que estén ordenados
            // Pages dictionary
            xrefOffsets.add(1, offset); // insertar al frente
            StringBuilder pagesKids = new StringBuilder();
            for (String ref : pageRefs) pagesKids.append(ref).append(" ");
            String pagesObj = "2 0 obj\n<< /Type /Pages /Kids [" + pagesKids.toString().trim()
                + "] /Count " + paginas.size() + " >>\nendobj\n";
            byte[] pagesBytes = pagesObj.getBytes();
            fos.write(pagesBytes);
            offset += pagesBytes.length;

            // Catalog
            xrefOffsets.add(0, offset);
            String catalogObj = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
            byte[] catalogBytes = catalogObj.getBytes();
            fos.write(catalogBytes);
            offset += catalogBytes.length;

            // xref
            int xrefStart = offset;
            StringBuilder xref = new StringBuilder("xref\n0 " + (objNum) + "\n");
            xref.append("0000000000 65535 f \n");
            // Sort offsets by object number
            for (int i = 0; i < xrefOffsets.size(); i++) {
                xref.append(String.format("%010d 00000 n \n", xrefOffsets.get(i)));
            }
            fos.write(xref.toString().getBytes());

            // trailer
            String trailer = "trailer\n<< /Size " + objNum + " /Root 1 0 R >>\n"
                + "startxref\n" + xrefStart + "\n%%EOF\n";
            fos.write(trailer.getBytes());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────
    private static String ns(String s)  { return s != null ? s : ""; }
    private static String ntx(String s) { return (s != null && !s.isEmpty()) ? s : "—"; }
    private static String fmt(double d) { return String.format("%.2f", d); }
    private static String padCol(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(0, len - 1) + " ";
        return s + " ".repeat(len - s.length());
    }
    private static String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
    private static Color estadoColor(String estado) {
        if (estado == null) return Color.BLACK;
        switch (estado) {
            case "Pendiente": return new Color(180, 80, 0);
            case "Atrasado":  return new Color(180, 0, 0);
            case "Pagado":    return new Color(0, 120, 0);
            case "Pagada":    return new Color(0, 120, 0);
            case "Cancelado": return new Color(100, 0, 150);
            case "Cancelada": return new Color(100, 0, 150);
            case "Confirmada":return new Color(0, 80, 180);
            case "Anulada":   return new Color(100, 100, 100);
            default:          return Color.BLACK;
        }
    }
    @SuppressWarnings("unchecked")
    private static double sumaEstado(ArrayList<?> lista, String estado, int tipo) {
        double t = 0;
        for (Object o : lista) {
            if (tipo==1) { Alicuota a=(Alicuota)o; if (estado.equals(a.getEstado())) t+=a.getMonto(); }
            else if (tipo==2) { Multa m=(Multa)o; if (estado.equals(m.getEstado())) t+=m.getMonto(); }
            else { Arriendo a=(Arriendo)o; if (estado.equals(a.getEstado())) t+=a.getMontoMensual(); }
        }
        return t;
    }
}
