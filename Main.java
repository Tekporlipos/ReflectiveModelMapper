package com.gigacapstone.billingservice.dto.mappers;

import com.gigacapstone.billingservice.dto.InternetPackageDTO;
import com.gigacapstone.billingservice.dto.TariffPlanDTO;
import com.gigacapstone.billingservice.enums.ExpirationRate;
import com.gigacapstone.billingservice.model.InternetPackage;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

//@Component
public class ModelMapper {

    public <T, U> U mapToEntityOrDto(T source, Class<U> destinationClass) {
        U destination = null;
        if (source!= null && destinationClass!= null){
            try {
                Constructor<U> destinationConstructor = destinationClass.getDeclaredConstructor();
                destination = destinationConstructor.newInstance();
                Field[] sourceFields = source.getClass().getDeclaredFields();
                Field[] destinationFields = destinationClass.getDeclaredFields();
                for (Field sourceField : sourceFields) {
                    sourceField.setAccessible(true);
                    Object sourceValue = sourceField.get(source);
                    for (Field destinationField : destinationFields) {
                        destinationField.setAccessible(true);
                        if (sourceField.getName().equals(destinationField.getName()) && sourceValue != null) {
                            String name = sourceField.getType().getPackage().getName();
                            if (name.split("[.]")[0].
                                    equals(destinationClass.getPackage().
                                            getName().split("[.]")[0])){
                                try {
                                    Class<?> type = destinationField.getType();
                                    type.getDeclaredConstructor();
                                    sourceValue = mapToEntityOrDto(sourceValue, type);
                                }catch (NoSuchMethodException e){
                                    System.out.println(e);
                                }
                            }
                            destinationField.set(destination, sourceValue);
                            break;
                        }
                    }
                }
            } catch (NoSuchMethodException
                     | IllegalAccessException
                     | InstantiationException
                     | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        return destination;
    }
}

class Main {
    public static void main(String[] args) {
        ModelMapper modelMapper = new ModelMapper();
        TariffPlanDTO tariffPlanDTO = new TariffPlanDTO();
        tariffPlanDTO.setName("tekporlipos");
        tariffPlanDTO.setPrice(30.0);
        tariffPlanDTO.setVatPercentage(40);
        tariffPlanDTO.setIsEnabled(true);
        tariffPlanDTO.setIsVatApplied(true);
        tariffPlanDTO.setExpirationRate(ExpirationRate.PERMANENT);

        InternetPackageDTO internetPackageDTO = new InternetPackageDTO();
        internetPackageDTO.setUploadSpeed(30.0);
        internetPackageDTO.setDownloadSpeed(50.2);
        internetPackageDTO.setDataSize(70.2);
        internetPackageDTO.setUploadSpeed(30.5);
        internetPackageDTO.setTariffPlan(tariffPlanDTO);


        InternetPackage internetPackage = modelMapper.mapToEntityOrDto(internetPackageDTO, InternetPackage.class);
        System.out.println(internetPackage);

    }
}

