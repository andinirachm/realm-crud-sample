package id.realm;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andini Rachmah on 9/7/2017.
 */

public class BookHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image_view)
    public ImageView imageView;
    @BindView(R.id.text_view_title)
    public AppCompatTextView textViewTitle;
    @BindView(R.id.text_view_author)
    public AppCompatTextView textViewAuthor;

    public BookHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
