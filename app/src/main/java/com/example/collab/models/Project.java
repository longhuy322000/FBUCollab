package com.example.collab.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Project")
public class Project extends ParseObject {

    public static final String KEY_PROJECT_ID = "objectId";
    public static final String KEY_PROJECT_NAME = "projectName";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SKILLS = "skills";
    public static final String KEY_CAPACITY = "capacity";
    public static final String KEY_SPOTS = "spots";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_PROJECT_IMAGE = "image";
    public static final String KEY_STATUS = "status";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_CREATED_AT = "createdAt";

    public static final String KEY_STATUS_OPEN = "open";
    public static final String KEY_STATUS_CLOSED = "closed";
    public static final String KEY_PROJECT_POSITION = "projectPosition";
    public static final String KEY_USER_LIKE = "userLike";
    public static final String KEY_LIKES_NUM = "likesNum";

    Boolean liked;
    int likesNum;
    Like userLike;

    public Project() {
    }

    // getters start here

    public String getProjectName() {
        return getString(KEY_PROJECT_NAME);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public List<String> getSkillsList() {
        return getList(KEY_SKILLS);
    }

    public int getCapacity() {
        return getInt(KEY_CAPACITY);
    }

    public int getSpots() {
        return getInt(KEY_SPOTS);
    }

    public String getSpotsStringDisplay(){ return getSpots() + "/" + getCapacity(); }

    public String getDuration() {
        return getString(KEY_DURATION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_PROJECT_IMAGE);
    }

    public String getStatus() {
        return getString(KEY_STATUS);
    }

    public ParseUser getOwner() {
        return (ParseUser) getParseObject(KEY_OWNER);
    }

    public Boolean getLiked() { return liked; }

    public int getLikesNum() { return likesNum; }

    public Like getUserLike() { return userLike; }

    // setters start here

    public void setProjectName(String projectName) {
        put(KEY_PROJECT_NAME, projectName);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setSkills(List<String> skills) {
        List<String> temp = new ArrayList<>();
        temp.addAll(skills);
        put(KEY_SKILLS, temp);
    }

    public void setCapacity(int spots) {
        put(KEY_CAPACITY, spots);
    }

    public void setSpots(int availSpots) {
        put(KEY_SPOTS, availSpots);
    }

    public void setDuration(String timeLength) {
        put(KEY_DURATION, timeLength);
    }

    public void setImage(ParseFile file) {
        put(KEY_PROJECT_IMAGE, file);
    }

    public void setStatus(String status) {
        put(KEY_STATUS, status);
    }

    public void setOwner(ParseUser user) {
        put(KEY_OWNER, user);
    }

    public void setLiked(Boolean liked) { this.liked = liked; }

    public void setLikesNum(int num) { likesNum = num; }

    public void incrementLikesNum() { likesNum += 1; }

    public void decrementLikesNum() { likesNum -= 1; }

    public void setUserLike(Like like) { userLike = like; }
}
