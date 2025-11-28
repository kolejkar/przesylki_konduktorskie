package karol.przesylki.konduktorskie_przesylki.front;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;

@Route("/order/fin")
public class Order_QRcode extends VerticalLayout implements BeforeEnterObserver {

    private Image img;
    private Label info;

    Order_QRcode()
    {
        info = new Label("Etykieta wysyłkowa:");
        img = new Image();
        add(info);
        add(img);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // TODO Auto-generated method stub
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        GenerateQR(parametersMap.get("order").get(0));
    }

    public void GenerateQR(String text)
    {
        String alt = "Kod do wysyłki :)";
        QRCodeWriter codeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        img.setSrc(asSreamRresourse(MatrixToImageWriter.toBufferedImage(bitMatrix)));
        
    }

    private AbstractStreamResource asSreamRresourse(BufferedImage bufferedImage)
    {
        return new StreamResource("barcode.png", (output, session) -> {
            try {
                ImageIO.write(bufferedImage, "png", output);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
