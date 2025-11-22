package karol.przesylki.konduktorskie_przesylki.front;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.Route;

import karol.przesylki.konduktorskie_przesylki.front.menu.MenuGUI;
import karol.przesylki.konduktorskie_przesylki.repository.ConductorRepository;
import karol.przesylki.konduktorskie_przesylki.tables.Conductor;
import karol.przesylki.konduktorskie_przesylki.tables.ERole;
import karol.przesylki.konduktorskie_przesylki.tables.Role;

@Route("/admin")
public class AdminUI extends VerticalLayout {

    private Binder<Conductor> binder_conductor;

    private Grid<Conductor> conductorGrid;
    private Button deleteConductor;

    AdminUI(ConductorRepository conductorRepo)
    {
        binder_conductor = new BeanValidationBinder<>(Conductor.class);
        Conductor conductor = new Conductor();
        binder_conductor.setBean(conductor);
        Hearder(conductorRepo);
        AddConductor(conductorRepo);
        ShowConductors(conductorRepo);
    }

    private void Hearder(ConductorRepository conductorRepo)
    {
        MenuGUI menu = new MenuGUI(conductorRepo);
		add(menu);
    }

    /*private void ValidateConductor()
    {
        binder_conductor.validate();
    }*/

    private void AddConductor(ConductorRepository conductorRepo)
    {
        TextField conductor_id = new TextField("Numer indefikatora konduktora:");
        binder_conductor.forField(conductor_id)
            .asRequired("Musi być podany numer indefikatora konduktora")
            .bind(Conductor::getConductorIndeficator, Conductor::setConductorIndeficator);

        TextField conductor_name = new TextField("Nazwa konduktora:");
        binder_conductor.forField(conductor_name)
            .asRequired("Musi być podana nazwa konduktora")
            .bind(Conductor::getName, Conductor::setName);

        TextField conductor_password = new TextField("Hasło konduktora:");
        binder_conductor.forField(conductor_password)
            .asRequired("Musi być podane hasło")
            .withValidator(cond -> cond.length() < 64 && cond.length() > 8,"Hasło musi mieć między 8 a 64 znaki")
            .bind(Conductor::getPassword, Conductor::setPassword);

        Button save_conductor = new Button("Dodaj");
        save_conductor.addClickListener(l -> {
            //ValidateConductor();
            if(binder_conductor.validate().isOk())
            {
                Role role = new Role();
			    role.setName(ERole.ROLE_CONDUKTOR);
			
			    Set<Role> roles =new HashSet();
			    roles.add(role);

                Conductor conductor= new Conductor();
                conductor.setConductorIndeficator(conductor_id.getValue());
                conductor.setName(conductor_name.getValue());
                conductor.setPassword(conductor_password.getValue());
                conductor.setRole(roles);
                conductorRepo.save(conductor);
                Notification.show("Dodano konduktora");
            }
        });
        add(conductor_id, conductor_name, conductor_password, save_conductor);
    }

    private void RemoveConductor(ConductorRepository conductorRepo, Conductor conductor)
    {
			conductorRepo.deleteById(conductor.getId());
            Notification.show("Usunięto konduktora");
    }

    private void ShowConductors(ConductorRepository conductorRepo)
    {
        deleteConductor = new Button("Usuwanie konduktora");

        conductorGrid= new Grid<Conductor>(Conductor.class);
        List<Conductor> conductors = conductorRepo.findAll();		
		conductorGrid.setItems(conductors);
        SingleSelect<Grid<Conductor>, Conductor> conductorSelect =
		        conductorGrid.asSingleSelect();
        deleteConductor.addClickListener(clickEvent -> {
			RemoveConductor(conductorRepo, conductorSelect.getValue());
		});

        add(conductorGrid, deleteConductor);
    }
}
