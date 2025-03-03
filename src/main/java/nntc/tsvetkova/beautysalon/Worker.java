package nntc.tsvetkova.beautysalon;

public class Worker {
    private int id;
    private String name;
    private String specialization;


    public Worker(int id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return specialization;
    }
}
