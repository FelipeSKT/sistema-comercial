package Rinamed.sistema_comercial;

import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

    @Entity
    public class Produto {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String manufacturer;
        private String mainCategory;
        private String secondCategory;
        private String packaging;

        private Double unitPackSize;
        private BigDecimal packPrice;
        private BigDecimal unitPrice;
        private String description;

        private Integer quantidadeEstoque = 0; // Começa a zero por defeito
        private Integer estoqueMinimo =0;

        public Integer getEstoqueMinimo() {
            return estoqueMinimo;
        }

        public void setEstoqueMinimo(Integer estoqueMinimo) {
            this.estoqueMinimo = estoqueMinimo;
        }

        public Integer getQuantidadeEstoque() {
            return quantidadeEstoque;
        }

        public void setQuantidadeEstoque(Integer quantidadeEstoque) {
            this.quantidadeEstoque = quantidadeEstoque;
        }

        // Getters e Setters
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

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getMainCategory() {
            return mainCategory;
        }

        public void setMainCategory(String mainCategory) {
            this.mainCategory = mainCategory;
        }

        public String getSecondCategory() {
            return secondCategory;
        }

        public void setSecondCategory(String secondCategory) {
            this.secondCategory = secondCategory;
        }

        public String getPackaging() {
            return packaging;
        }

        public void setPackaging(String packaging) {
            this.packaging = packaging;
        }

        public Double getUnitPackSize() {
            return unitPackSize;
        }

        public void setUnitPackSize(Double unitPackSize) {
            this.unitPackSize = unitPackSize;
        }

        public BigDecimal getPackPrice() {
            return packPrice;
        }

        public void setPackPrice(BigDecimal packPrice) {
            this.packPrice = packPrice;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

