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


package com.fututel.ui.messages;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.fututel.R;
import com.fututel.ui.SipHome;
import com.fututel.ui.SipHome.ViewPagerVisibilityListener;
import com.fututel.ui.dialpad.DialerFragment;
import com.fututel.utils.contacts.ContactsSearchAdapter;
import com.fututel.widgets.CSSListFragment;

/**
 * This activity provides a list view of existing conversations.
 */
public class ConversationsListFragment extends CSSListFragment implements ViewPagerVisibilityListener {


    private ContactsSearchAdapter autoCompleteAdapter;
    private OnAutoCompleteListItemClicked autoCompleteListItemListener;
    View parentview;
    FrameLayout framelayout;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void attachAdapter() {
        framelayout = (FrameLayout) parentview.findViewById(R.id.listContainer);
        framelayout.setVisibility(View.VISIBLE);
        ListView lv = (ListView) framelayout.findViewById(R.id.contactlist);
        autoCompleteAdapter = new ContactsSearchAdapter(getActivity());
        autoCompleteListItemListener = new OnAutoCompleteListItemClicked(autoCompleteAdapter);

        autoCompleteAdapter.setSelectedAccount(-1);
        autoCompleteAdapter.getFilter().filter("");

        lv.setAdapter(autoCompleteAdapter);
        lv.setOnItemClickListener(autoCompleteListItemListener);
        //lv.setFastScrollEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.message_list_fragment, container, false);

        parentview = v;


        return v;
    }

    private class OnAutoCompleteListItemClicked implements AdapterView.OnItemClickListener {
        private ContactsSearchAdapter searchAdapter;

        /**
         * Instanciate with a ContactsSearchAdapter adapter to search in when a
         * contact entry is clicked
         *
         * @param adapter the adapter to use
         */
        public OnAutoCompleteListItemClicked(ContactsSearchAdapter adapter) {
            searchAdapter = adapter;
        }

        @Override
        public void onItemClick(AdapterView<?> list, View v, int position, long id) {
            SipHome.sendFragmentVisibilityChange(3, false);
            SipHome.sendFragmentVisibilityChange(0, true);
            SipHome.mViewPager.setCurrentItem(0, true);
            DialerFragment DialerFrag =(DialerFragment) SipHome.getFragmentAt(0);
            Object selectedItem = searchAdapter.getItem(position);
            if (selectedItem != null) {
                CharSequence newValue = searchAdapter.getFilter().convertResultToString(
                        selectedItem);
                DialerFrag.setTextFieldValue(newValue);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    // Options
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {


        return super.onContextItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //ConversationListItemViews cri = (ConversationListItemViews) v.getTag();
    }



    boolean alreadyLoaded = false;

    @Override
    public void onVisibilityChanged(boolean visible) {

        if(visible) {
            attachAdapter();
            // Start loading
            if(!alreadyLoaded) {
                getLoaderManager().initLoader(0, null, this);
                alreadyLoaded = true;
            }
        }

        if (visible && isResumed()) {

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle args) {
        return null;
    }

    @Override
    public void changeCursor(Cursor c) {

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        // Modify list view




    }

}
