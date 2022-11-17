package org.acme;


import java.util.*;

public class HeavyObjectDTO  {
    private String id;
    private String name;
    private int age;
    private Acl acl = new Acl();
    private Map<String, Map<String, Object>> metaData = new HashMap<>();

    public HeavyObjectDTO() {
        this.id = UUID.randomUUID().toString();
        this.acl.admin = List.of(id);
    }

    public HeavyObjectDTO(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acl.admin = List.of(id);
    }

    public void addFriend(String id) {
        this.acl.getRead().add(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Acl getAcl() {
        return acl;
    }

    public void setAcl(Acl acl) {
        this.acl = acl;
    }

    public Map<String, Map<String, Object>> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Map<String, Object>> metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeavyObjectDTO that = (HeavyObjectDTO) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // store list of UIs
    public class Acl {
        private List<String> read = new ArrayList<>();
        private List<String> write = new ArrayList<>();
        private List<String> admin  = new ArrayList<>();

        public List<String> getRead() {
            return read;
        }

        public void setRead(List<String> read) {
            this.read = read;
        }

        public List<String> getWrite() {
            return write;
        }

        public void setWrite(List<String> write) {
            this.write = write;
        }

        public List<String> getAdmin() {
            return admin;
        }

        public void setAdmin(List<String> admin) {
            this.admin = admin;
        }
    }
}
