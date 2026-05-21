public class main {
    public static void main(String[] args) {
        DeliveryManager app = new DeliveryManager();

        app.addCourier(new Courier("Alice (E-Bike)", new Location(1.0, 2.0)));

        Location cafe = new Location(2.0, 2.0);
        Location customerHouse = new Location(7.0, 2.0); // 5 units away
        Order coffeeOrder = new Order("ORD-001", cafe, customerHouse);
        app.addOrder(coffeeOrder);

        System.out.println("--- System Status ---");
        System.out.printf("Current Surge Multiplier: %.1fx\n\n", app.calculateSurgeMultiplier());

        System.out.println("--- Executing Matching & Pricing ---");
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
    private double finalPrice;

    public Order(String id, Location pickup, Location dropoff) {
        this.id = id;
        this.pickupLocation = pickup;
        this.dropoffLocation = dropoff;
        this.status = OrderStatus.PENDING;
        this.finalPrice = 0.0;
    }
    
    public String getId() { return id; }
    public Location getPickupLocation() { return pickupLocation; }
    public Location getDropoffLocation() { return dropoffLocation; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
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
    
    private final double BASE_RATE_PER_MILE = 1.50;
    public void addCourier(Courier courier) { couriers.add(courier); }
    public void addOrder(Order order) { orders.add(order); }

    public double calculateSurgeMultiplier() {
        long availableCouriers = couriers.stream().filter(Courier::isAvailable).count();
        long pendingOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();

        if (pendingOrders > availableCouriers && availableCouriers > 0) {
            return 1.8;
        } else if (availableCouriers == 0) {
            return 2.5;
        }
        return 1.0;
    }

    public void matchOrders() {
        double surge = calculateSurgeMultiplier();

        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.PENDING) {
                
                double travelDistance = order.getPickupLocation().distanceTo(order.getDropoffLocation());
                double price = travelDistance * BASE_RATE_PER_MILE * surge;
                order.setFinalPrice(price);

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
                    System.out.printf("Delivery Distance: %.2f miles | Total Price: $%.2f (Includes Surge)\n", travelDistance, order.getFinalPrice());
                } else {
                    System.out.println("Notice: Order [" + order.getId() + "] is waiting. No available couriers nearby.");
                    System.out.printf("Estimated Quote: $%.2f\n", order.getFinalPrice());
                }
            }
        }
    }
}