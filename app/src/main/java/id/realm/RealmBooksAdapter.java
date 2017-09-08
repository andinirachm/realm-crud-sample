package id.realm;

import android.content.Context;

import io.realm.RealmResults;

/**
 * Created by Andini Rachmah on 9/7/2017.
 */

public class RealmBooksAdapter extends RealmModelAdapter<Book> {

    public RealmBooksAdapter(Context context, RealmResults<Book> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
