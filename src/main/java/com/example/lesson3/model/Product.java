package com.example.lesson3.model;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    private String name;

    @NotNull(message = "Giá sản phẩm là bắt buộc")
    @DecimalMin(value = "0", message = "Giá phải lớn hơn hoặc bằng 0")
    @Column(precision = 19, scale = 2)
    private BigDecimal price;

    private String description;
    
    @Transient
    private MultipartFile imageFile;

    public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	private String image;

    private int status;
    
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getStatusName() {
	    switch (this.status) {
	        case 1:
	            return "Còn hàng";
	        case 2:
	            return "Hết hàng";
	        default:
	            return "UNKNOWN";
	    }
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getDisplayPrice() {
	    if (price == null) return "";
	    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
	    formatter.setMaximumFractionDigits(0);
	    return formatter.format(price);
	}

	
	public Store getStore() {
	    return store;
	}

	public void setStore(Store store) {
	    this.store = store;
	}
	
	public void setStoreId(Long storeId) {
	    if (this.store == null) {
	        this.store = new Store();
	    }
	    this.store.setId(storeId);
	}

    public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
