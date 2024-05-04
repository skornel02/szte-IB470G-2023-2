package hu.notetaker.models;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class NoteModel {

    private String user;

    private String title;

    private String content;

    private Timestamp created;

    private Timestamp alertAt;

    private boolean finished;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getAlertAt() {
        return alertAt;
    }

    public void setAlertAt(Timestamp alertAt) {
        this.alertAt = alertAt;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteModel noteModel = (NoteModel) o;
        return finished == noteModel.finished && Objects.equals(user, noteModel.user) && Objects.equals(title, noteModel.title) && Objects.equals(content, noteModel.content) && Objects.equals(created, noteModel.created) && Objects.equals(alertAt, noteModel.alertAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, title, content, created, alertAt, finished);
    }
}
