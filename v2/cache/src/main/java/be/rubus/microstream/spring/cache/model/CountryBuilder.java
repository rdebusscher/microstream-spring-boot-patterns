package be.rubus.microstream.spring.cache.model;

public class CountryBuilder {
    private String code;
    private String name;

    public CountryBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public CountryBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Country build() {
        return new Country(code, name);
    }
}