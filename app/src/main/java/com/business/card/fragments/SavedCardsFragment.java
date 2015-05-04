package com.business.card.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.activities.MainActivity;
import com.business.card.activities.ViewCardActivity;
import com.business.card.adapters.SavedBusinessCardAdapter;
import com.business.card.objects.BusinessCard;
import com.business.card.requests.RequestDeleteSavedCard;
import com.business.card.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

public class SavedCardsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View mView;
    private List<BusinessCard> savedCards;
    private ListView savedCardsListView;
    private SavedBusinessCardAdapter adapter;
    private ProgressBar progressBar;
    private TextView noCardsAvailable;
    private boolean refreshList = false;
    private BusinessCard selectedBusinessCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.saved_cards, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        savedCardsListView = (ListView) mView.findViewById(R.id.saved_cards_listview);
        savedCardsListView.setVisibility(View.GONE);

        noCardsAvailable = (TextView) mView.findViewById(R.id.no_cards_available);
        noCardsAvailable.setVisibility(View.GONE);

        if (refreshList) {
            refreshList = false;
            setSavedCards(savedCards);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (((MainActivity) getActivity()).getSavedCards() != null) {
            setSavedCards(((MainActivity) getActivity()).getSavedCards());
        }
    }

    public void setSavedCards(List<BusinessCard> savedCards) {
        if (!isAdded()) {
            this.savedCards = savedCards;
            refreshList = true;
            return;
        }
        progressBar.setVisibility(View.GONE);

        if (savedCards.size() > 0) {
            savedCardsListView.setVisibility(View.VISIBLE);
            noCardsAvailable.setVisibility(View.GONE);
            adapter = new SavedBusinessCardAdapter(getActivity(), savedCards);
            savedCardsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            savedCardsListView.setOnItemClickListener(this);

            registerForContextMenu(savedCardsListView);
        } else {
            savedCardsListView.setVisibility(View.GONE);
            noCardsAvailable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BusinessCard businessCard = adapter.getItem(position);
        BusinessCardApplication.selectedBusinessCard = businessCard;

        // start the view card activity
        Intent intent = new Intent(getActivity(), ViewCardActivity.class);
        intent.putExtra(ViewCardActivity.DISPLAY_EDIT_EXTRA_KEY, false);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedBusinessCard = (BusinessCard) savedCardsListView.getAdapter().getItem(info.position);

        menu.setHeaderTitle(getString(R.string.choose_action_for_card));

        // add context menu items - second parameter is the itemId
        menu.add(0, Util.CONTEXT_MENU_ITEM_SAVED_CARDS_REMOVE, 0, getString(R.string.remove));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Util.CONTEXT_MENU_ITEM_SAVED_CARDS_REMOVE:
                // selected Remove
                displayConfirmRemoveDialog(selectedBusinessCard);
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void displayConfirmRemoveDialog(final BusinessCard businessCard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove_card);
        builder.setMessage(getString(R.string.remove_card_message, businessCard.getTitle(),
                businessCard.getEmail(), businessCard.getPhone(), businessCard.getAddress()));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Yes" button, delete the selected business card
                ((MainActivity) getActivity()).displayProgressDialog();
                RequestDeleteSavedCard requestDeleteSavedCard = new RequestDeleteSavedCard((MainActivity) getActivity(), businessCard);
                requestDeleteSavedCard.execute(new String[]{});
            }
        });

        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }

    public void removeSelectedBusinessCard() {
        // remove the Business Card that the user deleted
        savedCards.remove(selectedBusinessCard);

        adapter = new SavedBusinessCardAdapter(getActivity(), savedCards);
        savedCardsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
