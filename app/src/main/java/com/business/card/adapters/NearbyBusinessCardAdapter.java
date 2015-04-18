package com.business.card.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.business.card.R;
import com.business.card.activities.NearbyCardsActivity;
import com.business.card.objects.BusinessCard;

import java.util.List;

public class NearbyBusinessCardAdapter extends BaseAdapter {

    private NearbyCardsActivity activity;
    private List<BusinessCard> businessCards;

    public NearbyBusinessCardAdapter(NearbyCardsActivity activity, List<BusinessCard> businessCards) {
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

            rowView = inflater.inflate(R.layout.nearby_business_card_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.distance = (TextView) rowView.findViewById(R.id.distance);
            viewHolder.getCardButton = (Button) rowView.findViewById(R.id.get_card_button);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.title.setText(businessCard.getTitle());
        viewHolder.name.setText(businessCard.getFirstName() + " " + businessCard.getLastName());
        viewHolder.distance.setText(activity.getString(R.string.distance_meters, businessCard.getDistance()));
        viewHolder.getCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "Get Card" button was pressed for this card
                activity.requestPublicCard(businessCard);
            }
        });

        return rowView;
    }

    static class ViewHolder {
        private TextView name;
        private TextView title;
        private TextView distance;
        private Button getCardButton;
    }
}