package pl.marcinm312.springbootspotify.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AfterLogoutGuiTest {

	@Test
	void afterLogoutGuiTest_simpleCase_success() {

		AfterLogoutGui afterLogoutGui = new AfterLogoutGui();
		long receivedChildrenSize = afterLogoutGui.getChildren().count();
		Assertions.assertEquals(2, receivedChildrenSize);
	}

}