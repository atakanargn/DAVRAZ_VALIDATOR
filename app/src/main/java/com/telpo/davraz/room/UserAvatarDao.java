package com.telpo.davraz.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.telpo.davraz.model.UserAvatar;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface UserAvatarDao {

    @Query("SELECT * FROM UserAvatar")
    Flowable<List<UserAvatar>> getAll();

    @Insert
    Completable insert(UserAvatar userAvatar);

    @Delete
    Completable delete(UserAvatar userAvatar);

    @Update
    Completable update(UserAvatar userAvatar);

}
