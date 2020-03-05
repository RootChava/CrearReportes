/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.root.crearreporte;

import java.awt.Color;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.WHITE;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

/**
 *
 * @author chava
 */
public class CrearReporte {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://192.168.0.55:3306/ADIP_CIMA", "jazmin", "jazmin");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT ap_MacAddress, modelo, serial, uplink FROM aps");
        try (PDDocument doc = new PDDocument()) {

            PDPage myPage = new PDPage();
            doc.addPage(myPage);
            String imgFileName = "src/main/resources/logo_adip.jpg";
            PDImageXObject pdImage = PDImageXObject.createFromFile(imgFileName, doc);
            int iw = pdImage.getWidth() / 2;
            int ih = pdImage.getHeight() / 2;
            float offset = 600f;

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                cont.drawImage(pdImage, 0, offset, iw, ih);

                cont.beginText();
                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.newLineAtOffset(420, 700);
                cont.showText(new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' YYYY", new Locale("es", "mx")).format(new Date()));
                cont.endText();

                cont.beginText();
                cont.setFont(PDType1Font.TIMES_ROMAN, 20);
                cont.newLineAtOffset(155, 600);
                String Titulo = "Reporte de estado de AP's por delegación";
                cont.showText(Titulo);
                cont.endText();

                cont.beginText();
                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.setLeading(15f);
                cont.newLineAtOffset(50f, 570f);
                String Delegacion = "Benito Juarez";
                cont.showText(Delegacion);
                cont.newLine();

                cont.endText();

                TableBuilder myTableBuilder = Table.builder()
                        .addColumnsOfWidth(120, 70, 140, 90)
                        .fontSize(12)
                        .font(HELVETICA);

                myTableBuilder
                        .addRow(Row.builder()
                                .add(TextCell.builder().text("Dirección MAC").borderWidth(1).build())
                                .add(TextCell.builder().text("Modelo").borderWidth(1).build())
                                .add(TextCell.builder().text("Serial").borderWidth(1).build())
                                .add(TextCell.builder().text("Estado").borderWidth(1).build())
                                .font(HELVETICA_BOLD)
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .build());
                while(rs.next()){
                    myTableBuilder
                        .addRow(Row.builder()
                                .add(TextCell.builder().text(rs.getString(1)).borderWidth(1).build())
                                .add(TextCell.builder().text(rs.getString(2)).borderWidth(1).build())
                                .add(TextCell.builder().text(rs.getString(3)).borderWidth(1).build())
                                .add(TextCell.builder().text(rs.getString(4)).borderWidth(1).build())
                                .build());
                }

                Table myTable = myTableBuilder.build();

                TableDrawer tableDrawer = TableDrawer.builder()
                        .contentStream(cont)
                        .startX(80F)
                        .startY(550F)
                        .endY(50F)
                        .table(myTable)
                        .build();

                tableDrawer.draw(() -> doc, () -> new PDPage(PDRectangle.A4), 50f);
            }

            doc.save("src/main/resources/wwii.pdf");
        }
    }
}
