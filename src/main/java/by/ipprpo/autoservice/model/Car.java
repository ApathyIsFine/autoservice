package by.ipprpo.autoservice.model;

/** Автомобиль клиента. Хранит только данные об автомобиле. */
public class Car {

    private final int id;
    private final String plateNumber;
    private final String brand;
    private final String model;
    private final Customer owner;

    public Car(int id, String plateNumber, String brand, String model, Customer owner) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Customer getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return brand + " " + model + " [" + plateNumber + "]";
    }
}
