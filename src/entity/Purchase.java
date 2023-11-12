package entity;


public class Purchase {
    private Buyer buyer;
    private Product product;

    public Purchase(Buyer buyer, Product product) {
        this.buyer = buyer;
        this.product = product;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public Product getProduct() {
        return product;
    }
}