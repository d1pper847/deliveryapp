public class main {
    public static void main(String[] args) {
        DeliveryManager app = new DeliveryManager();
        app.addCourier(new Courier("Alice (E-Bike)", new Location(1.0, 2.0)));
        app.addCourier(new Courier("Bob (Walking)", new Location(10.0, 10.0)));

        Location cafe = new Location(2.0, 2.0);
        Location customerHouse = new Location(5.0, 5.0);
        Order coffeeOrder = new Order("ORD-001", cafe, customerHouse);
        
        app.addOrder(coffeeOrder);
        System.out.println("--- First Matching Round ---");
        app.matchOrders(); 
    }
}

class Location {
    private double x, y;
    public Location(double x, double y) { this.x = x; this.y = y; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double distanceTo(Location other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}

enum OrderStatus { PENDING, ASSIGNED, DELIVERED }

class Order {
    private String id;
    private Location pickupLocation;
    private Location dropoffLocation;
    private OrderStatus status;

    public Order(String id, Location pickup, Location dropoff) {
        this.id = id;
        this.pickupLocation = pickup;
        this.dropoffLocation = dropoff;
        this.status = OrderStatus.PENDING;
    }
    public String getId() { return id; }
    public Location getPickupLocation() { return pickupLocation; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}

class Courier {
    private String name;
    private Location currentLocation;
    private boolean isAvailable;

    public Courier(String name, Location currentLocation) {
        this.name = name;
        this.currentLocation = currentLocation;
        this.isAvailable = true;
    }
    public String getName() { return name; }
    public Location getCurrentLocation() { return currentLocation; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
}

class DeliveryManager {
    private java.util.List<Order> orders = new java.util.ArrayList<>();
    private java.util.List<Courier> couriers = new java.util.ArrayList<>();

    public void addCourier(Courier courier) { couriers.add(courier); }
    public void addOrder(Order order) { orders.add(order); }

    public void matchOrders() {
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.PENDING) {
                Courier closestCourier = null;
                double minDistance = Double.MAX_VALUE;

                for (Courier courier : couriers) {
                    if (courier.isAvailable()) {
                        double distance = courier.getCurrentLocation().distanceTo(order.getPickupLocation());
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestCourier = courier;
                        }
                    }
                }

                if (closestCourier != null) {
                    order.setStatus(OrderStatus.ASSIGNED);
                    closestCourier.setAvailable(false);
                    System.out.println("Success: Order [" + order.getId() + "] assigned to Courier [" + closestCourier.getName() + "].");
                } else {
                    System.out.println("Notice: Order [" + order.getId() + "] is waiting.");
                }
            }
        }
    }
}