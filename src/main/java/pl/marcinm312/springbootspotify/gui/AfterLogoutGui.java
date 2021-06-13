package pl.marcinm312.springbootspotify.gui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("log-out")
public class AfterLogoutGui extends VerticalLayout {

	H1 h1;
	Anchor anchor;

	public AfterLogoutGui() {

		h1 = new H1("You have been logged out");
		anchor = new Anchor("..", "Go back to the application");

		add(h1, anchor);
	}
}
