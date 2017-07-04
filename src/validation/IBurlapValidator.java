package validation;

/**
 * Created by edwardwilliams on 6/30/17.
 * Interface for burlap validator.
 */


public interface IBurlapValidator<LABEL, MR> {
    boolean isValid(LABEL var2, MR var1);
}

