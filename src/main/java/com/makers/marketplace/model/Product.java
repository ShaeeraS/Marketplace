package com.makers.marketplace.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.*;
=======
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
>>>>>>> 6bccd5fbda32a7c60c7c2a5dbe89a088478afb61

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EqualsAndHashCode(of = "id") // Ensure equality is based on 'id'
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String category;

    @Min(value = 0, message = "Quantity must be at least 0")
    @Column(nullable = false)
    private Integer quantityAvailable = 1;

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

<<<<<<< HEAD
    @Column(name = "total_quantity_ordered", nullable = false)
    private Integer totalQuantityOrdered = 0;
}
=======

    //Getters and Setters:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}
>>>>>>> 6bccd5fbda32a7c60c7c2a5dbe89a088478afb61
