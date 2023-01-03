package be.rubus.microstream.spring.cache.service;

import be.rubus.microstream.spring.cache.model.Country;
import be.rubus.microstream.spring.cache.model.CountryBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CountryService {

    @Cacheable("countries")
    public List<Country> getCountries() {
        List<Country> result = new ArrayList<>();
        result.add(new CountryBuilder().withCode("BE").withName("Belgium").build());
        result.add(new CountryBuilder().withCode("DE").withName("Germany").build());
        result.add(new CountryBuilder().withCode("NL").withName("Netherlands").build());
        result.add(new CountryBuilder().withCode("FR").withName("France").build());
        result.add(new CountryBuilder().withCode("NOW").withName(new Date().toString()).build());
        return result;
    }

    @CacheEvict("countries")
    public void resetCountries() {
        // In this demo, we don't need to do anything.
    }
}
