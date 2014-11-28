/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of Fututel.
 *
 *  Fututel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  Fututel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Fututel.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.fututel.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fututel.api.SipManager;
import com.fututel.ui.SipHome;
import com.fututel.ui.SipHome.ViewPagerVisibilityListener;
import com.fututel.ui.account.AccountsEditList;

import android.widget.RelativeLayout;
import com.actionbarsherlock.app.SherlockFragment;
import com.fututel.R;
import com.fututel.utils.PreferencesWrapper;

public class FavListFragment extends SherlockFragment implements ViewPagerVisibilityListener {

    private RelativeLayout account, setting, logout;
    private SipHome siphome = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.settings, container, false);
        account = (RelativeLayout) v.findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              startActivity(new Intent(siphome, AccountsEditList.class));
            }
        });

        setting = (RelativeLayout) v.findViewById(R.id.settings);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SipManager.ACTION_UI_PREFS_GLOBAL), 1);
            }
        });

        logout = (RelativeLayout) v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                siphome.prefProviderWrapper.setPreferenceBooleanValue(PreferencesWrapper.HAS_BEEN_QUIT, true);
                siphome.disconnect(true);//rangdong
            }
        });

        return v;
    }


    public void setParent(SipHome Home)
    {
        siphome = Home;
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
    }

}

