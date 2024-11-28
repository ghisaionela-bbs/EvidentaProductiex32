package ro.brutariabaiasprie.evidentaproductie.Domain;

import java.util.Objects;

public class Group {
    private int id;
    private String name;
    private int parentGroupId;

    public Group() {
        this.id = 0;
        this.name = "";
        this.parentGroupId = 0;
    }

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
        this.parentGroupId = 0;
    }

    public Group(int id, String name, int parentGroupId) {
        this.id = id;
        this.name = name;
        this.parentGroupId = parentGroupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(int parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && parentGroupId == group.parentGroupId && Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parentGroupId);
    }
}
