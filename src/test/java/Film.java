/**
 * Created by tingx on 2016/12/22.
 */
public class Film {
    private String id;
    private String title;
    private String desc;

    public Film() {

    }

    public Film(String id, String title, String desc) {
        this.id = id;
        this.title = title;
        this.desc = desc;
    }

    public Film(String str) {
        String[] segs = str.trim().split("\\|");
        this.id = segs[0].trim();
        this.title = segs[1];
        this.desc = segs[2];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String toString() {
        return id + "|" + title + "|" + desc;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Film that = (Film) o;
        return that.getId() == this.getId();
    }

    public int hashCode() {
        return getId().hashCode();
    }
}