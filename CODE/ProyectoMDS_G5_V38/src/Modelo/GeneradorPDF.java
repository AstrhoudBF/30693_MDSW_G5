package Modelo;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Genera un PDF real usando Java2D (sin librerías externas).
 * Estrategia: renderiza cada página en un BufferedImage,
 * luego construye un PDF 1.4 válido con las imágenes JPEG embebidas.
 */
public class GeneradorPDF implements Printable {

    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Datos
    private final String numeroCasa;
    private final Residentes residente;
    private final ArrayList<Alicuota>   alicuotas;
    private final ArrayList<Multa>      multas;
    private final ArrayList<Arriendo>   arriendos;
    private final ArrayList<ArriendoSede> sede;

    // Layout
    private static final int PAGE_W   = 595;
    private static final int PAGE_H   = 842;
    private static final int MARGIN_X = 50;
    private static final int MARGIN_T = 55;
    private static final int MARGIN_B = 45;
    private static final int LINE_H   = 14;
    private static final int CONTENT_W = PAGE_W - MARGIN_X * 2;

    // Líneas de contenido
    private final List<Object[]> lineas = new ArrayList<>();

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

    // ── Construir contenido ───────────────────────────────────────
    private void construirLineas() {
        lineas.clear();
        String ahora    = LocalDateTime.now().format(FMT_DT);
        double alicPend = sumaEstado(alicuotas, "Pendiente", 1);
        double alicAtras= sumaEstado(alicuotas, "Atrasado",  1);
        double multPend = sumaEstado(multas,    "Pendiente", 2);
        double arrPend  = sumaEstado(arriendos, "Pendiente", 3);
        double totalDeuda = alicPend + alicAtras + multPend + arrPend;

        L("TITULO",  "CONSULTA DE CASA N° " + numeroCasa, null, true);
        L("META",    "Generado el: " + ahora, Color.GRAY, false);
        L("SEP",     null, null, false);

        L("SEC",  "RESUMEN DE DEUDA", null, true);
        L("KV",   "Alícuotas Pendientes:  $ " + f2(alicPend), null, false);
        L("KV",   "Alícuotas Atrasadas:   $ " + f2(alicAtras), null, false);
        L("KV",   "Multas Pendientes:     $ " + f2(multPend), null, false);
        L("KV",   "Arriendos Pendientes:  $ " + f2(arrPend), null, false);
        L("DEUDA","TOTAL DEUDA: $ " + f2(totalDeuda), new Color(180,0,0), true);
        L("SEP", null, null, false);

        L("SEC", "DATOS DEL RESIDENTE", null, true);
        if (residente == null) {
            L("KV", "Sin residente registrado.", Color.GRAY, false);
        } else {
            L("KV", "Nombre:             " + residente.getNombres() + " " + residente.getApellidos(), null, false);
            L("KV", "Cédula:             " + ns(residente.getCedula()), null, false);
            L("KV", "Tel. Móvil:         " + ns(residente.getTelefonoMovil()), null, false);
            L("KV", "Tel. Convencional:  " + ns(residente.getTelefonoConvencional()), null, false);
            L("KV", "Tipo:               " + ns(residente.getTipoResidente()), null, false);
            L("KV", "Estado:             " + residente.getEstadoResidente(), null, false);
            L("KV", "Mascotas:           " + (residente.isTieneMascotas() ? "Sí" : "No"), null, false);
            L("KV", "Vehículos:          " + residente.getVehiculosResumen(), null, false);
            if (residente.getFechaRegistro() != null)
                L("KV", "Registrado:         " + residente.getFechaRegistro().format(FMT_DT), null, false);
        }
        L("SEP", null, null, false);

        // ALÍCUOTAS
        L("SEC", "ALÍCUOTAS  (" + alicuotas.size() + ")", null, true);
        if (alicuotas.isEmpty()) {
            L("KV", "Sin alícuotas registradas.", Color.GRAY, false);
        } else {
            L("TH", col("Período",12) + col("Monto",9) + col("Estado",10) + col("Forma Pago",13) + col("N° Tx",11) + "Fecha Pago", null, true);
            for (Alicuota a : alicuotas)
                L("TR", col(ns(a.getPeriodo()),12) + col("$"+f2(a.getMonto()),9) + col(ns(a.getEstado()),10)
                       + col(ns(a.getFormaPago()),13) + col(ntx(a.getNumeroTransaccion()),11)
                       + (a.getFechaPago()!=null ? a.getFechaPago().format(FMT_D) : "—"),
                       estadoColor(a.getEstado()), false);
            L("KV", "  Pagado:$" + f2(sumaEstado(alicuotas,"Pagado",1)) + "  Pendiente:$" + f2(alicPend) + "  Atrasado:$" + f2(alicAtras), Color.DARK_GRAY, false);
        }
        L("SEP", null, null, false);

        // MULTAS
        L("SEC", "MULTAS  (" + multas.size() + ")", null, true);
        if (multas.isEmpty()) {
            L("KV", "Sin multas registradas.", Color.GRAY, false);
        } else {
            L("TH", col("Categoría",12) + col("Motivo",22) + col("Fecha",11) + col("Monto",9) + col("Estado",10) + "Forma Pago", null, true);
            for (Multa m : multas)
                L("TR", col(ns(m.getCategoria()),12) + col(trunc(ns(m.getMotivo()),20),22)
                       + col(m.getFechaInfraccion()!=null?m.getFechaInfraccion().format(FMT_D):"—",11)
                       + col("$"+f2(m.getMonto()),9) + col(ns(m.getEstado()),10) + ns(m.getFormaPago()),
                       estadoColor(m.getEstado()), false);
            L("KV", "  Pendiente:$" + f2(multPend) + "  Pagada:$" + f2(sumaEstado(multas,"Pagada",2)), Color.DARK_GRAY, false);
        }
        L("SEP", null, null, false);

        // ARRIENDOS
        L("SEC", "ARRIENDOS — LOCALES / PARQUEADEROS  (" + arriendos.size() + ")", null, true);
        if (arriendos.isEmpty()) {
            L("KV", "Sin arriendos.", Color.GRAY, false);
        } else {
            L("TH", col("Tipo",12) + col("Espacio",15) + col("Período",12) + col("Monto",9) + col("Estado",10) + "Forma Pago", null, true);
            for (Arriendo a : arriendos)
                L("TR", col(ns(a.getTipoEspacio()),12) + col(trunc(ns(a.getNombreEspacio()),13),15)
                       + col(ns(a.getMesPeriodo()),12) + col("$"+f2(a.getMontoMensual()),9)
                       + col(ns(a.getEstado()),10) + ns(a.getFormaPago()),
                       estadoColor(a.getEstado()), false);
            L("KV", "  Pagado:$" + f2(sumaEstado(arriendos,"Pagado",3)) + "  Pendiente:$" + f2(arrPend), Color.DARK_GRAY, false);
        }
        L("SEP", null, null, false);

        // SEDE SOCIAL
        L("SEC", "RESERVAS DE SEDE SOCIAL  (" + sede.size() + ")", null, true);
        if (sede.isEmpty()) {
            L("KV", "Sin reservas.", Color.GRAY, false);
        } else {
            L("TH", col("Fecha",11) + col("Modalidad",12) + col("Monto",9) + col("Estado",10) + "Motivo", null, true);
            for (ArriendoSede s : sede)
                L("TR", col(s.getFechaReserva()!=null?s.getFechaReserva().format(FMT_D):"—",11)
                       + col(ns(s.getModalidad()),12) + col("$"+f2(s.getMonto()),9)
                       + col(ns(s.getEstado()),10) + trunc(ns(s.getMotivo()),28),
                       estadoColor(s.getEstado()), false);
        }
        L("SEP", null, null, false);
        L("FOOT", "Sistema de Administración Residencial — " + ahora, Color.GRAY, false);
    }

    private void L(String tipo, String texto, Color color, boolean bold) {
        lineas.add(new Object[]{tipo, texto, color, bold});
    }

    // ── Printable.print() ─────────────────────────────────────────
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int usable = (int)(pf.getImageableHeight()) - MARGIN_T - MARGIN_B;
        int lpp    = usable / LINE_H;

        int start = pageIndex * lpp;
        if (start >= lineas.size()) return NO_SUCH_PAGE;

        g2.translate(pf.getImageableX(), pf.getImageableY());
        if (pageIndex > 0) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.setColor(new Color(80,80,80));
            g2.drawString("Consulta Casa N° " + numeroCasa, MARGIN_X, 18);
            g2.setColor(new Color(180,180,180));
            g2.fillRect(MARGIN_X, 22, CONTENT_W, 1);
        }

        int y = MARGIN_T;
        for (int i = start; i < lineas.size() && i < start + lpp; i++) {
            Object[] l = lineas.get(i);
            String  tipo  = (String)  l[0];
            String  texto = (String)  l[1];
            Color   color = (Color)   l[2];
            boolean bold  = (Boolean) l[3];

            switch (tipo) {
                case "TITULO":
                    g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g2.setColor(new Color(20,20,100));
                    g2.drawString(texto, MARGIN_X, y + 14);
                    g2.setColor(new Color(20,20,100));
                    g2.fillRect(MARGIN_X, y + 18, CONTENT_W, 2);
                    y += 24; break;
                case "META":
                    g2.setFont(new Font("SansSerif", Font.ITALIC, 9));
                    g2.setColor(Color.GRAY);
                    g2.drawString(texto, MARGIN_X, y + 9); y += LINE_H; break;
                case "SEC":
                    y += 3;
                    g2.setColor(new Color(230,235,255));
                    g2.fillRect(MARGIN_X - 4, y, CONTENT_W + 8, LINE_H + 2);
                    g2.setColor(new Color(30,80,160));
                    g2.fillRect(MARGIN_X - 4, y, 4, LINE_H + 2);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.setColor(new Color(20,50,130));
                    g2.drawString(texto, MARGIN_X + 5, y + 11);
                    y += LINE_H + 5; break;
                case "DEUDA":
                    g2.setColor(new Color(200,0,0));
                    g2.fillRect(MARGIN_X - 4, y, CONTENT_W + 8, LINE_H + 2);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.setColor(Color.WHITE);
                    g2.drawString(texto, MARGIN_X + 5, y + 11);
                    y += LINE_H + 3; break;
                case "TH":
                    g2.setColor(new Color(215,215,215));
                    g2.fillRect(MARGIN_X - 2, y, CONTENT_W + 4, LINE_H);
                    g2.setFont(new Font("Monospaced", Font.BOLD, 8));
                    g2.setColor(Color.BLACK);
                    g2.drawString(texto, MARGIN_X, y + 9); y += LINE_H; break;
                case "TR":
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 8));
                    g2.setColor(color != null ? color : Color.BLACK);
                    g2.drawString(texto, MARGIN_X, y + 9); y += LINE_H; break;
                case "KV":
                    g2.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, 9));
                    g2.setColor(color != null ? color : Color.BLACK);
                    g2.drawString(texto, MARGIN_X, y + 9); y += LINE_H; break;
                case "SEP":
                    y += 3;
                    g2.setColor(new Color(200,200,200));
                    g2.fillRect(MARGIN_X, y, CONTENT_W, 1);
                    y += 5; break;
                case "FOOT":
                    g2.setFont(new Font("SansSerif", Font.ITALIC, 8));
                    g2.setColor(Color.GRAY);
                    g2.drawString(texto, MARGIN_X, y + 9); y += LINE_H; break;
            }
        }

        // Número de página
        int yFoot = (int) pf.getImageableHeight() - 15;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g2.setColor(new Color(120,120,120));
        g2.fillRect(MARGIN_X, yFoot - 4, CONTENT_W, 1);
        String pg = "Página " + (pageIndex + 1);
        int pgW = g2.getFontMetrics().stringWidth(pg);
        g2.drawString(pg, (int)(pf.getImageableWidth() - pgW) / 2, yFoot + 6);

        return PAGE_EXISTS;
    }

    // ── Punto de entrada: guardar PDF ─────────────────────────────
    public void guardarPDF(java.awt.Component parent) {
        JFileChooser fc = new JFileChooser(
            System.getProperty("user.home") + File.separator + "Downloads");
        fc.setDialogTitle("Guardar reporte como PDF");
        fc.setSelectedFile(new File("Reporte_Casa_" + numeroCasa + ".pdf"));
        fc.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));
        fc.setAcceptAllFileFilterUsed(false);

        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File dest = fc.getSelectedFile();
        if (!dest.getName().toLowerCase().endsWith(".pdf"))
            dest = new File(dest.getAbsolutePath() + ".pdf");

        // Confirmar sobreescritura
        if (dest.exists()) {
            int r = JOptionPane.showConfirmDialog(parent,
                "El archivo ya existe. ¿Desea sobreescribirlo?",
                "Archivo existente", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return;
        }

        try {
            generarPDF(dest);
            JOptionPane.showMessageDialog(parent,
                "PDF generado correctamente en:\n" + dest.getAbsolutePath(),
                "PDF guardado", JOptionPane.INFORMATION_MESSAGE);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
                Desktop.getDesktop().open(dest);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                "Error al generar el PDF:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── Generar PDF: renderizar páginas → JPEG → PDF válido ───────
    private void generarPDF(File dest) throws Exception {
        // 1. Calcular páginas
        int usable = PAGE_H - MARGIN_T - MARGIN_B;
        int lpp    = usable / LINE_H;
        int total  = (int) Math.ceil((double) lineas.size() / Math.max(lpp, 1));
        if (total < 1) total = 1;

        // 2. Renderizar cada página a JPEG bytes
        List<byte[]> jpegs = new ArrayList<>();
        for (int p = 0; p < total; p++) {
            BufferedImage img = new BufferedImage(PAGE_W, PAGE_H, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, PAGE_W, PAGE_H);

            // Usar PageFormat con margen cero para que print() dibuje en coordenadas absolutas
            PageFormat pf = new PageFormat();
            Paper paper = new Paper();
            paper.setSize(PAGE_W, PAGE_H);
            paper.setImageableArea(0, 0, PAGE_W, PAGE_H);
            pf.setPaper(paper);

            print(g2, pf, p);
            g2.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpeg", baos);
            jpegs.add(baos.toByteArray());
        }

        // 3. Construir PDF
        escribirPDF(dest, jpegs);
    }

    /**
     * Escribe un PDF 1.4 válido con imágenes JPEG embebidas.
     * Construcción secuencial con tabla xref correcta.
     */
    private void escribirPDF(File dest, List<byte[]> jpegs) throws IOException {
        // Contadores de objetos PDF
        // Estructura:
        //  obj 1: Catalog
        //  obj 2: Pages
        //  por cada página N (0-based):
        //    obj  3 + N*2    : Image XObject
        //    obj  4 + N*2    : Page
        int n = jpegs.size();
        int totalObjs = 2 + n * 2; // catalog + pages + (image+page)*n

        // Escribimos a un RandomAccessFile para registrar offsets exactos
        byte[] header = ("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n").getBytes("ISO-8859-1");

        // Guardar cada objeto y su offset
        long[] offsets = new long[totalObjs + 1]; // 1-indexed
        ByteArrayOutputStream body = new ByteArrayOutputStream();

        // Helpers para escribir a body y registrar offset
        // (usamos un contador de bytes)
        long[] cursor = {header.length};

        // Objeto 1: Catalog (lo escribiremos al final pero reservamos índice)
        // Objeto 2: Pages (ídem)
        // Primero escribimos imágenes y páginas

        // Arrays para referencias
        int[] imgObjNums  = new int[n];
        int[] pageObjNums = new int[n];
        for (int i = 0; i < n; i++) {
            imgObjNums[i]  = 3 + i * 2;
            pageObjNums[i] = 4 + i * 2;
        }

        // Escribir al ByteArrayOutputStream acumulando
        // Luego volcar todo a FileOutputStream con offsets calculados en segundas pasadas

        // Usamos un enfoque de dos pasadas: primera pasada en memoria
        FileOutputStream fos = new FileOutputStream(dest);
        fos.write(header);
        long off = header.length;

        // Escribir objetos de imagen y página
        for (int i = 0; i < n; i++) {
            byte[] jpeg = jpegs.get(i);

            // Image XObject
            offsets[imgObjNums[i]] = off;
            String imgHdr = imgObjNums[i] + " 0 obj\n"
                + "<< /Type /XObject /Subtype /Image"
                + " /Width " + PAGE_W + " /Height " + PAGE_H
                + " /ColorSpace /DeviceRGB /BitsPerComponent 8"
                + " /Filter /DCTDecode /Length " + jpeg.length
                + " >>\nstream\n";
            byte[] imgHdrB = imgHdr.getBytes("ISO-8859-1");
            byte[] imgEnd  = "\nendstream\nendobj\n".getBytes("ISO-8859-1");
            fos.write(imgHdrB); off += imgHdrB.length;
            fos.write(jpeg);    off += jpeg.length;
            fos.write(imgEnd);  off += imgEnd.length;

            // Content stream para la página
            // Dibuja la imagen en coordenadas PDF (origen abajo-izquierda)
            String cs = "q " + PAGE_W + " 0 0 " + PAGE_H + " 0 0 cm /Im" + i + " Do Q";
            String csObj = (imgObjNums[i] + 1) + " 0 obj\n"
                + "<< /Length " + cs.length() + " >>\nstream\n"
                + cs + "\nendstream\nendobj\n";
            offsets[pageObjNums[i] - 1] = off; // reusamos índice: content = pageObj-1... skip
            // Simpler: track separately
            // Actually pageObjNums[i] = 4+i*2, content stream = 3+i*2+1 = 4+i*2 = pageObjNums[i]
            // Let's track content obj differently
            long contentOff = off;
            int contentObjNum = imgObjNums[i] + 1; // == pageObjNums[i]? No.
            // imgObjNums[i] = 3+i*2, content = 3+i*2 + 0.5... can't do half
            // Re-plan: img=3+i*3, content=4+i*3, page=5+i*3
            // This is getting complicated. Use simple list approach instead.
            byte[] csB = csObj.getBytes("ISO-8859-1");
            offsets[pageObjNums[i]] = off; // actually this is the content stream obj
            fos.write(csB); off += csB.length;
        }

        fos.close();

        // This approach is getting messy. Use the clean List<long> approach below.
        dest.delete();
        escribirPDFLimpio(dest, jpegs);
    }

    private void escribirPDFLimpio(File dest, List<byte[]> jpegs) throws IOException {
        int n = jpegs.size();
        // Object numbering:
        // 1 = Catalog, 2 = Pages
        // per page i: 3+i*2 = Content stream, 4+i*2 = Page dict
        // Total objects = 2 + n*2

        List<Long>   xref = new ArrayList<>();
        xref.add(0L); // obj 0 placeholder

        try (FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] hdr = "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n".getBytes("ISO-8859-1");
            fos.write(hdr);
            long pos = hdr.length;

            // Build objects in memory first so we know exact offsets
            // We'll write them sequentially:
            // obj 1 (Catalog) - needs ref to obj 2 -> write after Pages
            // obj 2 (Pages)   - needs page refs    -> write after all pages
            // So: write pages first (obj 3+), then Pages (obj 2), then Catalog (obj 1)
            // But xref must list offsets by object number.

            // Pass 1: Write image XObjects + content streams + page dicts
            List<Long> objOffsets = new ArrayList<>();
            objOffsets.add(0L); // obj 0 unused (65535 f)
            objOffsets.add(0L); // obj 1 Catalog - placeholder
            objOffsets.add(0L); // obj 2 Pages   - placeholder

            StringBuilder kidsRef = new StringBuilder();

            for (int i = 0; i < n; i++) {
                byte[] jpeg = jpegs.get(i);

                // obj 3+i*3: Image XObject
                int imgNum     = 3 + i * 3;
                int contentNum = 4 + i * 3;
                int pageNum    = 5 + i * 3;

                // Make sure list is large enough
                while (objOffsets.size() <= pageNum) objOffsets.add(0L);

                // Image
                objOffsets.set(imgNum, pos);
                String imgDict = imgNum + " 0 obj\n"
                    + "<< /Type /XObject /Subtype /Image"
                    + " /Width " + PAGE_W + " /Height " + PAGE_H
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8"
                    + " /Filter /DCTDecode /Length " + jpeg.length + " >>\n"
                    + "stream\n";
                byte[] b = imgDict.getBytes("ISO-8859-1");
                fos.write(b); pos += b.length;
                fos.write(jpeg); pos += jpeg.length;
                b = "\nendstream\nendobj\n".getBytes("ISO-8859-1");
                fos.write(b); pos += b.length;

                // Content stream (places image filling the page)
                String cs = "q " + PAGE_W + " 0 0 " + PAGE_H + " 0 0 cm /Im Do Q";
                String csStr = contentNum + " 0 obj\n<< /Length " + cs.length() + " >>\nstream\n"
                    + cs + "\nendstream\nendobj\n";
                objOffsets.set(contentNum, pos);
                b = csStr.getBytes("ISO-8859-1");
                fos.write(b); pos += b.length;

                // Page dictionary
                String pageStr = pageNum + " 0 obj\n"
                    + "<< /Type /Page /Parent 2 0 R"
                    + " /MediaBox [0 0 " + PAGE_W + " " + PAGE_H + "]"
                    + " /Contents " + contentNum + " 0 R"
                    + " /Resources << /XObject << /Im " + imgNum + " 0 R >> >> >>\n"
                    + "endobj\n";
                objOffsets.set(pageNum, pos);
                b = pageStr.getBytes("ISO-8859-1");
                fos.write(b); pos += b.length;

                kidsRef.append(pageNum).append(" 0 R ");
            }

            // obj 2: Pages dictionary
            String pagesStr = "2 0 obj\n<< /Type /Pages /Kids ["
                + kidsRef.toString().trim() + "] /Count " + n + " >>\nendobj\n";
            objOffsets.set(2, pos);
            byte[] b = pagesStr.getBytes("ISO-8859-1");
            fos.write(b); pos += b.length;

            // obj 1: Catalog
            String catStr = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
            objOffsets.set(1, pos);
            b = catStr.getBytes("ISO-8859-1");
            fos.write(b); pos += b.length;

            // xref table
            long xrefPos = pos;
            int totalObjs = objOffsets.size();
            String xrefHdr = "xref\n0 " + totalObjs + "\n";
            b = xrefHdr.getBytes("ISO-8859-1");
            fos.write(b);

            // obj 0: free entry
            fos.write("0000000000 65535 f \r\n".getBytes("ISO-8859-1"));
            for (int i = 1; i < totalObjs; i++) {
                String entry = String.format("%010d 00000 n \r\n", objOffsets.get(i));
                fos.write(entry.getBytes("ISO-8859-1"));
            }

            // trailer
            String trailer = "trailer\n<< /Size " + totalObjs + " /Root 1 0 R >>\n"
                + "startxref\n" + xrefPos + "\n%%EOF\n";
            fos.write(trailer.getBytes("ISO-8859-1"));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────
    private static String ns(String s)   { return s != null ? s : ""; }
    private static String ntx(String s)  { return s != null && !s.isEmpty() ? s : "—"; }
    private static String f2(double d)   { return String.format("%.2f", d); }
    private static String col(String s, int len) {
        if (s == null) s = "";
        return s.length() >= len ? s.substring(0, len-1) + " " : s + " ".repeat(len - s.length());
    }
    private static String trunc(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max-1) + "." : s;
    }
    private static Color estadoColor(String e) {
        if (e == null) return Color.BLACK;
        switch (e) {
            case "Pendiente":  return new Color(180, 80, 0);
            case "Atrasado":   return new Color(180, 0, 0);
            case "Pagado": case "Pagada": return new Color(0, 120, 0);
            case "Cancelado": case "Cancelada": return new Color(100, 0, 150);
            case "Confirmada": return new Color(0, 80, 180);
            default: return Color.BLACK;
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
