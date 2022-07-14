package parkinglot.validation;

import java.util.Objects;

import javax.print.attribute.standard.JobKOctets;

import parkinglot.exception.ValidationException;

public class Validator {

    public static <T> T validateNotNull(T obj) {
        if (obj == null) {
            throw new ValidationException("Given objects is null!");
        }
        return obj;
    }

}
