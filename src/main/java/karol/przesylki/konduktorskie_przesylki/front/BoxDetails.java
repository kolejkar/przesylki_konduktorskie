package karol.przesylki.konduktorskie_przesylki.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import karol.przesylki.konduktorskie_przesylki.repository.PostBoxRepository;
import karol.przesylki.konduktorskie_przesylki.tables.Box_status;
import karol.przesylki.konduktorskie_przesylki.tables.Post_Client;
import karol.przesylki.konduktorskie_przesylki.tables.Postbox;

@Route("/detail/:postBoxID")
public class BoxDetails extends VerticalLayout implements BeforeEnterObserver {
    
    private Integer postBoxID;

    private Paragraph post_Details;
    private Paragraph post_Src;
    private Paragraph post_Dest;

    private H1 info;
    private H3 box_info;
    private H3 src_info;
    private H3 dest_info;

    @Autowired
    private PostBoxRepository postBoxRepo;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        postBoxID = event.getRouteParameters().getInteger("postBoxID").orElse(0);
        
        Postbox postBox = postBoxRepo.findById(postBoxID).orElse(null);
        GetClientDetails(postBox);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CONDUKTOR")))
		{
			GetConductorDetails(postBox);
		}
    }

    public BoxDetails()
    {
        info = new H1("Informacje o paczce konduktorskiej");
        box_info = new H3("Podstawowe informacje");
        src_info = new H3("Nadawca informacje");
        dest_info = new H3("Odbiorca informacje");
        post_Details = new Paragraph("*");
        post_Details.getStyle().set("white-space", "pre-line");
        post_Src = new Paragraph("*");
        post_Src.getStyle().set("white-space", "pre-line");
        post_Dest = new Paragraph("*");
        post_Dest.getStyle().set("white-space", "pre-line");

        add(info, box_info, post_Details, src_info, post_Src, dest_info, post_Dest);
    }

    private void GetPostDetails(Postbox postbox)
    {
        String info = "Data nadania: " +postbox.getTransportDate().toLocalDate().toString() + "\n";
        info = info + "Numer paczki: " + postbox.getId().toString() + "\n";
        info = info + "Numer pociągu: " + postbox.getTrain_number().toString() + "\n";
        info = info + "Typ paczki: " + postbox.getType().toString() + "\n";
        info = info + "Zawartość: " + postbox.getInside_item() + "\n";
        info = info + "Wartość zawartości: " + postbox.getInside_item_price().toString() + "zł\n";
        info = info + "Status paczki: " + postbox.getStatus().toString() + "\n";
        info = info + "Cena: " + postbox.getShipping_price().toString() + "zł";

        post_Details.setText(info);

    }

    private void GetPostSrcDetails(Postbox postbox)
    {
        Post_Client postClient = postbox.getSrc_client();

        String info = "Nimię: " + postClient.getName() + "\n";
        info = info + "Nazwisko: " + postClient.getSurname() + "\n";
        info = info + "Numer telefonu: " + postClient.getPhone_nuber() + "\n";
        info = info + "Adress: " + postClient.getAdress() + "\n";
        info = info + "Stacja nadania: " + postClient.getStation() + "\n";
        info = info + "Odbiór w punkcie przesyłek konduktorskich: " + postClient.getCollection_order();

        post_Src.setText(info);

    }

    private void GetPostDestDetails(Postbox postbox)
    {
        Post_Client postClient = postbox.getDest_client();

        String info = "Nimię: " + postClient.getName() + "\n";
        info = info + "Nazwisko: " + postClient.getSurname() + "\n";
        info = info + "Numer telefonu: " + postClient.getPhone_nuber() + "\n";
        info = info + "Adress: " + postClient.getAdress() + "\n";
        info = info + "Stacja odbioru: " + postClient.getStation() + "\n";
        info = info + "Odbiór w punkcie przesyłek konduktorskich: " + postClient.getCollection_order();

        post_Dest.setText(info);

    }

    private void GetClientDetails(Postbox postbox)
    {
        GetPostDetails(postbox);
        GetPostSrcDetails(postbox);
        GetPostDestDetails(postbox);
    }

    private void GetConductorDetails(Postbox postBox)
    {
        if (postBox.getStatus() == Box_status.AcceptPost || postBox.getStatus() == Box_status.PayedPost)
        {
            Button accept = new Button("Akceptuj paczkę");
            Button dismis = new Button("Odrzuć paczkę");

            accept.addClickListener(e -> {
                postBox.setStatus(Box_status.InProgressPost);
            });

            dismis.addClickListener(e -> {
                postBoxRepo.delete(postBox);
                //przekierowanie do panelu konduktora
            });

            add(accept, dismis);
        }
        if (postBox.getStatus() == Box_status.InProgressPost)
        {
            Button finish = new Button("Wydaj paczkę");
            finish.addClickListener(e -> {
                postBox.setStatus(Box_status.DeliveredPost);
            });
            add(finish);
        }
    }
}
