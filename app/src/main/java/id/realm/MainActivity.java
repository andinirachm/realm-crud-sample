package id.realm;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements BooksAdapter.OnItemClickListener, BooksAdapter.OnItemLongClickListener {

    @BindView(R.id.recycler_view_book)
    RecyclerView recyclerView;
    @BindView(R.id.linear_layout_empty)
    LinearLayout linearLayoutEmpty;


    TextInputEditText editTextTitle;
    TextInputEditText editTextAuthor;
    TextInputEditText editTextImage;

    Realm realm;
    BooksAdapter adapter;

    List<Book> bookList = new ArrayList<>();


    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRecyclerView();

        this.realm = RealmController.with(this).getRealm();

        if (!Prefs.with(this).getPreLoad()) {
            setData();
        }

        RealmController.with(this).refresh();
        setRealmAdapter(RealmController.with(this).getBooks());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create_playlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_add) {
            showDialogCreate();
        }
        return true;
    }

    public void setRealmAdapter(RealmResults<Book> books) {
        RealmBooksAdapter realmAdapter = new RealmBooksAdapter(this.getApplicationContext(), books, true);
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
        //showEmptyState();
    }

    private void setRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BooksAdapter(MainActivity.this);
        adapter.setOnItemClickListenerListener(this);
        adapter.setOnItemLongClickListenerListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setData() {
        Book book = new Book();
        book.setBookId(1 + System.currentTimeMillis());
        book.setAuthor("Roger S. Pressman");
        book.setBookTitle("Software Development");
        bookList.add(book);

        Book book2 = new Book();
        book2.setBookId(2 + System.currentTimeMillis());
        book2.setAuthor("Roger S. Pressman");
        book2.setBookTitle("Software Development Life Cycle");
        bookList.add(book2);

        for (Book b : bookList) {
            realm.beginTransaction();
            realm.copyToRealm(b);
            realm.commitTransaction();
        }

        Prefs.with(this).setPreLoad(true);
    }

    private void showDialogEdit(Book book, final int position) {
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_edit_book, null);

        editTextTitle = (TextInputEditText) content.findViewById(R.id.edit_text_view_title);
        editTextAuthor = (TextInputEditText) content.findViewById(R.id.edit_text_view_author);
        editTextImage = (TextInputEditText) content.findViewById(R.id.edit_text_view_image);

        editTextTitle.setText(book.getBookTitle());
        editTextAuthor.setText(book.getAuthor());
        editTextImage.setText(book.getImageUrl());

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(content)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Book> results = realm.where(Book.class).findAll();

                        realm.beginTransaction();
                        results.get(position).setAuthor(editTextAuthor.getText().toString());
                        results.get(position).setBookTitle(editTextTitle.getText().toString());
                        results.get(position).setImageUrl(editTextImage.getText().toString());

                        realm.commitTransaction();

                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        //showEmptyState();
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogCreate() {
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_edit_book, null);

        editTextTitle = (TextInputEditText) content.findViewById(R.id.edit_text_view_title);
        editTextAuthor = (TextInputEditText) content.findViewById(R.id.edit_text_view_author);
        editTextImage = (TextInputEditText) content.findViewById(R.id.edit_text_view_image);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(content)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Book book = new Book();
                        book.setBookId(RealmController.getInstance().getBooks().size() + System.currentTimeMillis());
                        book.setBookTitle(editTextTitle.getText().toString());
                        book.setAuthor(editTextAuthor.getText().toString());
                        book.setImageUrl(editTextImage.getText().toString());

                        if (editTextTitle.getText() == null || editTextTitle.getText().toString().equals("") || editTextTitle.getText().toString().equals(" ")) {
                            Toast.makeText(MainActivity.this, "Entry not saved, missing title", Toast.LENGTH_SHORT).show();
                        } else {
                            realm.beginTransaction();
                            realm.copyToRealm(book);
                            realm.commitTransaction();

                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(RealmController.getInstance().getBooks().size() - 1);
                        }
                        //showEmptyState();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemClick(Book data, int position) {
        RealmResults<Book> results = realm.where(Book.class).findAll();
        if (results != null || results.size() != 0 || data != null)
            showDialogEdit(data, position);
    }

    @Override
    public void onItemLongClick(Book data, int position) {
        RealmResults<Book> results = realm.where(Book.class).findAll();
        realm.beginTransaction();
        results.remove(position);
        realm.commitTransaction();
        if (results.size() == 0) {
            //Prefs.with(this).setPreLoad(false);
        }

        adapter.notifyDataSetChanged();
        //showEmptyState();
    }

    private void showEmptyState() {
        RealmResults<Book> results = RealmController.with(this).getBooks();
        if (results.size() == 0 || results == null) {
            linearLayoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            linearLayoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
