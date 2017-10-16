/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.chatsdk;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.chatsdk.model.BaseUser;

/**
 * Created by Anthony Ngure on 04/02/2017.
 * Email : anthonyngure25@gmail.com.
 * Company : VibeCampo Social Network..
 */

public class UserPresenceStatusManager implements ValueEventListener {

    private static final String TAG = UserPresenceStatusManager.class.getSimpleName();
    private static boolean userPresent;
    private static Context context;
    private static UserPresenceStatusManager mInstance;

    private UserPresenceStatusManager(){

    }

    public static UserPresenceStatusManager getInstance(Context ctx){
        if (mInstance == null){
            mInstance = new UserPresenceStatusManager();
            context = ctx;
        }
        return mInstance;
    }

    public static boolean isUserPresent(Context context) {
        return (userPresent && BaseUtils.canConnect(context));
    }

    public void add(BaseUser user, long chatId){
        DatabaseReference userPresenceRef = FirebaseUtils.getUserPresenceRef(user.getId(), chatId);
        userPresenceRef.addValueEventListener(this);
    }

    public void remove(BaseUser user, long chatId) {
        DatabaseReference userPresenceRef = FirebaseUtils.getUserPresenceRef(user.getId(), chatId);
        userPresenceRef.removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        BeeLog.d(TAG, databaseRefToString(dataSnapshot.getRef()));
        if (dataSnapshot.getValue(Boolean.class) != null) {
            userPresent = dataSnapshot.getValue(Boolean.class);
            ConversationUtils.onUserPresent(context);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private String databaseRefToString(DatabaseReference reference) {
        return reference.getRef().toString();
    }
}