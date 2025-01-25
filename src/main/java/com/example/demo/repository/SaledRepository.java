package com.example.demo.repository;

import com.example.demo.model.SaleAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaledRepository extends JpaRepository<SaleAd , Long> {
    SaleAd findById(long adId);

    @Query("SELECT u.ad_type , MAX(u.sales_price) FROM SaleAd u GROUP BY u.ad_type")
    List<SaleAd> findAllPricesByAd_type(@Param("ad_type") String ad_type);

}
