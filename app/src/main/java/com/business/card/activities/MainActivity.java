package com.business.card.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.fragments.ConferencesFragment;
import com.business.card.fragments.MyCardsFragment;
import com.business.card.fragments.SavedCardsFragment;
import com.business.card.objects.BusinessCard;
import com.business.card.requests.RequestMyCards;
import com.business.card.requests.RequestSavedCards;
import com.business.card.util.LocationBroadcastReceiver;
import com.business.card.util.PreferenceHelper;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;

    private MenuItem addMenuItem;
    private boolean displayAddMenuItem = false;

    private List<BusinessCard> savedCards;
    private List<BusinessCard> myCards;

    private int currentPage;
    private ProgressDialog progressDialog;

    private LocationBroadcastReceiver lftBroadcastReceiver;

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            if (position == 0) {
                if (addMenuItem != null) {
                    addMenuItem.setVisible(false);
                } else {
                    displayAddMenuItem = false;
                }
            } else if (position == 1) {
                if (addMenuItem != null) {
                    addMenuItem.setVisible(true);
                } else {
                    displayAddMenuItem = true;
                }
            } else if (position == 2) {
                if (addMenuItem != null) {
                    addMenuItem.setVisible(true);
                } else {
                    displayAddMenuItem = true;
                }
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setOnPageChangeListener(pageChangeListener);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RequestSavedCards requestSavedCards = new RequestSavedCards(this, BusinessCardApplication.loggedUser);
        requestSavedCards.execute(new String[]{});

        RequestMyCards requestMyCards = new RequestMyCards(this, BusinessCardApplication.loggedUser);
        requestMyCards.execute(new String[]{});

        final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        lftBroadcastReceiver = new LocationBroadcastReceiver();
        registerReceiver(lftBroadcastReceiver, lftIntentFilter);

        // force a location update
        LocationLibrary.forceLocationUpdate(this);
    }

    public void displayProgressDialog() {
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        addMenuItem = menu.getItem(0);
        if (displayAddMenuItem) {
            addMenuItem.setVisible(true);
        } else {
            addMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                if (currentPage == 1) {
                    // Clear any Business Card selected
                    BusinessCardApplication.selectedBusinessCard = null;
                    // start the edit card activity
                    Intent intent = new Intent(this, AddEditCardActivity.class);
                    this.startActivity(intent);
                }
                break;
            case R.id.action_logout:
                // remove the previously saved user
                PreferenceHelper.deletSavedUser(this);

                // start the initial activity, clearing any other activities previously opened
                Intent intent = new Intent(this, NotLoggedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<BusinessCard> getSavedCards() {
        return savedCards;
    }

    public List<BusinessCard> getMyCards() {
        return myCards;
    }

    /**
     * Finished request for Saved Cards
     */
    public void onSavedCardsRequestFinished(JSONArray j) {
        savedCards = new ArrayList<BusinessCard>();
        List<BusinessCard> businessCards = new ArrayList<BusinessCard>();
        for (int i = 0; i < j.length(); i++) {
            try {
                BusinessCard businessCard = BusinessCard.parseBusinessCardFromJson(j.getJSONObject(i));
                businessCards.add(businessCard);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        savedCards = businessCards;
        ((SavedCardsFragment) pagerAdapter.getItem(0)).setSavedCards(businessCards);
    }

    /**
     * Finished request for My Cards
     */
    public void onMyCardsRequestFinished(JSONArray j) {
        myCards = new ArrayList<BusinessCard>();
        List<BusinessCard> businessCards = new ArrayList<BusinessCard>();
        for (int i = 0; i < j.length(); i++) {
            try {
                BusinessCard businessCard = BusinessCard.parseBusinessCardFromJson(j.getJSONObject(i));
                businessCards.add(businessCard);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        myCards = businessCards;
        ((MyCardsFragment) pagerAdapter.getItem(1)).setMyCards(businessCards);
    }

    /**
     * Finished request for My Card Delete
     */
    public void onMyCardDeleteRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card deleted
                Toast.makeText(this, getString(R.string.card_delete_success), Toast.LENGTH_SHORT).show();

                ((MyCardsFragment) pagerAdapter.getItem(1)).removeSelectedBusinessCard();
            } else {
                // card not deleted
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private SavedCardsFragment savedCardsFragment;
        private MyCardsFragment myCardsFragment;
        private ConferencesFragment conferencesFragment;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    if (savedCardsFragment == null) {
                        savedCardsFragment = new SavedCardsFragment();
                    }
                    return savedCardsFragment;
                case 1:
                    if (myCardsFragment == null) {
                        myCardsFragment = new MyCardsFragment();
                    }
                    return myCardsFragment;
                case 2:
                    if (conferencesFragment == null) {
                        conferencesFragment = new ConferencesFragment();
                    }
                    return conferencesFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.saved_cards);
                case 1:
                    return getString(R.string.my_cards);
                case 2:
                    return getString(R.string.conferences);
                default:
                    return null;
            }
        }
    }
}