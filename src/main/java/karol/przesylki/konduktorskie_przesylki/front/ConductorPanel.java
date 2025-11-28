package karol.przesylki.konduktorskie_przesylki.front;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpHeaders;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.modeler.NotificationInfo;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;

import karol.przesylki.konduktorskie_przesylki.front.menu.MenuGUI;
import karol.przesylki.konduktorskie_przesylki.repository.ConductorRepository;
import sk.smartbase.component.qrscanner.QrScanner;

@Route("/conductor")
@Push
public class ConductorPanel extends VerticalLayout {

    private QrScanner qrScanner;

    ConductorPanel(ConductorRepository cRepository)
    {
        MenuGUI menu = new MenuGUI(cRepository);
        add(menu);

        Button scan = new Button("Skanuj kod QR");
        Button close = new Button("Zamknij skanowanie kodu QR");
        
        qrScanner = new QrScanner(false);
        qrScanner.setFrequency(1);
        qrScanner.setActive(true);
        qrScanner.setDebug(true);

        qrScanner.setConsumer(event -> {
	            if(!event.equalsIgnoreCase(QrScanner.ERROR_DECODING_QR_CODE)) {
		            //qrScanner.setActive(false);
	                System.out.println("consumer event value: " + event);
                    String hostname =VaadinRequest.getCurrent().getHeader("host");
                    String url =  event.toString();
                    Pattern pattern = Pattern.compile(hostname + "/detail/\\d+");
                    Matcher match = pattern.matcher(hostname + "/" + url);
                    if (match.matches())
                    {
                        UI.getCurrent().getPage().setLocation(url);
                    }
                }
        });

        scan.addClickListener(e -> {     
            add(qrScanner);
        });

        close.addClickListener(e ->
            {
                remove(qrScanner);
            });

        add(scan, close);
    }

}
