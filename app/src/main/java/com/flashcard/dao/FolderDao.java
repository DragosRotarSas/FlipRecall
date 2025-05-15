package com.flashcard.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.flashcard.model.Folder;

import java.util.List;

@Dao
public interface FolderDao {
    @Insert
    long insert(Folder folder);
    
    @Update
    void update(Folder folder);
    
    @Delete
    void delete(Folder folder);
    
    @Query("SELECT * FROM folders WHERE parentFolderId = :parentId")
    LiveData<List<Folder>> getFoldersByParentId(long parentId);
    
    @Query("SELECT * FROM folders WHERE id = :id")
    LiveData<Folder> getFolderById(long id);
    
    @Query("SELECT * FROM folders WHERE parentFolderId = 0")
    LiveData<List<Folder>> getRootFolders();

    @Query("SELECT * FROM folders WHERE parentFolderId = :parentId")
    List<Folder> getFoldersByParentIdSync(long parentId);

    @Query("DELETE FROM folders WHERE id = :folderId")
    void deleteFolderById(long folderId);
} 