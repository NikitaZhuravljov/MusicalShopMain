package entity;
import java.util.Objects;

public class Buyer {
    private String name;
    private String email;
    private String address;
    private double money;

    public Buyer(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.money = 0.0;
    }

    public Buyer(String name, String email, String address, double money) {
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public double getMoney() {
        return money;
    }

    public void addMoney(double amount) {
        if (amount > 0) {
            money += amount;
        }
    }

    public void deductMoney(double amount) {
        if (amount > 0 && money >= amount) {
            money -= amount;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, address, money);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Buyer buyer = (Buyer) obj;
        return Double.compare(buyer.money, money) == 0 &&
                Objects.equals(name, buyer.name) &&
                Objects.equals(email, buyer.email) &&
                Objects.equals(address, buyer.address);
    }

    @Override
    public String toString() {
        return String.format("%s; %s; %s; $%.2f", name, email, address, money);
    }


}
