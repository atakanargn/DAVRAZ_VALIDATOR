package com.telpo.davraz.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.telpo.davraz.model.UserAvatar;

@Database(entities = {UserAvatar.class},version = 1)
public abstract class UserAvatarDatabase extends RoomDatabase {

    public abstract UserAvatar userAvatar();

}
