package ru.kerminator.sms_transmitter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.kerminator.sms_transmitter.R;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    // Массив SMS, которые следует отобразить в списке
    ArrayList<SentSMS> mMessages = null;

    public MessagesAdapter(ArrayList<SentSMS> listOfData ) {
        mMessages = listOfData;
    }

    // Метод возвращает количество элементов в RecyclerView
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.message_placeholder;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup,
                shouldAttachToParentImmediately);
        MessagesViewHolder viewHolder = new MessagesViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder holder, int position) {
        holder.bind(position);
    }

    // Определяем ViewHolder - он используется для связывания данных и органов
    // управления во View
    class MessagesViewHolder extends RecyclerView.ViewHolder {

        TextView listItemPhoneView;
        TextView listItemMessageView;

        public MessagesViewHolder(View itemView) {
            super(itemView);

            // Находим в указанном View орган управления предназначенный
            // для отображения номера телефона и текста SMS.
            // Эта ссылка будет использоваться для установки текста при связывании.
            // См. bind()
            listItemPhoneView = itemView.findViewById(R.id.tv_item_phone);
            listItemMessageView = itemView.findViewById(R.id.tv_item_message);
        }

        void bind(int index) {

            SentSMS message = mMessages.get(index);
            listItemPhoneView.setText(String.valueOf(message.phone));
            listItemMessageView.setText(String.valueOf(message.message));
        }
    }
}
