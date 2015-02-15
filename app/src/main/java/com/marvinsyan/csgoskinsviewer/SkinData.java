package com.marvinsyan.csgoskinsviewer;

/**
 * Created by Marvin on 2/12/2015.
 */
public class SkinData {
    public int imageId;
    public int gunNameId;
    public int skinNameId;
    public int rarityId;
    public int collectionId;
    public int specialId;
    public int titleId;

    public SkinData(int imageId, int gunNameId, int skinNameId, int rarityId, int collectionId, int specialId) {
        this.imageId = imageId;
        this.gunNameId = gunNameId;
        this.skinNameId = skinNameId;
        this.rarityId = rarityId;
        this.collectionId = collectionId;
        this.specialId = specialId;
    }

    // For TypeActivity
    // Should display the collection or weapon name where the skin name usually appears
    public SkinData(int imageId, int weaponOrCollectionNameId) {
        this.imageId = imageId;
        this.titleId = weaponOrCollectionNameId;
    }
}
