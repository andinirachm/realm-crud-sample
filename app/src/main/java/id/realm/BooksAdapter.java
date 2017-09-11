package id.realm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import io.realm.Realm;

/**
 * Created by Andini Rachmah on 9/7/2017.
 */

public class BooksAdapter extends RealmRecyclerViewAdapter<Book> {
    Context context;
    private Realm realm;
    private LayoutInflater inflater;

    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;

    public BooksAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BookHolder holder1 = (BookHolder) holder;
        realm = RealmController.getInstance().getRealm();
        Book book = getItem(position);

        holder1.textViewTitle.setText(book.getBookTitle());
        holder1.textViewAuthor.setText(book.getAuthor());
        holder1.itemView.setOnClickListener(new OnItemClick(book, position));
        holder1.itemView.setOnLongClickListener(new OnItemLongClick(book, position));

        Glide.with(context)
                .load(book.getImageUrl())
                .into(holder1.imageView);
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(Book data, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Book data, int position);
    }

    public void setOnItemLongClickListenerListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    public void setOnItemClickListenerListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    private class OnItemClick implements View.OnClickListener {

        private Book book;
        private int position;

        public OnItemClick(Book book, int position) {
            this.book = book;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null && getRealmAdapter() != null) {
                onItemClickListener.onItemClick(book, position);
            }
        }
    }

    private class OnItemLongClick implements View.OnLongClickListener {

        private Book book;
        private int position;

        public OnItemLongClick(Book book, int position) {
            this.book = book;
            this.position = position;
        }


        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(book, position);
            }
            return false;
        }
    }
}
