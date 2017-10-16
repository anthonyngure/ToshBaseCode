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

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.chatsdk.model.BaseUser;

/**
 * Created by Anthony Ngure on 04/02/2017.
 * Email : anthonyngure25@gmail.com.
 * Company : VibeCampo Social Network..
 */

public class UserConnectedStatusManager implements ValueEventListener {

    public static final String TAG = UserConnectedStatusManager.class.getSimpleName();

    private static HashMap<String, Long> mUserConnectedStatusRefs = new HashMap<>();
    private static List<Long> mOnlineUserList = new ArrayList<>();
    private static UserConnectedStatusManager mInstance;

    private UserConnectedStatusManager() {

    }

    public static UserConnectedStatusManager getInstance(Context ctx){
        if (mInstance == null){
            mUserConnectedStatusRefs = new HashMap<>();
            mOnlineUserList = new ArrayList<>();
            mInstance = new UserConnectedStatusManager();
        }
        return mInstance;
    }

    /**
     * If user is typing the user must be connected
     * @param userId the id of the typing user
     */
    static void onUserTyping(long userId, Context context){
        BeeLog.d(TAG, "onUserTyping, "+userId);
        if (!mOnlineUserList.contains(userId)) {
            mOnlineUserList.add(userId);
        }
        ConversationUtils.onUserConnected(context);
    }

    public static boolean isUserOnline(BaseUser user, Context context) {
        return (mOnlineUserList.contains(user.getId()) && BaseUtils.canConnect(context));
    }

    private String databaseRefToString(DatabaseReference reference){
        return reference.getRef().toString();
    }

    public void add(BaseUser user, Context context){
        DatabaseReference userConnectedStatusRef = FirebaseUtils.getUserConnectedStatusRef(user.getId());
        if (!mUserConnectedStatusRefs.containsKey(databaseRefToString(userConnectedStatusRef))) {
            userConnectedStatusRef.addValueEventListener(this);
            mUserConnectedStatusRefs.put(userConnectedStatusRef.getRef().toString(), user.getId());
            BeeLog.d(TAG, databaseRefToString(userConnectedStatusRef) + " Added to queue");
        } else {
            BeeLog.d(TAG, databaseRefToString(userConnectedStatusRef) + " Already exists");
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        BeeLog.d(TAG, "onDataChange, "+ databaseRefToString(dataSnapshot.getRef()));
        Object val = dataSnapshot.getValue();
        long userId = mUserConnectedStatusRefs.get(databaseRefToString(dataSnapshot.getRef()));
        User user = Database.getInstance().getUserDao().load(userId);
        if ((val != null) && (user != null)) {
            if (val instanceof Long) {
                user.setLastSeen((Long) val);
                Database.getInstance().getUserDao().update(user);
                BeeLog.d(TAG, user.getName() + " is offline.");
                if (mOnlineUserList.contains(user.getId())) {
                    mOnlineUserList.remove(user.getId());
                }
                ConversationUtils.onUserConnected();
            } else if (val instanceof Boolean) {
                BeeLog.d(TAG, user.getName() + " is online.");
                if (!mOnlineUserList.contains(user.getId())) {
                    mOnlineUserList.add(user.getId());
                }
                ConversationUtils.onUserConnected();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}