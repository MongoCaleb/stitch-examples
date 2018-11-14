package com.mongodb.stitch.sdk.examples.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;
import java.util.List;


public class TodoListAdapter extends ArrayAdapter<TodoItem> {

    private final RemoteMongoCollection _itemSource;

    public TodoListAdapter(
            final Context context,
            final int resource,
            final List<TodoItem> items,
            final RemoteMongoCollection itemSource
    ) {
        super(context, resource, items);
        _itemSource = itemSource;
    }

    @NonNull
    @Override
    public View getView(
            final int position,
            final View convertView,
            @NonNull final ViewGroup ignored){

        final View row;
        if(convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.todo_item, null);
        } else  {
            // Reuse past view
            row = convertView;
        }

        final TodoItem item = this.getItem(position);

        // Hydrate data/event handlers
        ((TextView) row.findViewById(R.id.text)).setText(item.getText());

        final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            final Document query = new Document();
            query.put("_id", item.getId());

            final Document update = new Document();
            final Document set = new Document();
            set.put("checked", b);
            update.put("$set", set);

            _itemSource.sync().updateOne(query, update);
        });
        return row;
    }
}
