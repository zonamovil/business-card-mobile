package com.business.card.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.business.card.R;
import com.business.card.activities.ConferenceCardsActivity;
import com.business.card.objects.BusinessCard;

import java.util.List;

public class ConferenceBusinessCardAdapter extends BaseAdapter {

    private ConferenceCardsActivity activity;
    private List<BusinessCard> businessCards;

    public ConferenceBusinessCardAdapter(ConferenceCardsActivity activity, List<BusinessCard> businessCards) {
        this.activity = activity;
        this.businessCards = businessCards;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getCount() {
        return businessCards.size();
    }

    public BusinessCard getItem(int position) {
        return businessCards.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BusinessCard businessCard = getItem(position);
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.conference_business_card_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.email = (TextView) rowView.findViewById(R.id.email);
            viewHolder.phone = (TextView) rowView.findViewById(R.id.phone);
            viewHolder.address = (TextView) rowView.findViewById(R.id.address);
            viewHolder.saveCardButton = (Button) rowView.findViewById(R.id.save_card_button);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.title.setText(businessCard.getTitle());
        viewHolder.name.setText(businessCard.getFirstName() + " " + businessCard.getLastName());

        if (businessCard.getEmail().equals("")) {
            viewHolder.email.setVisibility(View.GONE);
        } else {
            viewHolder.email.setVisibility(View.VISIBLE);
            viewHolder.email.setText(businessCard.getEmail());
        }

        if (businessCard.getAddress().equals("")) {
            viewHolder.address.setVisibility(View.GONE);
        } else {
            viewHolder.address.setVisibility(View.VISIBLE);
            viewHolder.address.setText(businessCard.getAddress());
        }

        viewHolder.phone.setText(businessCard.getPhone());
        viewHolder.saveCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "Save Card" button was pressed for this card
                activity.requestConferenceCard(businessCard);
            }
        });

        return rowView;
    }

    static class ViewHolder {
        private TextView name;
        private TextView title;
        private TextView email;
        private TextView phone;
        private TextView address;
        private Button saveCardButton;
    }
}
