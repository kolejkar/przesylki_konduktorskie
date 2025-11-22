package karol.przesylki.konduktorskie_przesylki.front.menu;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

import karol.przesylki.konduktorskie_przesylki.UserDetails.ConductorDetails;
import karol.przesylki.konduktorskie_przesylki.repository.ConductorRepository;
import karol.przesylki.konduktorskie_przesylki.tables.Conductor;

import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class MenuGUI extends HorizontalLayout implements AfterNavigationObserver {

	private ConductorRepository conductorRepo;
		
	public MenuGUI(ConductorRepository conductorRepo) 
	{
		this.setJustifyContentMode(JustifyContentMode.CENTER);
		this.setWidthFull();
		
		this.conductorRepo = conductorRepo;
	}
	
	private String getConductorName()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken))
		{
			ConductorDetails conductorDetails = (ConductorDetails) authentication.getPrincipal();
			return conductorDetails.getUsername();
		}
		return "";
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
		{
			Label user = new Label("Panel administracyjny"); 
			Button logout = new Button("Wyloguj");
			
			logout.addClickListener(clickEvent -> {
				UI.getCurrent().getPage().setLocation("logout");
			});
			
			add(user, logout);
			expand(user);
		}
		else
		if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CONDUKTOR")))
		{
			String indeficator = getConductorName();
			Conductor conductor = conductorRepo.findByConductorIndeficator(indeficator).get();
			Label user = new Label("Panel " + conductor.getName()); 
			Button logout = new Button("Wyloguj");
			
			logout.addClickListener(clickEvent -> {
				UI.getCurrent().getPage().setLocation("logout");
			});
			
			add(user, logout);
			expand(user);
		}
		else
		{
			Button login = new Button("Zaloguj");
			
			login.addClickListener(clickEvent -> {
				UI.getCurrent().getPage().setLocation("login");
			});
			
			Label info = new Label("");			
			add(info, login);
			expand(info);
		}
	}
}
