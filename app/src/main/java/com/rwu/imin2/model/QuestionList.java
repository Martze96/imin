package com.rwu.imin2.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestionList<Question> extends ArrayList implements Serializable {

    private ArrayList<Question> myContent;

    /**
     * Constructors
     */
    public QuestionList() {
        myContent = new ArrayList<Question>();
    }

    public QuestionList(QuestionList<Question> initList) {
        myContent = new ArrayList<Question>(initList);
    }

    public QuestionList(List<Question> initList) {
        myContent = new ArrayList<Question>(initList);
    }


    /**
     * Override List-functions to get/set Items in myContent Attribute
     */
    public ArrayList<Question> getContent() {
        return myContent;
    }

    @Override
    public int size() {
        return myContent.size();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return myContent.contains(o);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return myContent.indexOf(o);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return myContent.toArray();
    }

    @Override
    public Question get(int index) {
        return myContent.get(index);
    }

    @Override
    public Question set(int index, Object element) {
        return myContent.set(index, (Question) element);
    }

    @Override
    public boolean add(Object o) {
        return myContent.add((Question) o);
    }

    @Override
    public void add(int index, Object element) {
        myContent.add(index, (Question) element);
    }

    @Override
    public Question remove(int index) {
        return myContent.remove(index);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return myContent.remove(o);
    }


    @Override
    public boolean addAll(@NonNull Collection c) {
        return myContent.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection c) {
        return myContent.addAll(index, c);
    }

    public String toString() {
        return myContent.toString();
    }

    /**
     *
     * @param q - Question that should be/ shouldn't be contained in the List based on Object Similiarity
     * @return boolean true/false if contains
     */
    public boolean containsQuestionID(Question q) {
        for (Object i : myContent) {
            if (q.equals((Question) i)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param q - Question that should be/ shouldn't be contained in the List based on Title
     * @return boolean true/false if contains
     */
    public boolean containsQuestionTitle(Question q) {
        for (Object i : myContent) {
            if (((com.rwu.imin2.model.Question) q).equalsTitle(((com.rwu.imin2.model.Question) i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes Question in the List based on the similiarity of their title
     * @param q - Question that should be removed by its Title
     */
    public void removeQuestionByTitle(Question q) {

        for (int i = 0; i < myContent.size(); i++) {
            if (((com.rwu.imin2.model.Question) myContent.get(i)).equalsTitle(((com.rwu.imin2.model.Question) q))) {
                myContent.remove(myContent.get(i));
            }
        }
    }


}
