package com.example.model;

import java.time.LocalDate;

/**
 * 药品模型
 */
public class Medicine {
    private String id;
    private String name;
    private String category;
    private String specification;
    private String manufacturer;
    private double price;
    private int stockQuantity;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private String usage;
    private String contraindication;
    private String status;

    public Medicine() {
        this.status = "正常";
    }

    public Medicine(String id, String name, String category, double price, int stockQuantity) {
        this();
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public LocalDate getProductionDate() { return productionDate; }
    public void setProductionDate(LocalDate productionDate) { this.productionDate = productionDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage; }

    public String getContraindication() { return contraindication; }
    public void setContraindication(String contraindication) { this.contraindication = contraindication; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
