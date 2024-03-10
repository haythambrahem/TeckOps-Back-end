package tn.esprit.se.pispring.entities;

public class TestClass {

    private String name;
    private String email;


    private TestClass(String name, String email) {
        this.name = name;
        this.email = email;
    }


    public void name(String name) {
        this.name = name;
    }

    public void email(String email) {
        this.email = email;
    }

    public TestClass build() {
        return new TestClass(this.name, this.email);
    }

}
