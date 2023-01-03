package be.rubus.microstream.spring.cache.controller;

import be.rubus.microstream.spring.cache.model.Country;
import be.rubus.microstream.spring.cache.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/country")
    public List<Country> getCountryList() {
        return countryService.getCountries();
    }

    @GetMapping("/country/reset")
    public void resetCountryList() {
        countryService.resetCountries();
    }
}
