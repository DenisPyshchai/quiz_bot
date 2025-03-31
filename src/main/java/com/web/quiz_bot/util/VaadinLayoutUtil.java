package com.web.quiz_bot.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.web.quiz_bot.vaadin.component.CustomTab;

public class VaadinLayoutUtil {

    private VaadinLayoutUtil() {}

    public static Tab createTab(VaadinIcon viewIcon, String viewName,
                                Class<? extends Component> route, Tabs tabs) {
        Icon icon = viewIcon.create();
        icon.getStyle()
                .set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        CustomTab tab = new CustomTab();
        link.add(icon, new Span(viewName));
        link.setRoute(route);
        link.setHighlightCondition(HighlightConditions.sameLocation());
        link.setHighlightAction((routerLink, shouldHighlight) -> {
            if (shouldHighlight) tabs.setSelectedTab(tab);
        });
        link.setTabIndex(-1);
        tab.add(link);
        return tab;
    }
}
