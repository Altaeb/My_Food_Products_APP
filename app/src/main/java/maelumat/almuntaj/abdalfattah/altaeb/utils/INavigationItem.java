package maelumat.almuntaj.abdalfattah.altaeb.utils;

/**
 * Created by Lobster on 06.03.18.
 */

import maelumat.almuntaj.abdalfattah.altaeb.utils.NavigationDrawerListener.NavigationDrawerType;

public interface INavigationItem {

    NavigationDrawerListener getNavigationDrawerListener();

    @NavigationDrawerType
    int getNavigationDrawerType();

}
