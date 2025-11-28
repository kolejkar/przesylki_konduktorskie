package karol.przesylki.konduktorskie_przesylki.front;

import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;

import karol.przesylki.konduktorskie_przesylki.front.menu.MenuGUI;
import karol.przesylki.konduktorskie_przesylki.repository.ConductorRepository;
import karol.przesylki.konduktorskie_przesylki.repository.PostBoxRepository;
import karol.przesylki.konduktorskie_przesylki.repository.Post_ClientRepository;
import karol.przesylki.konduktorskie_przesylki.tables.Box_Dimensions;
import karol.przesylki.konduktorskie_przesylki.tables.Box_size;
import karol.przesylki.konduktorskie_przesylki.tables.Box_status;
import karol.przesylki.konduktorskie_przesylki.tables.Post_Client;
import karol.przesylki.konduktorskie_przesylki.tables.Postbox;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;

@Route("/order")
public class Order extends VerticalLayout {

    @Autowired
    public PostBoxRepository postBoxRepo;

    @Autowired
    public Post_ClientRepository clientRepo;

    private Binder<Box_size> binder_size;
    private Binder<Postbox> binder_post;
    private Binder<Post_Client> binder_client1;
    private Binder<Post_Client> binder_client2;
    private Checkbox overweight;
    private Checkbox src_punkt;
    private Checkbox dest_punkt;
    private Checkbox cilinder;
    private Label total_price;

    private Double shipping_price;

    private List<String> punkt_stations = new ArrayList<>(Arrays.asList("Warszawa Centralna","Szczecin Główny","Lublin Główny","Gdynia Główna")); 

    private void ValidateOrder()
    {
        binder_client1.validate();
        binder_client2.validate();
        binder_post.validate();
        binder_size.validate();
    }

    private Postbox postBox;
    private Box_size box_size;
    private Post_Client post_Client1;
    private Post_Client post_Client2;

    private void InitValidation()
    {
        binder_size = new BeanValidationBinder<>(Box_size.class);
        binder_post = new BeanValidationBinder<>(Postbox.class);
        binder_client1 = new BeanValidationBinder<>(Post_Client.class);
        binder_client2 = new BeanValidationBinder<>(Post_Client.class);

        binder_post.setBean(postBox);
        binder_size.setBean(box_size);
        binder_client1.setBean(post_Client1);
        binder_client2.setBean(post_Client2);
    }

    private void FinishValidate()
    {
        if (binder_client1.validate().isOk() && binder_client2.validate().isOk() && binder_post.validate().isOk() && binder_size.validate().isOk())
        {
            postBox.setDest_client(post_Client2);
            postBox.setSrc_client(post_Client1);
            clientRepo.save(post_Client1);
            clientRepo.save(post_Client2);
            postBox.setShipping_price(shipping_price);
            postBox.setTransportDate(LocalDateTime.now());
            postBox.setStatus(Box_status.PayedPost);
            if (overweight.getValue())
            {
                postBox.setType(Box_Dimensions.Oversize);
            }
            else if (cilinder.getValue())
            {
                postBox.setType(Box_Dimensions.Cilinder);
            }
            else
            {
                postBox.setType(Box_Dimensions.Normal);
            }
            postBoxRepo.save(postBox);
            String viewURL = "detail/" + postBox.getId();
            QueryParameters param = QueryParameters.simple(Collections.singletonMap("order", viewURL));
            UI.getCurrent().navigate("/order/fin", param);
        }
    }

    public Order(ConductorRepository conductorRepo)
    {
        MenuGUI menu = new MenuGUI(conductorRepo);
		add(menu);

        shipping_price = 0.0d;
        
        postBox = new Postbox();
        box_size = new Box_size();
        post_Client1 = new Post_Client();
        post_Client2 = new Post_Client();

        InitValidation();

        H1 info = new H1("Zamawianie przesyłki konduktorskiej");
        total_price = new Label("");

        src_punkt = new Checkbox("Punkt przesyłek konduktorskich", false);
        dest_punkt = new Checkbox("Punkt przesyłek konduktorskich", false);
        overweight = new Checkbox("Przesyłka ponadgabarytowa", false);

        add(info);
        Check_box();
        Set_Post();
        Check_src();
        Check_dest();
        add(total_price);

        Button button = new Button("Zapłać");
        button.addClickListener(e -> {
            FinishValidate();
        });
        add(button);
    }

    private void Set_Post()
    {
        H3 info = new H3("Inne informacje");

        TextField post_train_number = new TextField("Numer pociągu:");
        binder_post.forField(post_train_number)
            .withNullRepresentation("")
            .asRequired("Musi być podany numer pociągu dla danej relacji")
            .bind(Postbox::getTrain_number, Postbox::setTrain_number);

        TextField post_item = new TextField("Opisz zawartość paczki:");
        binder_post.forField(post_item)
            .withNullRepresentation("")
            .asRequired("Musisz opisać zawartość paczki:")
            .bind(Postbox::getInside_item, Postbox::setInside_item);

            TextField post_item_price = new TextField("Podaj wartość zawartości paczki w zł:"); 
            binder_post.forField(post_item_price)
                .withConverter(new StringToDoubleConverter("Wartość nie jest liczbą"))
                .withNullRepresentation(0.0d)
                .asRequired("Wymagana wartośc zawartości paczki w zł")
                .bind(Postbox::getInside_item_price, Postbox::setInside_item_price);

        add(info, post_train_number, post_item, post_item_price);
    }

    private void Check_dest()
    {
        H3 info = new H3("Odbiorca");

        dest_punkt.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });

        TextField dest_name = new TextField("Imię:"); 
        binder_client2.forField(dest_name)
            .asRequired("Imię jest wymagne")
            .withNullRepresentation("")
            .bind(Post_Client::getName, Post_Client::setName);
        TextField dest_surname = new TextField("Nazwisko:");
        binder_client2.forField(dest_surname)
            .asRequired("Nazwisko jest wymagne")
            .withNullRepresentation("")
            .bind(Post_Client::getSurname, Post_Client::setSurname);
        TextField dest_phone = new TextField("Numer telefonu:"); 
        binder_client2.forField(dest_phone)
            .asRequired("Numer telefonu jest wymagny")
            .withNullRepresentation("")
            .bind(Post_Client::getPhone_nuber, Post_Client::setPhone_nuber);
        TextField dest_station = new TextField("Stacja:"); 
        binder_client2.forField(dest_station)
            .asRequired("Stacja docelowa jest wymagna")
            .withNullRepresentation("")
            .withValidator(station -> !dest_punkt.getValue() || (dest_punkt.getValue() && punkt_stations.stream().anyMatch(p -> p.equals(station.toString()))),"Zły punkt przesyłek konduktorskich")
            .bind(Post_Client::getStation, Post_Client::setStation); 
        TextField dest_adress = new TextField("Adress:");
        binder_client2.forField(dest_adress)
            .asRequired("Adres jest wymagny")
            .withNullRepresentation("")
            .bind(Post_Client::getAdress,Post_Client::setAdress);
        binder_client2.forField(dest_punkt).bind(Post_Client::getCollection_order, Post_Client::setCollection_order);
        
        add(info, dest_name, dest_surname, dest_phone, dest_station, dest_adress, dest_punkt);
    }

    private void Check_src()
    {   
        H3 info = new H3("Nadawca");

        src_punkt.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });

        TextField src_name = new TextField("Imię:"); 
        binder_client1.forField(src_name)
            .asRequired("Imię jest wymagne")
            .withNullRepresentation("")
            .bind(Post_Client::getName, Post_Client::setName);
        TextField src_surname = new TextField("Nazwisko:");
        binder_client1.forField(src_surname)
            .asRequired("Nazwisko jest wymagne")
            .withNullRepresentation("")
            .bind(Post_Client::getSurname, Post_Client::setSurname);
        TextField src_phone = new TextField("Numer telefonu:"); 
        binder_client1.forField(src_phone)
            .asRequired("Numer telefonu jest wymagny")
            .withNullRepresentation("")
            .bind(Post_Client::getPhone_nuber, Post_Client::setPhone_nuber);
        TextField src_station = new TextField("Stacja:"); 
        binder_client1.forField(src_station)
            .asRequired("Stacja docelowa jest wymagna")
            .withNullRepresentation("")
            .withValidator(station -> !src_punkt.getValue() || (src_punkt.getValue() && punkt_stations.stream().anyMatch(p -> p.equals(station.toString()))),"Zły punkt przesyłek konduktorskich")
            .bind(Post_Client::getStation, Post_Client::setStation); 
        TextField src_adress = new TextField("Adress:");
        binder_client1.forField(src_adress)
            .asRequired("Adres jest wymagny")
            .withNullRepresentation("")
            .bind(Post_Client::getAdress,Post_Client::setAdress);
            binder_client1.forField(src_punkt).bind(Post_Client::getCollection_order, Post_Client::setCollection_order);
        
        add(info, src_name, src_surname, src_phone, src_station, src_adress, src_punkt);
    }

    private void Check_box()
    {
        overweight.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });

        H3 info = new H3("Sprawdzanie rodzaju paczki");

        cilinder = new Checkbox("Przesyłka rulonowa", false);
        cilinder.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });

        Label box_size = new Label("Wymiary przesyłki:");
        TextField box_width = new TextField("Szerokość (w cm):");
        box_width.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });
        binder_size.forField(box_width)
            .withConverter(new StringToDoubleConverter("Wartość nie jest liczbą"))
            .withNullRepresentation(0.0d)
            .asRequired("Wymagana szerokość paczki (w cm)")
            .withValidator(width -> (!cilinder.getValue() && width < 20.0d) || (cilinder.getValue() && width < 20.0d), "Paczka jest za szeroka")
            .bind(Box_size::getWidth, Box_size::setWidth);  
        TextField box_length = new TextField("Długość (w cm):");
        box_length.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });
        binder_size.forField(box_length)
            .withConverter(new StringToDoubleConverter("Wartość nie jest liczbą"))
            .withNullRepresentation(0.0d)
            .asRequired("Wymagana długość paczki w (cm)")
            .withValidator(length -> (!cilinder.getValue() && length < 40.0d) || (cilinder.getValue() && length < 20.0d), "Paczka jest za długa")
            .bind(Box_size::getLength, Box_size::setLength); 
        TextField box_height = new TextField("Wysokość (w cm):");
        box_height.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });
        binder_size.forField(box_height)
            .withConverter(new StringToDoubleConverter("Wartość nie jest liczbą"))
            .withNullRepresentation(0.0d)
            .asRequired("Wymagana wysokość paczki (w cm)")
            .withValidator(height -> (!cilinder.getValue() && height < 50.0d) || (cilinder.getValue() && height < 120.0d), "Paczka jest za wysoka")
            .bind(Box_size::getHeight, Box_size::setHeight);

        TextField box_weight = new TextField("Waga przesyłki (w kg):");
        box_weight.addValueChangeListener(e -> { ValidateOrder(); CheckPrice(); });
        binder_size.forField(box_weight)
            .withConverter(new StringToDoubleConverter("Wartość nie jest wagą"))
            .withNullRepresentation(0d) 
            .asRequired("Wymagana waga paczki (w kg)")
            .withValidator(weight -> (!overweight.getValue() && weight < 10.0d) || (weight < 20.0d && overweight.getValue()),"Paczka ma niepoprawną wagę")
            .bind(Box_size::getWeight, Box_size::setWeight);

        add(info, cilinder, box_size, box_width, box_length, box_height, overweight, box_weight);
    }

    private void CheckPrice()
    {
        shipping_price = 30.0d;
        if (overweight.getValue())
            shipping_price *= 2.0d;
        if (dest_punkt.getValue())
            shipping_price += 11.0d;
        if (src_punkt.getValue())
            shipping_price += 11.0d;
        total_price.setText("Koszty wysyłki konduktorskiej: " + String.valueOf(shipping_price) + " zł");
    }
}
