package com.example.user.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;


//@Data
//@NoArgsConstructor
@Embeddable
public class Phone {
    private String number;
    @JsonProperty("citycode")
    private String cityCode;
    @JsonProperty("contrycode")
    private String countryCode;

    public Phone() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
