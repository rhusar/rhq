/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.enterprise.gui.coregui.client.menu;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Hyperlink;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

import org.rhq.enterprise.gui.coregui.client.CoreGUI;
import org.rhq.enterprise.gui.coregui.client.components.AboutModalWindow;

/**
 * @author Greg Hinkle
 */
public class MenuBarView extends VLayout {

    private AboutModalWindow aboutModalWindow;

    public static final String[] SECTIONS = {"Dashboard", "Demo", "Inventory", "Bundles", "Administration"};

    private String selected = "Dashboard";

    HTMLFlow linksPane;

    public MenuBarView() {
        super(5);
        setHeight(50);
        setWidth100();

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                String first = stringValueChangeEvent.getValue().split("/")[0];

                selected = first;
                linksPane.setContents(setupLinks());
                linksPane.markForRedraw();
            }
        });
    }

    @Override
    protected void onDraw() {
        super.onDraw();


        ToolStrip topStrip = new ToolStrip();
        topStrip.setHeight(34);
        topStrip.setWidth100();
        topStrip.setBackgroundImage("header/header_bg.png");
        topStrip.setMembersMargin(20);


        this.aboutModalWindow = new AboutModalWindow();
        Img logo = new Img("header/rhq_logo_28px.png", 80, 28);
        logo.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
                MenuBarView.this.aboutModalWindow.show();
            }
        });
        topStrip.addMember(logo);


        linksPane = new HTMLFlow();
        linksPane.setContents(setupLinks());

        topStrip.addMember(linksPane);

        topStrip.addMember(new LayoutSpacer());

        topStrip.addMember(new HTMLFlow("Logged in as " + CoreGUI.getSessionSubject().getName()));

        topStrip.addMember(new Hyperlink("Help", "Help"));
        topStrip.addMember(new Hyperlink("Preferences", "Preferences"));
        topStrip.addMember(new Hyperlink("Log Out", "LogOut"));


        /* DynamicForm links = new DynamicForm();
                links.setNumCols(SECTIONS.length * 2);
                links.setHeight100();

                int i = 0;
                FormItem[] linkItems = new FormItem[SECTIONS.length];
                for (String section : SECTIONS) {
                    LinkItem sectionLink = new LinkItem();
                    sectionLink.setTitle(section);
                    sectionLink.setValue("#" + section);
                    sectionLink.setShowTitle(false);

                    if (section.equals("Demo")) {
                        sectionLink.setCellStyle("TopSectionLinkSelected");
        //                sectionLink.("header/header_bg_selected.png");
                    } else {
                        sectionLink.setCellStyle("TopSectionLink");
        //                widgetCanvas.setStyleName("TopSectionLink");
                    }
                    linkItems[i++] = sectionLink;
                }
                links.setItems(linkItems);

                topStrip.addMember(links);
        */
        addMember(topStrip);
        addMember(new SearchBarPane());

        markForRedraw();
    }


    private String setupLinks() {
        StringBuilder headerString = new StringBuilder("<table style=\"height: 34px;\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

        boolean first = true;
        for (String section : SECTIONS) {
            if (first) {
                headerString.append("<td style=\"width: 1px;\"><img src=\"http://localhost:7080/coregui/images/header/header_bg_line.png\"/></td>");
            }
            first = false;

            String bg = "";
            String styleClass = "TopSectionLink";
            if (section.equals(selected)) {
                bg = " background-image: url('http://localhost:7080/coregui/images/header/header_bg_selected.png');";
                styleClass += "Selected";
            }


            headerString.append("<td style=\"vertical-align: bottom; padding:5px; padding-left: 15px; padding-right: 15px;" + bg + "\">");
            headerString.append("<a class=\"" + styleClass + "\" href=\"#" + section + "\">" + section + "</a>");
            headerString.append("</td>\n");

            headerString.append("<td style=\"width: 1px;\"><img src=\"http://localhost:7080/coregui/images/header/header_bg_line.png\"/></td>");
        }

        headerString.append("</tr></table>");

        return headerString.toString();
    }
}
